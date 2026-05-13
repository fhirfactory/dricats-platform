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
package net.fhirfactory.dricats.datagrid.satellite.topologygrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.datagrid.common.topologygrid.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.common.id.ElementInstanceId;
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
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryExpiredEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Non-store peer/client for ApplicationComponentSummary cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class TopologyCacheClient implements IApplicationComponentCacheClient {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TopologyCacheClient.class);

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
    private HashMap<Long, ChangeLogEntry> changeMap;
    private final AtomicLong changeVersion = new AtomicLong(0);

    //
    // Constructors
    //
    public TopologyCacheClient() {
        super();
        this.changeMap = new HashMap<>();
    }

    //
    // Lifecycle
    //
    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting ApplicationComponentCacheClient (clustered, non-store)...");
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
                LOG.info("ApplicationComponentCacheClient: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("ApplicationComponentCacheClient: Using default JGroups transport (no custom config provided)");
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
            // Register change tracking listener to bump version on create/modify/remove/expire
            try {
                this.cache.addListener(new ChangeTrackingListener(cacheName));
            } catch (Exception ex) {
                LOG.debug("ApplicationComponentCacheClient: failed to register change listener for cache={}", cacheName, ex);
            }
            LOG.info("ApplicationComponentCacheClient: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("application.component.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("application.component.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("ApplicationComponentCacheClient: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);
        } catch (Exception e) {
            LOG.error("Failed to start ApplicationComponentCacheClient", e);
            throw new RuntimeException("Failed to start ApplicationComponentCacheClient", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping ApplicationComponentCacheClient...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping ApplicationComponentCacheClient", e);
        }
        LOG.info("ApplicationComponentCacheClient stopped");
    }

    @Listener(clustered = true, observation = Listener.Observation.POST)
    private class ChangeTrackingListener {
        private final String cacheName;

        ChangeTrackingListener(String cacheName) {
            this.cacheName = cacheName;
        }

        @CacheEntryCreated
        public void onCreated(CacheEntryCreatedEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                addApplicationComponentChangeLogEntry(changeVersion.get(), e.getKey());
                LOG.trace("ChangeTracking[{}]: created {}", cacheName, e.getKey());
            }
        }

        @CacheEntryModified
        public void onModified(CacheEntryModifiedEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                addApplicationComponentChangeLogEntry(changeVersion.get(), e.getKey());
                LOG.trace("ChangeTracking[{}]: modified {}", cacheName, e.getKey());
            }
        }

        @CacheEntryRemoved
        public void onRemoved(CacheEntryRemovedEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                addApplicationComponentChangeLogEntry(changeVersion.get(), e.getKey());
                LOG.trace("ChangeTracking[{}]: removed {}", cacheName, e.getKey());
            }
        }

        @CacheEntryExpired
        public void onExpired(CacheEntryExpiredEvent<String, ApplicationComponent> e) {
            if (!e.isPre()) {
                changeVersion.incrementAndGet();
                addApplicationComponentChangeLogEntry(changeVersion.get(), e.getKey());
                LOG.trace("ChangeTracking[{}]: expired {}", cacheName, e.getKey());
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
            LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    @Override
    public String resolveKey(ApplicationComponent item) {
        String key = item.resolveElementInstanceKey();
        return key;
    }

    public Optional<Cache<String, ApplicationComponent>> getCache() {
        return Optional.ofNullable(cache);
    }

    public ApplicationComponent getSolutionComponent() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public long getChangeVersion() {
        return changeVersion.get();
    }

    protected Map<Long, ChangeLogEntry> getChangeMap() {
        if (changeMap == null) {
            changeMap = new HashMap<>();
        }
        return changeMap;
    }

    protected void addApplicationComponentChangeLogEntry(long version, String key) {
        if(version < 0){
            return;
        }
        if(key == null || key.isEmpty()){
            return;
        }
        ChangeLogEntry entry = new ChangeLogEntry(version, key);
        getChangeMap().put(version, entry);
    }

    protected List<ChangeLogEntry> getApplicationComponentChangeMapKeys(Long start, Long size) {
        List<ChangeLogEntry> result = new ArrayList<>();
        if(start == null || start < 0){
            return(result);
        }
        if(size == null || size < 0){
            size = 10L;
        }
        for(long counter = start; counter < start + size; counter++){
            ChangeLogEntry key = getChangeMap().get(counter);
            if(key != null){
                result.add(key);
                getChangeMap().remove(counter);
            }
        }
        return (result);
    }

    public List<ApplicationComponent> getChangedApplicationComponents(Long start, Long size) {
        List<ChangeLogEntry> keys = getApplicationComponentChangeMapKeys(start, size);
        List<ApplicationComponent> result = new ArrayList<>();
        for(ChangeLogEntry key : keys){
            ApplicationComponent item = get(key.getKey());
            if(item != null){
                result.add(item);
            }
        }
        return(result);
    }

    public List<ApplicationComponent> getSubcomponents(ElementInstanceId parentElementInstanceId, ApplicationComponentSpecialisationEnum componentType) {
        LOG.debug(".getContainedComponents(): Entry, parentObjectId={}, componentType={}", parentElementInstanceId, componentType);
        if (parentElementInstanceId == null)
            return Collections.emptyList();
        ApplicationComponent parent = get(parentElementInstanceId.getIdValue());

        if (parent == null) {
            LOG.info(".getContainedComponents(): Exit, No subcomponents for id={} (component missing)", parentElementInstanceId);
            return Collections.emptyList();
        }
        if (parent.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT) {
            LOG.info(".getContainedComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", parentElementInstanceId);
            return Collections.emptyList();
        }
        List<ApplicationComponent> resultList = new ArrayList<>();

        for (ElementReference childReference : parent.getSubComponents()) {
            String currentKey = childReference.getElementInstanceId().getIdValue();
            ApplicationComponent child = get(currentKey);
            if (child != null) {
                resultList.add(child);
            } else {
                LOG.warn(".getContainedComponents(): No child component found for currentKey={}", currentKey);
            }
        }

        LOG.info(".getContainedComponents(): Exit, Returning {} subcomponents for parentObjectId={}", resultList.size(), parentElementInstanceId);
        return resultList;
    }

    public class ChangeLogEntry {
        private final long version;
        private final String key;
        private LocalDateTime timestamp;

        public ChangeLogEntry(long version, String key) {
            this.version = version;
            this.key = key;
            this.timestamp = LocalDateTime.now();
        }

        public long getVersion() {
            return version;
        }

        public String getKey() {
            return key;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

}
