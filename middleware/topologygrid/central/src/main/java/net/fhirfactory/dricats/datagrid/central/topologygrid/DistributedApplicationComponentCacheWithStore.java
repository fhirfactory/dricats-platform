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
package net.fhirfactory.dricats.datagrid.central.topologygrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.datagrid.central.topologygrid.spi.IApplicationComponentPersistenceService;
import net.fhirfactory.dricats.datagrid.common.topologygrid.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Persistent Infinispan cache for ApplicationComponentSummary resources with H2-backed JDBC store.
 * <p>
 * This cache joins an Infinispan cluster and persists entries to an embedded H2 database using
 * the JDBC String-Based Store. A small replicated cache is used for load requests so that store
 * nodes can trigger read-through loads from the JDBC store when a client requests a missing key.
 */
@ApplicationScoped
public class DistributedApplicationComponentCacheWithStore implements IApplicationComponentCacheClient {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(DistributedApplicationComponentCacheWithStore.class);

    //
    // Constants
    //
    public static final String DEFAULT_CACHE_NAME = "ApplicationComponentCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "ApplicationComponentLoadRequests";

    //
    // Attributes
    //
    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationComponent> cache;
    private Cache<String, String> loadRequestCache;
    private final List<Object> registeredListeners = new ArrayList<>();
    private final AtomicLong changeVersion = new AtomicLong(0);

    @Inject
    private Instance<IApplicationComponentPersistenceService> persistenceServiceInstance;

    //
    // Lifecycle
    //

    @PostConstruct
    public void start() {
        try {
            getLogger().info("Starting ApplicationComponentInfinispanPersistentStore (clustered, persistent H2)...");
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
                getLogger().info("ApplicationComponentInfinispanPersistentStore: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                getLogger().info("ApplicationComponentInfinispanPersistentStore: Using default JGroups transport (no custom config provided)");
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
            getLogger().info("ApplicationComponentInfinispanPersistentStore: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            // Replicated short-lived load request cache
            String reqCacheName = System.getProperty("application.component.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("application.component.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            getLogger().info("ApplicationComponentInfinispanPersistentStore: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);


            // Register listeners
            registerClusteredPersistenceListener(cacheName, this.cache);
            registerLoadRequestListener(reqCacheName, this.loadRequestCache);

            boolean bootstrapPersistAll = Boolean.parseBoolean(System.getProperty("application.component.store.persist.bootstrap", "true"));
            if (bootstrapPersistAll) {
                try {
                    bootstrapPersistAll(this.cache);
                } catch (Exception ex) {
                    getLogger().warn("ApplicationComponentInfinispanPersistentStore: bootstrap persistence failed", ex);
                }
            }
        } catch (Exception e) {
            getLogger().error("Failed to start ApplicationComponentInfinispanPersistentStore", e);
            throw new RuntimeException("Failed to start ApplicationComponentInfinispanPersistentStore", e);
        }
    }

    @PreDestroy
    public void stop() {
        getLogger().info("Stopping ApplicationComponentInfinispanPersistentStore...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            getLogger().warn("Error while stopping ApplicationComponentInfinispanPersistentStore", e);
        }
        getLogger().info("ApplicationComponentInfinispanPersistentStore stopped");
    }

    //
    // Business Methods
    //

    protected Optional<IApplicationComponentPersistenceService> persistence() {
        if (persistenceServiceInstance == null) {
            return Optional.empty();
        }
        try {
            if (persistenceServiceInstance.isResolvable()) {
                return Optional.ofNullable(persistenceServiceInstance.get());
            }
        } catch (Exception e) {
            getLogger().debug("persistence(): unable to resolve provider", e);
        }
        return Optional.empty();
    }

    protected void registerClusteredPersistenceListener(String cacheName, Cache<String, ApplicationComponent> cache) {
        ClusteredPersistenceListener listener = new ClusteredPersistenceListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void registerLoadRequestListener(String cacheName, Cache<String, String> cache) {
        LoadRequestListener listener = new LoadRequestListener(cacheName);
        cache.addListener(listener);
        registeredListeners.add(listener);
    }

    protected void bootstrapPersistAll(Cache<String, ApplicationComponent> cache) {
        Optional<IApplicationComponentPersistenceService> ps = persistence();
        if (ps.isEmpty()) {
            return;
        }
        cache.forEach((k, v) -> {
            try {
                ps.get().save(k, v);
            } catch (Exception ex) {
                getLogger().debug("bootstrapPersistAll: save failed for key={}", k, ex);
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
        public void onCreated(CacheEntryEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                        getLogger().trace("Listener[{}]: create key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        getLogger().debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryModified
        public void onModified(CacheEntryEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                persistence().ifPresent(ps -> {
                    try {
                        ps.save(e.getKey(), e.getValue());
                        getLogger().trace("Listener[{}]: modify key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        getLogger().debug("Listener[{}]: save failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryRemoved
        public void onRemoved(CacheEntryEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
                        getLogger().trace("Listener[{}]: remove key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        getLogger().debug("Listener[{}]: delete failed for key={}", cacheName, e.getKey(), ex);
                    }
                });
            }
        }

        @CacheEntryExpired
        public void onExpired(CacheEntryEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                persistence().ifPresent(ps -> {
                    try {
                        ps.delete(e.getKey());
                        getLogger().trace("Listener[{}]: expire key={}", cacheName, e.getKey());
                    } catch (Exception ex) {
                        getLogger().debug("Listener[{}]: expire-delete failed for key={}", cacheName, e.getKey(), ex);
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
        public void onCreated(CacheEntryEvent<String, String> e) {
            if (e.isPre()) {
                return;
            }
            String key = e.getKey();
            Optional<IApplicationComponentPersistenceService> ps = persistence();
            if (ps.isEmpty()) {
                return;
            }
            try {
                Optional<ApplicationComponent> loaded = ps.get().load(key);
                loaded.ifPresent(v -> cache.put(key, v));
                if (loaded.isPresent()) {
                    getLogger().info("LoadRequest[{}]: loaded and cached key={}", cacheName, key);
                } else {
                    getLogger().debug("LoadRequest[{}]: no result for key={}", cacheName, key);
                }
            } catch (Exception ex) {
                getLogger().debug("LoadRequest[{}]: failed to load key={}", cacheName, key, ex);
            }
        }
    }

    @Override
    public void put(ApplicationComponent item) {
        String key = resolveKey(item);
        cache.put(key, item);
        changeVersion.incrementAndGet();
    }

    @Override
    public ApplicationComponent get(String key) {
        ApplicationComponent existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        containsOrLoad(key);
        return cache.get(key);
    }

    @Override
    public ApplicationComponent remove(String key) {
        ApplicationComponent removed = cache.remove(key);
        if (removed != null) {
            changeVersion.incrementAndGet();
        }
        return removed;
    }

    @Override
    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsOrLoad(String key) {
        if (contains(key)) {
            return true;
        }
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
        } catch (Exception ex) {
            getLogger().debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    @Override
    public String resolveKey(ApplicationComponent item) {
        String key = item.resolveKey();
        return key;
    }

    @Override
    public List<ApplicationComponent> getSubcomponents(DistributableObjectId parentObjectId, ApplicationComponentSpecialisationEnum componentType) {
        getLogger().debug(".getContainedComponents(): Entry, parentObjectId={}, componentType={}", parentObjectId, componentType);
        String key = null;
        if (parentObjectId != null && parentObjectId.getQualifiedName() != null && parentObjectId.getQualifiedName().getCommonName().getValue() != null && !parentObjectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
            key = parentObjectId.getQualifiedName().getCommonName().getValue();
        }
        if (key == null)
            return Collections.emptyList();
        ApplicationComponent parent = get(key);

        if (parent == null) {
            getLogger().info(".getContainedComponents(): Exit, No subcomponents for id={} (component missing)", parentObjectId);
            return Collections.emptyList();
        }
        if (parent.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT) {
            getLogger().info(".getContainedComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", parentObjectId);
            return Collections.emptyList();
        }
        List<ApplicationComponent> resultList = new ArrayList<>();

        for (ElementReference childReference : parent.getSubComponents()) {
            String currentKey = childReference.getLocalObjectId().getQualifiedName().getCommonName().getValue();
            ApplicationComponent child = get(currentKey);
            if (child != null) {
                resultList.add(child);
            } else {
                getLogger().warn(".getContainedComponents(): No child component found for currentKey={}", currentKey);
            }
        }

        getLogger().info(".getContainedComponents(): Exit, Returning {} subcomponents for parentObjectId={}", resultList.size(), parentObjectId);
        return resultList;
    }

    @Override
    public ApplicationComponent getSolutionComponent() {
        return null;
    }

    @Override
    public boolean hasChangesSince(long sinceVersion) {
        getLogger().debug(".hasChangesSince(): Entry, sinceVersion={}", sinceVersion);
        if (sinceVersion < 0) {
            return true;
        }
        boolean changed = changeVersion.get() > sinceVersion;
        getLogger().debug(".hasChangesSince(): Exit, changed={}", changed);
        return (changed);
    }

    @Override
    public List<ApplicationComponent> getChangedApplicationComponents(Long start, Long size) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //
    // Accessors
    //
    protected Logger getLogger() {
        return LOG;
    }
    
    public Optional<Cache<String, ApplicationComponent>> getCache() {
        return Optional.ofNullable(cache);
    }

    public Optional<Cache<String, String>> getLoadRequestCache() {
        return Optional.ofNullable(loadRequestCache);
    }

    public long getChangeVersion() {
        return changeVersion.get();
    }
}
