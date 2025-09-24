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
package net.fhirfactory.dricats.datagrid.central.taskgrid;


import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.tasking.InternalTask;
import net.fhirfactory.dricats.datagrid.central.taskgrid.spi.ITaskPersistenceService;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryExpired;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Persistent variant of the Infinispan-backed store for IAdministrativeTask resources.
 *
 * This class is intended to be used by the designated store applications that must
 * persist ALL cache events via a ITaskPersistenceService (custom provider) and/or
 * optional local file-store configuration. It registers clustered listeners so
 * events from the entire cluster are persisted on these nodes.
 */
@ApplicationScoped
public class DistributedTaskCacheWithStore {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedTaskCacheWithStore.class);

    public static final String DEFAULT_CACHE_NAME = "TaskResourceCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "TaskResourceLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, InternalTask> taskCache;
    private Cache<String, String> loadRequestCache;
    private final List<Object> registeredListeners = new ArrayList<>();

    @Inject
    private Instance<ITaskPersistenceService> persistenceServiceInstance;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting TaskInfinispanPersistentStore (clustered, persistent)...");
            String clusterName = System.getProperty("task.store.cluster.name", "dricats-task-cluster");
            String nodeName = System.getProperty("task.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("task.store.jgroups.config");
            String jgroupsStack = System.getProperty("task.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("TaskInfinispanPersistentStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("TaskInfinispanPersistentStore: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder base = new ConfigurationBuilder();

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
            LOG.info("TaskInfinispanPersistentStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            // Define and start load-request cache (replicated, short-lived entries)
            String reqCacheName = System.getProperty("task.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("task.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("TaskInfinispanPersistentStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

            if (persistence().isPresent()) {
                LOG.info("TaskInfinispanPersistentStore: Custom persistence provider detected: {}",
                        persistence().get().getClass().getName());
            } else {
                LOG.warn("TaskInfinispanPersistentStore: No custom persistence provider found. Events will not be durably persisted unless file-store is enabled.");
            }

            // Register clustered listener on the single cache and bootstrap current contents
            registerClusteredPersistenceListener(cacheName, this.taskCache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);
            boolean bootstrapPersistAll = Boolean.parseBoolean(System.getProperty("task.store.persist.bootstrap", "true"));
            if (bootstrapPersistAll) {
                try {
                    bootstrapPersistAll(this.taskCache);
                } catch (Exception ex) {
                    LOG.warn("TaskInfinispanPersistentStore: bootstrap persistence failed", ex);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to start TaskInfinispanPersistentStore", e);
            throw new RuntimeException("Failed to start TaskInfinispanPersistentStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping TaskInfinispanPersistentStore...");
        try {
            for (Object listener : registeredListeners) {
                try {
                    if (taskCache != null) {
                        taskCache.removeListener(listener);
                    }
                } catch (Exception ex) {
                    LOG.debug("Error while removing listener", ex);
                }
            }
            registeredListeners.clear();

            if (taskCache != null) {
                taskCache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping TaskInfinispanPersistentStore", e);
        }
        LOG.info("TaskInfinispanPersistentStore stopped");
    }

    // CRUD with write/read/delete-through when persistence provider is present
    public void put(InternalTask task) {
        if (task == null) {
            LOG.warn("put(InternalTask): task is null, ignoring");
            return;
        }
        String key = resolveKey(task);
        taskCache.put(key, task);
        persistence().ifPresent(p -> {
            try {
                p.save(key, task);
            } catch (Exception ex) {
                LOG.warn("put(InternalTask): persistence save failed for key={}", key, ex);
            }
        });
        LOG.debug("put(InternalTask): stored task with key={}", key);
    }

    public InternalTask get(String key) {
        if (key == null || key.isEmpty()) {
            LOG.debug("get(String): key is null/empty, returning null");
            return null;
        }
        InternalTask task = taskCache.get(key);
        if (task == null) {
            Optional<InternalTask> loaded = persistence().flatMap(p -> {
                try {
                    return p.load(key);
                } catch (Exception ex) {
                    LOG.warn("get(String): persistence load failed for key={}", key, ex);
                    return Optional.empty();
                }
            });
            if (loaded.isPresent()) {
                task = loaded.get();
                taskCache.put(key, task);
                LOG.debug("get(String): key={}, loaded via persistence and cached", key);
            }
        }
        LOG.debug("get(String): key={}, found={}", key, task != null);
        return task;
    }

    public InternalTask remove(String key) {
        if (key == null || key.isEmpty()) {
            LOG.debug("remove(String): key is null/empty, returning null");
            return null;
        }
        InternalTask removed = taskCache.remove(key);
        persistence().ifPresent(p -> {
            try {
                p.delete(key);
            } catch (Exception ex) {
                LOG.warn("remove(String): persistence delete failed for key={}", key, ex);
            }
        });
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
     * Check whether the given key exists in the cache; if not, try to load it from the persistence store
     * and cache it. Returns true if the entry exists after this method (either was present or successfully loaded).
     */
    public boolean containsOrLoad(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        if (taskCache.containsKey(key)) {
            return true;
        }
        Optional<InternalTask> loaded = persistence().flatMap(p -> {
            try {
                return p.load(key);
            } catch (Exception ex) {
                LOG.warn("containsOrLoad(String): persistence load failed for key={}", key, ex);
                return Optional.empty();
            }
        });
        if (loaded.isPresent()) {
            try {
                taskCache.put(key, loaded.get());
            } catch (Exception e) {
                LOG.debug("containsOrLoad(String): failed to cache loaded entry for key={} (will still report true)", key, e);
            }
            return true;
        }
        return false;
    }

    private Optional<ITaskPersistenceService> persistence() {
        try {
            if (persistenceServiceInstance != null && persistenceServiceInstance.isResolvable()) {
                return Optional.ofNullable(persistenceServiceInstance.get());
            }
        } catch (Exception e) {
            LOG.debug("TaskInfinispanPersistentStore: persistence provider not resolvable", e);
        }
        return Optional.empty();
    }

    private void registerClusteredPersistenceListener(String cacheName, Cache<String, InternalTask> cache) {
        try {
            ClusteredPersistenceListener listener = new ClusteredPersistenceListener(cacheName);
            cache.addListener(listener);
            registeredListeners.add(listener);
            LOG.info("TaskInfinispanPersistentStore: Registered clustered persistence listener for cache={}", cacheName);
        } catch (Exception e) {
            LOG.warn("TaskInfinispanPersistentStore: Failed to register clustered listener for cache={}", cacheName, e);
        }
    }

    private void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        try {
            LoadRequestListener listener = new LoadRequestListener(cacheName);
            cache.addListener(listener);
            registeredListeners.add(listener);
            LOG.info("TaskInfinispanPersistentStore: Registered load-request listener for cache={}", cacheName);
        } catch (Exception e) {
            LOG.warn("TaskInfinispanPersistentStore: Failed to register load-request listener for cache={}", cacheName, e);
        }
    }

    private void bootstrapPersistAll(Cache<String, InternalTask> cache) {
        Optional<ITaskPersistenceService> ps = persistence();
        if (!ps.isPresent()) {
            return;
        }
        LOG.info("TaskInfinispanPersistentStore: Bootstrapping persistence of current cache contents...");
        try {
            cache.entrySet().stream().forEach(entry -> {
                try {
                    ps.get().save(entry.getKey(), entry.getValue());
                } catch (Exception ex) {
                    LOG.debug("TaskInfinispanPersistentStore: bootstrap save failed for key={}", entry.getKey(), ex);
                }
            });
            LOG.info("TaskInfinispanPersistentStore: Bootstrap persistence completed");
        } catch (Exception e) {
            LOG.warn("TaskInfinispanPersistentStore: Error during bootstrap persistence", e);
        }
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class ClusteredPersistenceListener {
        private final String cacheName;
        ClusteredPersistenceListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, InternalTask> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                        LOG.trace("Listener[{}]: persisted create key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, InternalTask> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                        LOG.trace("Listener[{}]: persisted modify key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, InternalTask> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
                        LOG.trace("Listener[{}]: persisted delete key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: delete failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, InternalTask> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
                        LOG.trace("Listener[{}]: persisted expire key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: expire-delete failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class LoadRequestListener {
        private final String cacheName;
        LoadRequestListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @CacheEntryCreated
        public void onRequest(CacheEntryEvent<String, String> e) {
            if (e.isPre()) {
                return;
            }
            String key = e.getKey();
            try {
                boolean claimed = false;
                try {
                    claimed = loadRequestCache.replace(key, "REQ", "IN_PROGRESS");
                } catch (Exception ex) {
                    LOG.debug("LoadRequestListener[{}]: claim failed for key={}", cacheName, key, ex);
                }
                if (!claimed) {
                    return; // another store is processing it
                }
                try {
                    boolean ok = containsOrLoad(key);
                    LOG.trace("LoadRequestListener[{}]: processed key={} loaded={} ", cacheName, key, ok);
                } finally {
                    try {
                        loadRequestCache.remove(key);
                    } catch (Exception ex) {
                        LOG.debug("LoadRequestListener[{}]: cleanup remove failed for key={}", cacheName, key, ex);
                    }
                }
            } catch (Exception ex) {
                LOG.debug("LoadRequestListener[{}]: error processing request for key={}", cacheName, key, ex);
            }
        }
    }

    protected String resolveKey(InternalTask task) {
        String key = null;
        try {
            DistributableObjectId objectId = task.getObjectID();
            if (objectId != null && objectId.getId() != null && objectId.getId().getValue() != null && !objectId.getId().getValue().isEmpty()) {
                key = objectId.getId().getValue();
            }
        } catch (Exception e) {
        }
        if (key == null) {
            try {
                CommonName cn = task.getId();
                if (cn != null && cn.getValue() != null && !cn.getValue().isEmpty()) {
                    key = cn.getValue();
                }
            } catch (Exception e) {
            }
        }
        if (key == null) {
            key = UUID.randomUUID().toString();
            try {
                task.setId(new CommonName(key));
            } catch (Exception e) {
                LOG.debug("resolveKey(InternalTask): unable to set generated id on task", e);
            }
        }
        return key;
    }

    public Optional<Cache<String, InternalTask>> getCache() {
        return Optional.ofNullable(taskCache);
    }
}
