package net.fhirfactory.dricats.datagrid.satellite.pathwaysgrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Non-store peer/client for PathwayElement cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class PathwayElementCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(PathwayElementCacheClient.class);

    public static final String DEFAULT_CACHE_NAME = "PathwayElementCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "PathwayElementLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, PathwayElement> cache;
    private Cache<String, String> loadRequestCache;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting PathwayElementCacheClient (clustered, non-store)...");
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
                LOG.info("PathwayElementCacheClient: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("PathwayElementCacheClient: Using default JGroups transport (no custom config provided)");
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
            LOG.info("PathwayElementCacheClient: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("pathway.element.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("pathway.element.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("PathwayElementCacheClient: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);
        } catch (Exception e) {
            LOG.error("Failed to start PathwayElementCacheClient", e);
            throw new RuntimeException("Failed to start PathwayElementCacheClient", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping PathwayElementCacheClient...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping PathwayElementCacheClient", e);
        }
        LOG.info("PathwayElementCacheClient stopped");
    }

    public void put(PathwayElement item) {
        String key = resolveKey(item);
        cache.put(key, item);
    }

    public PathwayElement get(String key) {
        PathwayElement existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        containsOrLoad(key);
        return cache.get(key);
    }

    public PathwayElement remove(String key) {
        return cache.remove(key);
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }

    public boolean containsOrLoad(String key) {
        if (contains(key)) {
            return true;
        }
        // Client has no persistence; ask store node via request-cache to load
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
        } catch (Exception ex) {
            LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    protected String resolveKey(PathwayElement item) {
        // PathwayElement extends ManagedObject through RelationshipBase/FlowRelationship
        return item.resolveElementInstanceKey();
    }

    public Optional<Cache<String, PathwayElement>> getCache() {
        return Optional.ofNullable(cache);
    }
}
