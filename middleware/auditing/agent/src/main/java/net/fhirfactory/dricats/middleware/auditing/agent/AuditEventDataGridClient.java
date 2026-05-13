/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.middleware.auditing.agent;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.audit.implementation.ApplicationAuditEvent;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Infinispan client/peer for the ApplicationAuditEvent cache. This client does NOT configure persistence,
 * it simply joins the same cluster as the server nodes and interacts with the cache.
 *
 * Configuration via system properties (same namespace used by the server to simplify deployments):
 * - dricats.audit.grid.cluster.name (default: dricats-audit-cluster)
 * - dricats.audit.grid.node.name
 * - dricats.audit.grid.jgroups.config
 * - dricats.audit.grid.jgroups.stack
 * - dricats.audit.grid.cache.name (default: ApplicationAuditEventCache)
 * - dricats.audit.grid.cache.mode (dist|repl, default: dist)
 * - dricats.audit.grid.cache.owners (default: 2 when dist)
 */
@ApplicationScoped
public class AuditEventDataGridClient {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEventDataGridClient.class);

    public static final String DEFAULT_CACHE_NAME = "ApplicationAuditEventCache";

    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationAuditEvent> cache;

    @PostConstruct
    public void start() {
        try {
            String clusterName = System.getProperty("dricats.audit.grid.cluster.name", "dricats-audit-cluster");
            String nodeName = System.getProperty("dricats.audit.grid.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isBlank()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("dricats.audit.grid.jgroups.config");
            String jgroupsStack = System.getProperty("dricats.audit.grid.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isBlank()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isBlank()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("AuditEventDataGridClient: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("AuditEventDataGridClient: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder builder = new ConfigurationBuilder();

            String cacheModeProp = System.getProperty("dricats.audit.grid.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("dricats.audit.grid.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                builder.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                builder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            String cacheName = System.getProperty("dricats.audit.grid.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, builder.build());
            this.cache = this.cacheManager.getCache(cacheName);

            LOG.info("AuditEventDataGridClient: cache='{}' ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);
        } catch (Exception e) {
            LOG.error("Failed to start AuditEventDataGridClient", e);
            throw new RuntimeException("Failed to start AuditEventDataGridClient", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (cacheManager != null) {
            try {
                cacheManager.stop();
            } catch (Exception e) {
                LOG.warn("Error stopping AuditEventDataGridClient", e);
            }
        }
    }

    /**
     * Add the audit event to the cache and return immediately without waiting for any confirmation
     * from the cluster or the underlying persistence on server nodes.
     */
    public void addAndContinue(ApplicationAuditEvent event) {
        String key = resolveKey(event);
        try {
            cache.putAsync(key, event);
        } catch (Exception e) {
            LOG.warn("addAndContinue: failed to enqueue put for key={}", key, e);
        }
    }

    /**
     * Add the audit event to the cache and wait for the operation to complete. When server nodes
     * are configured with synchronous write-through persistence, completion indicates the entry has
     * been written to the cache store.
     *
     * @param event the audit event to persist
     * @param timeoutMs optional timeout in milliseconds to wait for completion (null means no explicit timeout)
     * @return true if completed successfully within timeout; false if timed out
     */
    public boolean addAndWait(ApplicationAuditEvent event, Long timeoutMs) {
        String key = resolveKey(event);
        try {
            CompletableFuture<ApplicationAuditEvent> f = cache.putAsync(key, event).toCompletableFuture();
            if (timeoutMs == null || timeoutMs <= 0) {
                f.get();
                return true;
            } else {
                return f.completeOnTimeout(null, timeoutMs, TimeUnit.MILLISECONDS).get() != null || f.isDone();
            }
        } catch (Exception e) {
            LOG.error("addAndWait: failed for key={}", key, e);
            return false;
        }
    }

    public Cache<String, ApplicationAuditEvent> getCache() {
        return cache;
    }

    private String resolveKey(ApplicationAuditEvent event) {
        if (event == null) {
            return UUID.randomUUID().toString();
        }
        String k = null;
        try {
            // SimpleElementBase provides resolveKey() via inheritance chain
            k = event.resolveElementInstanceKey();
        } catch (Exception ignore) {
            // ignore
        }
        if (k == null || k.isBlank()) {
            k = UUID.randomUUID().toString();
        }
        return k;
    }
}
