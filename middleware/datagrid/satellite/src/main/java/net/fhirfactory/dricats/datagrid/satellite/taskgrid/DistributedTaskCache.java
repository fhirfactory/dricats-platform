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
package net.fhirfactory.dricats.datagrid.satellite.taskgrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.tasking.InternalTask;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A minimal Infinispan-backed store for IAdministrativeTask resources.
 * Provides simple lifecycle and CRUD-style operations.
 */
@ApplicationScoped
public class DistributedTaskCache {
    // Housekeeping
    private static final Logger LOG = LoggerFactory.getLogger(DistributedTaskCache.class);

    // Constants
    public static final String DEFAULT_CACHE_NAME = "TaskResourceCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "TaskResourceLoadRequests";

    // Members
    private DefaultCacheManager cacheManager;
    private Cache<String, InternalTask> taskCache;
    private Cache<String, String> loadRequestCache;

    // Lifecycle
    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting TaskInfinispanStore (clustered)...");
            String clusterName = System.getProperty("task.store.cluster.name", "dricats-task-cluster");
            String nodeName = System.getProperty("task.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            // Optional custom JGroups configuration for cross-application clustering
            String jgroupsConfig = System.getProperty("task.store.jgroups.config");
            String jgroupsStack = System.getProperty("task.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("TaskInfinispanStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("TaskInfinispanStore: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            // Build common cache configuration (clustered) and topology
            ConfigurationBuilder base = new ConfigurationBuilder();

            // Cache mode: distributed (recommended for many nodes) or replicated
            String cacheModeProp = System.getProperty("task.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("task.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            // Define and start single cache
            String cacheName = System.getProperty("task.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.taskCache = this.cacheManager.getCache(cacheName);
            LOG.info("TaskInfinispanStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            // Define and start load-request cache (replicated, short-lived entries)
            String reqCacheName = System.getProperty("task.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("task.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("TaskInfinispanStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

        } catch (Exception e) {
            LOG.error("Failed to start TaskInfinispanStore", e);
            throw new RuntimeException("Failed to start TaskInfinispanStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping TaskInfinispanStore...");
        try {
            if (taskCache != null) {
                taskCache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping TaskInfinispanStore", e);
        }
        LOG.info("TaskInfinispanStore stopped");
    }

    // CRUD-like operations
    public void put(InternalTask task) {
        if (task == null) {
            LOG.warn("put(IAdministrativeTask): task is null, ignoring");
            return;
        }
        String key = resolveKey(task);
        taskCache.put(key, task);
        LOG.debug("put(IAdministrativeTask): stored task with key={}", key);
    }

    public InternalTask get(String key) {
        if (key == null || key.isEmpty()) {
            LOG.debug("get(String): key is null/empty, returning null");
            return null;
        }
        InternalTask task = taskCache.get(key);
        LOG.debug("get(String): key={}, found={}", key, task != null);
        return task;
    }

    /**
     * Immediate check without any waiting. Returns the entry if present in cache, else empty.
     */
    public Optional<InternalTask> getIfPresent(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(taskCache.get(key));
    }

    /**
     * Wait for a task to appear in the distributed cache up to the given timeout.
     * This is useful when a store node is currently loading the resource from the persistent store
     * and will publish it into the cache shortly. This method performs periodic, light-weight checks
     * without generating any load on the store.
     *
     * @param key cache key
     * @param timeoutMillis maximum time to wait (ms)
     * @param pollIntervalMillis interval between checks (ms)
     * @return Optional present if the task appeared within the timeout
     */
    public Optional<InternalTask> waitFor(String key, long timeoutMillis, long pollIntervalMillis) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }
        long deadline = System.nanoTime() + (timeoutMillis * 1_000_000L);
        long sleep = Math.max(1L, pollIntervalMillis);
        InternalTask value = taskCache.get(key);
        if (value != null) {
            return Optional.of(value);
        }
        while (System.nanoTime() < deadline) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
            value = taskCache.get(key);
            if (value != null) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * Wait for a task to appear using default poll interval from system property task.store.wait.poll.ms (default 50ms).
     */
    public Optional<InternalTask> waitFor(String key, long timeoutMillis) {
        long poll = Long.getLong("task.store.wait.poll.ms", 50L);
        return waitFor(key, timeoutMillis, poll);
    }

    public InternalTask remove(String key) {
        if (key == null || key.isEmpty()) {
            LOG.debug("remove(String): key is null/empty, returning null");
            return null;
        }
        InternalTask removed = taskCache.remove(key);
        LOG.debug("remove(String): key={}, removed={}", key, removed != null);
        return removed;
    }

    public boolean contains(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return taskCache.containsKey(key);
    }

    /**
     * Publish a load request for the given key so that a store node can load it from the persistence service.
     */
    public void requestLoad(String key) {
        if (key == null || key.isEmpty()) {
            return;
        }
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
            LOG.debug("requestLoad: published request for key={}", key);
        } catch (Exception e) {
            LOG.warn("requestLoad: failed to publish request for key={}", key, e);
        }
    }

    /**
     * Convenience: try fast get; if absent, publish a load request and then wait up to timeout for it to appear.
     */
    public Optional<InternalTask> getOrRequestAndWait(String key, long timeoutMillis) {
        Optional<InternalTask> present = getIfPresent(key);
        if (present.isPresent()) {
            return present;
        }
        requestLoad(key);
        return waitFor(key, timeoutMillis);
    }



    protected String resolveKey(InternalTask task) {
        // Prefer DistributableObjectId.id (CommonName), then IAdministrativeTask.id (CommonName), else generate and set
        String key = task.resolveKey();
        return key;
    }

    // Expose cache for advanced use if needed
    public Optional<Cache<String, InternalTask>> getCache() {
        return Optional.ofNullable(taskCache);
    }
}
