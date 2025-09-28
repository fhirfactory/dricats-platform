package net.fhirfactory.dricats.datagrid.satellite.topologygrid;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.datagrid.topology.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Non-store peer/client for ApplicationComponentSummary cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class ApplicationComponentCacheClient implements IApplicationComponentCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponentCacheClient.class);

    public static final String DEFAULT_CACHE_NAME = "ApplicationComponentCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "ApplicationComponentLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, ApplicationComponentSummary> cache;
    private Cache<String, String> loadRequestCache;

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

    @Override
    public void put(ApplicationComponentSummary item) {
        String key = resolveKey(item);
        cache.put(key, item);
    }

    @Override
    public ApplicationComponentSummary get(String key) {
        ApplicationComponentSummary existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        containsOrLoad(key);
        return cache.get(key);
    }

    @Override
    public ApplicationComponentSummary remove(String key) { return cache.remove(key); }

    @Override
    public boolean contains(String key) { return cache.containsKey(key); }

    @Override
    public boolean containsOrLoad(String key) {
        if (contains(key)) { return true; }
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
        } catch (Exception ex) {
            LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    @Override
    public String resolveKey(ApplicationComponentSummary item) {
        String key = null;
        try {
            DistributableObjectId objectId = item.getObjectID();
            if (objectId != null && objectId.getId() != null && objectId.getId().getValue() != null && !objectId.getId().getValue().isEmpty()) {
                key = objectId.getId().getValue();
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
                LOG.debug("resolveKey(ApplicationComponent): unable to set generated id on item", e);
            }
        }
        return key;
    }

    public Optional<Cache<String, ApplicationComponentSummary>> getCache() { return Optional.ofNullable(cache); }

    public ApplicationComponentSummary getSolutionComponent() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<ApplicationComponentSummary> getContainedComponents(DistributableObjectId parent, SoftwareComponentTypeEnum componentType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
