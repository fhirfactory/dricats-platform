package net.fhirfactory.dricats.datagrid.central.notificatongrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import net.fhirfactory.dricats.internals.pubsub.notifications.NotificationSubscription;
import net.fhirfactory.dricats.datagrid.central.taskgrid.spi.INotificationSubscriptionPersistenceService;
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

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Persistent variant of the Infinispan-backed store for NotificationSubscription resources.
 */
@ApplicationScoped
public class DistributedNotificationSubscriptionCacheWithStore {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedNotificationSubscriptionCacheWithStore.class);

    public static final String DEFAULT_CACHE_NAME = "NotificationSubscriptionCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "NotificationSubscriptionLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, NotificationSubscription> cache;
    private Cache<String, String> loadRequestCache;
    private final List<Object> registeredListeners = new ArrayList<>();

    @Inject
    private Instance<INotificationSubscriptionPersistenceService> persistenceServiceInstance;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting NotificationSubscriptionInfinispanPersistentStore (clustered, persistent)...");
            String clusterName = System.getProperty("notification.subscription.store.cluster.name", "dricats-notification-subscription-cluster");
            String nodeName = System.getProperty("notification.subscription.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("notification.subscription.store.jgroups.config");
            String jgroupsStack = System.getProperty("notification.subscription.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("NotificationSubscriptionInfinispanPersistentStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("NotificationSubscriptionInfinispanPersistentStore: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder base = new ConfigurationBuilder();

            String cacheModeProp = System.getProperty("notification.subscription.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("notification.subscription.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            String cacheName = System.getProperty("notification.subscription.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.cache = this.cacheManager.getCache(cacheName);
            LOG.info("NotificationSubscriptionInfinispanPersistentStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("notification.subscription.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("notification.subscription.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("NotificationSubscriptionInfinispanPersistentStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

            if (persistence().isPresent()) {
                LOG.info("NotificationSubscriptionInfinispanPersistentStore: Custom persistence provider detected: {}",
                        persistence().get().getClass().getName());
            } else {
                LOG.warn("NotificationSubscriptionInfinispanPersistentStore: No custom persistence provider found. Events will not be durably persisted unless file-store is enabled.");
            }

            registerClusteredPersistenceListener(cacheName, this.cache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);
            boolean bootstrapPersistAll = Boolean.parseBoolean(System.getProperty("notification.subscription.store.persist.bootstrap", "true"));
            if (bootstrapPersistAll) {
                try {
                    bootstrapPersistAll(this.cache);
                } catch (Exception ex) {
                    LOG.warn("NotificationSubscriptionInfinispanPersistentStore: bootstrap persistence failed", ex);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to start NotificationSubscriptionInfinispanPersistentStore", e);
            throw new RuntimeException("Failed to start NotificationSubscriptionInfinispanPersistentStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping NotificationSubscriptionInfinispanPersistentStore...");
        try {
            for (Object listener : registeredListeners) {
                try {
                    if (cache != null) {
                        cache.removeListener(listener);
                    }
                } catch (Exception ex) {
                    LOG.debug("Error while removing listener", ex);
                }
            }
            registeredListeners.clear();

            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping NotificationSubscriptionInfinispanPersistentStore", e);
        }
        LOG.info("NotificationSubscriptionInfinispanPersistentStore stopped");
    }

    public void put(NotificationSubscription item) {
        String key = item.resolveKey();
        cache.put(key, item);
    }

    public NotificationSubscription get(String key) {
        NotificationSubscription existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        containsOrLoad(key);
        return cache.get(key);
    }

    public NotificationSubscription remove(String key) {
        return cache.remove(key);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public boolean containsOrLoad(String key) {
        if (contains(key)) {
            return true;
        }
        Optional<INotificationSubscriptionPersistenceService> ps = persistence();
        if (ps.isPresent()) {
            try {
                Optional<NotificationSubscription> loaded = ps.get().load(key);
                loaded.ifPresent(value -> cache.put(key, value));
                return loaded.isPresent();
            } catch (Exception ex) {
                LOG.debug("containsOrLoad: persistence load failed for key={}", key, ex);
            }
        } else {
            // No persistence provider; ask a store node via request-cache to load
            try {
                loadRequestCache.putIfAbsent(key, "REQ");
            } catch (Exception ex) {
                LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
            }
        }
        return false;
    }

    protected Optional<INotificationSubscriptionPersistenceService> persistence() {
        if (persistenceServiceInstance == null) {
            return Optional.empty();
        }
        try {
            if (persistenceServiceInstance.isResolvable()) {
                return Optional.ofNullable(persistenceServiceInstance.get());
            }
        } catch (Exception e) {
            LOG.debug("persistence(): unable to resolve provider", e);
        }
        return Optional.empty();
    }

    protected void registerClusteredPersistenceListener(String cacheName, Cache<String, NotificationSubscription> cache) {
        ClusteredPersistenceListener listener = new ClusteredPersistenceListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        LoadRequestListener listener = new LoadRequestListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void bootstrapPersistAll(Cache<String, NotificationSubscription> cache) {
        Optional<INotificationSubscriptionPersistenceService> ps = persistence();
        if (ps.isEmpty()) {
            return;
        }
        cache.forEach((k, v) -> {
            try {
                ps.get().save(k, v);
            } catch (Exception ex) {
                LOG.debug("bootstrapPersistAll: failed to save key={}", k, ex);
            }
        });
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class ClusteredPersistenceListener {
        private final String cacheName;
        ClusteredPersistenceListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, NotificationSubscription> e) {
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
        public void onModified(CacheEntryEvent<String, NotificationSubscription> e) {
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
        public void onRemoved(CacheEntryEvent<String, NotificationSubscription> e) {
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
        public void onExpired(CacheEntryEvent<String, NotificationSubscription> e) {
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


    public Optional<Cache<String, NotificationSubscription>> getCache() {
        return Optional.ofNullable(cache);
    }
}
