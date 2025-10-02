package net.fhirfactory.dricats.datagrid.central.topologygrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
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

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Central distributed cache with persistence for ApplicationComponentSummary entries.
 *
 * This mirrors the pattern used by DistributedMessageSubscriptionCacheWithStore
 * but targets ApplicationComponentSummary and persists via ApplicationComponentRepository.
 */
@ApplicationScoped
public class DistributedApplicationComponentCacheWithStore {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedApplicationComponentCacheWithStore.class);

    public static final String DEFAULT_CACHE_NAME = "ApplicationComponentCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "ApplicationComponentLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationComponentSummary> cache;
    private Cache<String, String> loadRequestCache;

    @Inject
    Instance<InMemoryApplicationComponentSummaryStore> repoProvider;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting DistributedApplicationComponentCacheWithStore (clustered, store node)...");
            String clusterName = System.getProperty("application.component.store.cluster.name", "dricats-application-component-cluster");
            String nodeName = System.getProperty("application.component.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("application.component.store.jgroups.config");
            String jgroupsStack = System.getProperty("application.component.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("DistributedApplicationComponentCacheWithStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("DistributedApplicationComponentCacheWithStore: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder base = new ConfigurationBuilder();
            String cacheModeProp = System.getProperty("application.component.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("application.component.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            String cacheName = System.getProperty("application.component.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.cache = this.cacheManager.getCache(cacheName);
            LOG.info("DistributedApplicationComponentCacheWithStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("application.component.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("application.component.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("DistributedApplicationComponentCacheWithStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

            registerClusteredPersistenceListener(cacheName, this.cache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);
        } catch (Exception e) {
            LOG.error("Failed to start DistributedApplicationComponentCacheWithStore", e);
            throw new RuntimeException("Failed to start DistributedApplicationComponentCacheWithStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping DistributedApplicationComponentCacheWithStore...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping DistributedApplicationComponentCacheWithStore", e);
        }
        LOG.info("DistributedApplicationComponentCacheWithStore stopped");
    }

    public void put(ApplicationComponentSummary item) {
        String key = item.resolveKey();
        cache.put(key, item);
    }

    public ApplicationComponentSummary get(String key) {
        return cache.get(key);
    }

    public ApplicationComponentSummary remove(String key) {
        return cache.remove(key);
    }

    public boolean contains(String key) { return cache.containsKey(key); }

    public boolean containsOrLoad(String key) {
        if (contains(key)) { return true; }
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
        } catch (Exception ex) {
            LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    protected Optional<InMemoryApplicationComponentSummaryStore> repository() {
        if (repoProvider == null) { return Optional.empty(); }
        try {
            InMemoryApplicationComponentSummaryStore repo = repoProvider.get();
            return Optional.ofNullable(repo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected void registerClusteredPersistenceListener(String cacheName, Cache<String, ApplicationComponentSummary> cache) {
        try {
            cache.addListener(new ClusteredPersistenceListener(cacheName));
            LOG.info("Registered ClusteredPersistenceListener for cache={}", cacheName);
        } catch (Exception e) {
            LOG.warn("Unable to register ClusteredPersistenceListener for cache={}", cacheName, e);
        }
    }

    protected void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        try {
            cache.addListener(new LoadRequestListener(cacheName));
            LOG.info("Registered LoadRequestListener for cache={}", cacheName);
        } catch (Exception e) {
            LOG.warn("Unable to register LoadRequestListener for cache={}", cacheName, e);
        }
    }

    @Listener(observation= Listener.Observation.POST)
    public class ClusteredPersistenceListener {
        private final String cacheName;
        public ClusteredPersistenceListener(String cacheName) { this.cacheName = cacheName; }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                repository().ifPresent(repo -> {
                    ApplicationComponentSummary value = e.getValue();
                    if (value != null) {
                        repo.add(value);
                        LOG.debug("[{}] persisted CREATE key={} value={}", cacheName, e.getKey(), value);
                    }
                });
            }
        }

        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                repository().ifPresent(repo -> {
                    ApplicationComponentSummary value = e.getValue();
                    if (value != null) {
                        repo.add(value); // upsert
                        LOG.debug("[{}] persisted MODIFY key={} value={}", cacheName, e.getKey(), value);
                    }
                });
            }
        }

        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                repository().ifPresent(repo -> {
                    String key = e.getKey();
                    try {
                        for (ApplicationComponentSummary ac : repo.getAll()) {
                            if (key.equals(ac.resolveKey())) {
                                repo.remove(ac);
                                LOG.debug("[{}] persisted REMOVE key={} value={}", cacheName, key, ac);
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        LOG.warn("[{}] persisted REMOVE failed for key={}", cacheName, key, ex);
                    }
                });
            }
        }

        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                repository().ifPresent(repo -> {
                    String key = e.getKey();
                    try {
                        for (ApplicationComponentSummary ac : repo.getAll()) {
                            if (key.equals(ac.resolveKey())) {
                                repo.remove(ac);
                                LOG.debug("[{}] persisted EXPIRE key={} value={}", cacheName, key, ac);
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        LOG.warn("[{}] persisted EXPIRE failed for key={}", cacheName, key, ex);
                    }
                });
            }
        }
    }

    @Listener(observation= Listener.Observation.POST)
    public class LoadRequestListener {
        private final String cacheName;
        public LoadRequestListener(String cacheName) { this.cacheName = cacheName; }

        @org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
        @org.infinispan.notifications.cachelistener.annotation.CacheEntryModified
        public void onRequest(CacheEntryEvent<String, String> e) {
            if (e.isPre()) { return; }
            String key = e.getKey();
            if (key == null) { return; }
            // Attempt to load from repository and populate
            repository().ifPresent(repo -> {
                // There is no direct lookup by string key; try to reconstruct DistributableObjectId from key if possible
                // For now, attempt a simple linear scan of repo.getAll() and match by element id/name.
                try {
                    for (ApplicationComponentSummary ac : repo.getAll()) {
                        String candidate = ac.resolveKey();
                        if (key.equals(candidate)) {
                            cache.put(key, ac);
                            LOG.info("[{}] LoadRequestListener: populated key={} from repository", cacheName, key);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    LOG.warn("[{}] LoadRequestListener: error loading key={} from repository", cacheName, key, ex);
                }
            });
        }
    }

    public Optional<Cache<String, ApplicationComponentSummary>> getCache() { return Optional.ofNullable(cache); }
}
