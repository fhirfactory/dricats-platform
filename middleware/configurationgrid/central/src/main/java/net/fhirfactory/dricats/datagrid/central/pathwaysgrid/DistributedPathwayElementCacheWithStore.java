package net.fhirfactory.dricats.datagrid.central.pathwaysgrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.datagrid.central.pathwaysgrid.spi.IPathwayElementPersistenceService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Distributed Infinispan cache for PathwayElement with pluggable persistence (H2 implementation provided separately).
 */
@ApplicationScoped
public class DistributedPathwayElementCacheWithStore {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedPathwayElementCacheWithStore.class);

    public static final String DEFAULT_CACHE_NAME = "PathwayElementCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "PathwayElementLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, PathwayElement> cache;
    private Cache<String, String> loadRequestCache;
    private final List<Object> registeredListeners = new ArrayList<>();

    @Inject
    private Instance<IPathwayElementPersistenceService> persistenceServiceInstance;

    @PostConstruct
    public void start() {
        try {
            String clusterName = System.getProperty("pathway.element.store.cluster.name", "dricats-pathway-element-cluster");
            String nodeName = System.getProperty("pathway.element.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("pathway.element.store.jgroups.config");
            String jgroupsStack = System.getProperty("pathway.element.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("PathwayElementCache: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("PathwayElementCache: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder base = new ConfigurationBuilder();
            String cacheModeProp = System.getProperty("pathway.element.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("pathway.element.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            String cacheName = System.getProperty("pathway.element.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.cache = this.cacheManager.getCache(cacheName);
            LOG.info("PathwayElementCache: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("pathway.element.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("pathway.element.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("PathwayElementCache: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

            if (persistence().isPresent()) {
                LOG.info("PathwayElementCache: Custom persistence provider detected: {}",
                        persistence().get().getClass().getName());
            } else {
                LOG.warn("PathwayElementCache: No custom persistence provider found. Events will not be durably persisted unless a store is provided.");
            }

            registerClusteredPersistenceListener(cacheName, this.cache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);

        } catch (Exception e) {
            LOG.error("Failed to start DistributedPathwayElementCacheWithStore", e);
            throw new RuntimeException("Failed to start DistributedPathwayElementCacheWithStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        // remove listeners
        for (Object l : registeredListeners) {
            try {
                cache.removeListener(l);
            } catch (Exception ignore) {}
        }
        registeredListeners.clear();
        try {
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception ignore) {}
    }

    public void put(PathwayElement item) {
        String key = resolveKey(item);
        cache.put(key, item);
    }

    public PathwayElement get(String key) {
        PathwayElement v = cache.get(key);
        if (v == null) {
            // trigger a clustered load from persistence service if present
            try {
                loadRequestCache.put(key, "REQ");
            } catch (Exception ex) {
                LOG.debug("PathwayElementCache: failed to send load request for key={}", key, ex);
            }
            v = cache.get(key);
        }
        return v;
    }

    public PathwayElement remove(String key) {
        return cache.remove(key);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public boolean containsOrLoad(String key) {
        if (cache.containsKey(key)) {
            return true;
        }
        Optional<IPathwayElementPersistenceService> ps = persistence();
        if (ps.isPresent()) {
            try {
                Optional<PathwayElement> loaded = ps.get().load(key);
                loaded.ifPresent(v -> cache.put(key, v));
                return loaded.isPresent();
            } catch (Exception ex) {
                LOG.debug("PathwayElementCache: persistence load failed for key={}", key, ex);
            }
        }
        return false;
    }

    protected Optional<IPathwayElementPersistenceService> persistence() {
        try {
            if (persistenceServiceInstance != null && !persistenceServiceInstance.isUnsatisfied()) {
                return Optional.ofNullable(persistenceServiceInstance.get());
            }
        } catch (Exception e) {
            // ignore
        }
        return Optional.empty();
    }

    protected void registerClusteredPersistenceListener(String cacheName, Cache<String, PathwayElement> cache) {
        ClusteredPersistenceListener listener = new ClusteredPersistenceListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        LoadRequestListener listener = new LoadRequestListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class ClusteredPersistenceListener {
        private final String cacheName;
        ClusteredPersistenceListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, PathwayElement> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, PathwayElement> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, PathwayElement> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
                    } catch (Exception ex) {
                        LOG.debug("Listener[{}]: delete failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, PathwayElement> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
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

    protected String resolveKey(PathwayElement item) {
        String key = null;
        try {
            DistributableObjectId objectId = item.getObjectID();
            if (objectId != null && objectId.getQualifiedName() != null && objectId.getQualifiedName().getCommonName() != null && !objectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
                key = objectId.getQualifiedName().getCommonName().getValue();
            }
        } catch (Exception e) {
            // ignore
        }
        if (key == null) {
            try {
                CommonName cn = item.getId();
                if (cn != null && cn.getValue() != null && !cn.getValue().isEmpty()) {
                    key = cn.getValue();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        if (key == null) {
            key = UUID.randomUUID().toString();
            try {
                item.setId(new CommonName(key));
            } catch (Exception e) {
                LOG.debug("resolveKey(PathwayElement): unable to set generated id on item", e);
            }
        }
        return key;
    }

    public Optional<Cache<String, PathwayElement>> getCache() {
        return Optional.ofNullable(cache);
    }
}
