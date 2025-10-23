/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.datagrid.central.topologygrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.datagrid.central.topologygrid.spi.IApplicationComponentPersistenceService;
import net.fhirfactory.dricats.datagrid.central.topologygrid.spi.IInterfaceComponentPersistenceService;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary;
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
import java.util.concurrent.TimeUnit;

/**
 * Persistent Infinispan cache for ApplicationComponentSummary resources with H2-backed JDBC store.
 *
 * This cache joins an Infinispan cluster and persists entries to an embedded H2 database using
 * the JDBC String-Based Store. A small replicated cache is used for load requests so that store
 * nodes can trigger read-through loads from the JDBC store when a client requests a missing key.
 */
@ApplicationScoped
public class DistributedApplicationComponentCacheWithStore {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedApplicationComponentCacheWithStore.class);

    public static final String DEFAULT_CACHE_NAME = "ApplicationComponentCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "ApplicationComponentLoadRequests";
    public static final String DEFAULT_INTERFACE_CACHE_NAME = "InterfaceComponentCache";
    public static final String DEFAULT_INTERFACE_LOAD_REQUEST_CACHE_NAME = "InterfaceComponentLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationComponentSummary> cache;
    private Cache<String, String> loadRequestCache;
    private Cache<String, InterfaceComponentSummary> interfaceCache;
    private Cache<String, String> interfaceLoadRequestCache;
    private final List<Object> registeredListeners = new ArrayList<>();

    @Inject
    private Instance<IApplicationComponentPersistenceService> persistenceServiceInstance;
    @Inject
    private Instance<IInterfaceComponentPersistenceService> interfacePersistenceServiceInstance;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting ApplicationComponentInfinispanPersistentStore (clustered, persistent H2)...");
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
                LOG.info("ApplicationComponentInfinispanPersistentStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("ApplicationComponentInfinispanPersistentStore: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            // Base clustered cache config
            ConfigurationBuilder base = new ConfigurationBuilder();
            String cacheModeProp = System.getProperty("application.component.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("application.component.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            // No built-in Infinispan store configured here. Persistence is delegated to a CDI SPI
            // (IApplicationComponentPersistenceService), see listeners below.

            String cacheName = System.getProperty("application.component.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.cache = this.cacheManager.getCache(cacheName);
            LOG.info("ApplicationComponentInfinispanPersistentStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            // Replicated short-lived load request cache
            String reqCacheName = System.getProperty("application.component.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("application.component.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("ApplicationComponentInfinispanPersistentStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);

            // Interface component cache
            String ifCacheName = System.getProperty("application.interface.store.cache.name", DEFAULT_INTERFACE_CACHE_NAME);
            this.cacheManager.defineConfiguration(ifCacheName, base.build());
            this.interfaceCache = this.cacheManager.getCache(ifCacheName);
            LOG.info("ApplicationComponentInfinispanPersistentStore: interface cache={} ready in cluster='{}'", ifCacheName, clusterName);

            // Interface load request cache
            String ifReqCacheName = System.getProperty("application.interface.store.load.request.cache.name", DEFAULT_INTERFACE_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder ifReqCfg = new ConfigurationBuilder();
            ifReqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ifTtlMs = Long.getLong("application.interface.store.load.request.ttl.ms", 60_000L);
            ifReqCfg.expiration().lifespan(ifTtlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(ifReqCacheName, ifReqCfg.build());
            this.interfaceLoadRequestCache = this.cacheManager.getCache(ifReqCacheName);
            LOG.info("ApplicationComponentInfinispanPersistentStore: interface load-request cache={} ready (ttlMs={})", ifReqCacheName, ifTtlMs);

            // Register listeners
            registerClusteredPersistenceListener(cacheName, this.cache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);
            registerInterfaceClusteredPersistenceListener(ifCacheName, this.interfaceCache);
            registerInterfaceLoadRequestListener(ifReqCacheName, this.interfaceLoadRequestCache);

            boolean bootstrapPersistAll = Boolean.parseBoolean(System.getProperty("application.component.store.persist.bootstrap", "true"));
            if (bootstrapPersistAll) {
                try {
                    bootstrapPersistAll(this.cache);
                } catch (Exception ex) {
                    LOG.warn("ApplicationComponentInfinispanPersistentStore: bootstrap persistence failed", ex);
                }
            }
            boolean ifBootstrapPersistAll = Boolean.parseBoolean(System.getProperty("application.interface.store.persist.bootstrap", "true"));
            if (ifBootstrapPersistAll) {
                try {
                    bootstrapInterfacePersistAll(this.interfaceCache);
                } catch (Exception ex) {
                    LOG.warn("ApplicationComponentInfinispanPersistentStore: interface bootstrap persistence failed", ex);
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to start ApplicationComponentInfinispanPersistentStore", e);
            throw new RuntimeException("Failed to start ApplicationComponentInfinispanPersistentStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping ApplicationComponentInfinispanPersistentStore...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (interfaceCache != null) {
                interfaceCache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping ApplicationComponentInfinispanPersistentStore", e);
        }
        LOG.info("ApplicationComponentInfinispanPersistentStore stopped");
    }

    protected Optional<IApplicationComponentPersistenceService> persistence() {
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

    protected void registerClusteredPersistenceListener(String cacheName, Cache<String, ApplicationComponentSummary> cache) {
        ClusteredPersistenceListener listener = new ClusteredPersistenceListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        LoadRequestListener listener = new LoadRequestListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void bootstrapPersistAll(Cache<String, ApplicationComponentSummary> cache) {
        Optional<IApplicationComponentPersistenceService> ps = persistence();
        if (ps.isEmpty()) { return; }
        cache.forEach((k, v) -> {
            try { ps.get().save(k, v); } catch (Exception ex) { LOG.debug("bootstrapPersistAll: save failed for key={}", k, ex); }
        });
    }

    protected Optional<IInterfaceComponentPersistenceService> interfacePersistence() {
        if (interfacePersistenceServiceInstance == null) {
            return Optional.empty();
        }
        try {
            if (interfacePersistenceServiceInstance.isResolvable()) {
                return Optional.ofNullable(interfacePersistenceServiceInstance.get());
            }
        } catch (Exception e) {
            LOG.debug("interfacePersistence(): unable to resolve provider", e);
        }
        return Optional.empty();
    }

    protected void registerInterfaceClusteredPersistenceListener(String cacheName, Cache<String, InterfaceComponentSummary> cache) {
        InterfaceClusteredPersistenceListener listener = new InterfaceClusteredPersistenceListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void registerInterfaceLoadRequestListener(String cacheName, Cache<String, String> cache) {
        InterfaceLoadRequestListener listener = new InterfaceLoadRequestListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void bootstrapInterfacePersistAll(Cache<String, InterfaceComponentSummary> cache) {
        Optional<IInterfaceComponentPersistenceService> ps = interfacePersistence();
        if (ps.isEmpty()) { return; }
        cache.forEach((k, v) -> {
            try { ps.get().save(k, v); } catch (Exception ex) { LOG.debug("bootstrapInterfacePersistAll: save failed for key={}", k, ex); }
        });
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class ClusteredPersistenceListener {
        private final String cacheName;
        ClusteredPersistenceListener(String cacheName) { this.cacheName = cacheName; }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> { try { ps.save(e.getKey(), e.getValue()); LOG.trace("Listener[{}]: create key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> { try { ps.save(e.getKey(), e.getValue()); LOG.trace("Listener[{}]: modify key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> { try { ps.delete(e.getKey()); LOG.trace("Listener[{}]: remove key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("Listener[{}]: delete failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, ApplicationComponentSummary> e) {
            if (!e.isPre()) {
                persistence().ifPresent(ps -> { try { ps.delete(e.getKey()); LOG.trace("Listener[{}]: expire key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("Listener[{}]: expire-delete failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class LoadRequestListener {
        private final String cacheName;
        LoadRequestListener(String cacheName) { this.cacheName = cacheName; }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, String> e) {
            if (e.isPre()) { return; }
            String key = e.getKey();
            Optional<IApplicationComponentPersistenceService> ps = persistence();
            if (ps.isEmpty()) { return; }
            try {
                Optional<ApplicationComponentSummary> loaded = ps.get().load(key);
                loaded.ifPresent(v -> cache.put(key, v));
                if (loaded.isPresent()) {
                    LOG.info("LoadRequest[{}]: loaded and cached key={}", cacheName, key);
                } else {
                    LOG.debug("LoadRequest[{}]: no result for key={}", cacheName, key);
                }
            } catch (Exception ex) {
                LOG.debug("LoadRequest[{}]: failed to load key={}", cacheName, key, ex);
            }
        }
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class InterfaceClusteredPersistenceListener {
        private final String cacheName;
        InterfaceClusteredPersistenceListener(String cacheName) { this.cacheName = cacheName; }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, InterfaceComponentSummary> e) {
            if (!e.isPre()) {
                interfacePersistence().ifPresent(ps -> { try { ps.save(e.getKey(), e.getValue()); LOG.trace("IF Listener[{}]: create key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("IF Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, InterfaceComponentSummary> e) {
            if (!e.isPre()) {
                interfacePersistence().ifPresent(ps -> { try { ps.save(e.getKey(), e.getValue()); LOG.trace("IF Listener[{}]: modify key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("IF Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, InterfaceComponentSummary> e) {
            if (!e.isPre()) {
                interfacePersistence().ifPresent(ps -> { try { ps.delete(e.getKey()); LOG.trace("IF Listener[{}]: remove key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("IF Listener[{}]: delete failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, InterfaceComponentSummary> e) {
            if (!e.isPre()) {
                interfacePersistence().ifPresent(ps -> { try { ps.delete(e.getKey()); LOG.trace("IF Listener[{}]: expire key={}", cacheName, e.getKey()); } catch (Exception ex) { LOG.debug("IF Listener[{}]: expire-delete failed for key={}", cacheName, e.getKey(), ex); } });
            }
        }
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class InterfaceLoadRequestListener {
        private final String cacheName;
        InterfaceLoadRequestListener(String cacheName) { this.cacheName = cacheName; }

        @CacheEntryCreated
        public void onCreated(CacheEntryEvent<String, String> e) {
            if (e.isPre()) { return; }
            String key = e.getKey();
            Optional<IInterfaceComponentPersistenceService> ps = interfacePersistence();
            if (ps.isEmpty()) { return; }
            try {
                Optional<InterfaceComponentSummary> loaded = ps.get().load(key);
                loaded.ifPresent(v -> interfaceCache.put(key, v));
                if (loaded.isPresent()) {
                    LOG.info("IF LoadRequest[{}]: loaded and cached key={}", cacheName, key);
                } else {
                    LOG.debug("IF LoadRequest[{}]: no result for key={}", cacheName, key);
                }
            } catch (Exception ex) {
                LOG.debug("IF LoadRequest[{}]: failed to load key={}", cacheName, key, ex);
            }
        }
    }

    public Optional<Cache<String, ApplicationComponentSummary>> getCache() { return Optional.ofNullable(cache); }
    public Optional<Cache<String, String>> getLoadRequestCache() { return Optional.ofNullable(loadRequestCache); }
}
