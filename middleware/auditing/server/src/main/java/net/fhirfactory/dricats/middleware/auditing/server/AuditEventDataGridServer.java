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
package net.fhirfactory.dricats.middleware.auditing.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.audit.implementation.ApplicationAuditEvent;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Infinispan data grid server for ApplicationAuditEvent. Persistence is configured by a dedicated configurator bean.
 *
 * Configuration via system properties (with sensible defaults):
 * - dricats.audit.grid.cluster.name (default: dricats-audit-cluster)
 * - dricats.audit.grid.node.name
 * - dricats.audit.grid.jgroups.config
 * - dricats.audit.grid.jgroups.stack
 * - dricats.audit.grid.cache.name (default: ApplicationAuditEventCache)
 * - dricats.audit.grid.cache.mode (dist|repl, default: dist)
 * - dricats.audit.grid.cache.owners (default: 2 when dist)
 * - dricats.audit.grid.jdbc.url (default: jdbc:h2:file:./data/dricats-auditing;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE)
 * - dricats.audit.grid.jdbc.user (default: sa)
 * - dricats.audit.grid.jdbc.pass (default: empty)
 */
@ApplicationScoped
public class AuditEventDataGridServer {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEventDataGridServer.class);

    public static final String DEFAULT_CACHE_NAME = "ApplicationAuditEventCache";

    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationAuditEvent> cache;

    @Inject
    AuditEventPersistenceConfigurator persistenceConfigurator;

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
                LOG.info("AuditEventDataGridServer: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("AuditEventDataGridServer: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder builder = new ConfigurationBuilder();

            // Clustering mode
            String cacheModeProp = System.getProperty("dricats.audit.grid.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("dricats.audit.grid.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                builder.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                builder.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            // Delegate persistence setup to configurator
            if (persistenceConfigurator != null) {
                persistenceConfigurator.apply(builder);
            }

            String cacheName = System.getProperty("dricats.audit.grid.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, builder.build());
            this.cache = this.cacheManager.getCache(cacheName);

            String jdbcUrlLog = (persistenceConfigurator != null) ? persistenceConfigurator.getJdbcUrl() : "<none>";
            LOG.info("AuditEventDataGridServer: cache='{}' ready in cluster='{}' node='{}' mode='{}' owners={} persisted to H2 {}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners, jdbcUrlLog);
        } catch (Exception e) {
            LOG.error("Failed to start AuditEventDataGridServer", e);
            throw new RuntimeException("Failed to start AuditEventDataGridServer", e);
        }
    }

    @PreDestroy
    public void stop() {
        if (cacheManager != null) {
            try {
                cacheManager.stop();
            } catch (Exception e) {
                LOG.warn("Error stopping AuditEventDataGridServer", e);
            }
        }
    }

    public Cache<String, ApplicationAuditEvent> getCache() {
        return cache;
    }
}
