package net.fhirfactory.dricats.datagrid.satellite.notificationgrid;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.pubsub.NotificationSubscription;
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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Non-store peer/client for NotificationSubscription cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class NotificationSubscriptionCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSubscriptionCacheClient.class);

    public static final String DEFAULT_CACHE_NAME = "NotificationSubscriptionCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "NotificationSubscriptionLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, NotificationSubscription> cache;
    private Cache<String, String> loadRequestCache;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting NotificationSubscriptionCacheClient (clustered, non-store)...");
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
                LOG.info("NotificationSubscriptionCacheClient: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("NotificationSubscriptionCacheClient: Using default JGroups transport (no custom config provided)");
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
            LOG.info("NotificationSubscriptionCacheClient: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("notification.subscription.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("notification.subscription.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("NotificationSubscriptionCacheClient: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);
        } catch (Exception e) {
            LOG.error("Failed to start NotificationSubscriptionCacheClient", e);
            throw new RuntimeException("Failed to start NotificationSubscriptionCacheClient", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping NotificationSubscriptionCacheClient...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping NotificationSubscriptionCacheClient", e);
        }
        LOG.info("NotificationSubscriptionCacheClient stopped");
    }

    public void put(NotificationSubscription item) {
        String key = resolveKey(item);
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
        // Client has no persistence; ask store node via request-cache to load
        try {
            loadRequestCache.putIfAbsent(key, "REQ");
        } catch (Exception ex) {
            LOG.debug("containsOrLoad: failed to enqueue load request for key={}", key, ex);
        }
        return false;
    }

    protected String resolveKey(NotificationSubscription item) {
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
                LOG.debug("resolveKey(NotificationSubscription): unable to set generated id on item", e);
            }
        }
        return key;
    }

    public Optional<Cache<String, NotificationSubscription>> getCache() {
        return Optional.ofNullable(cache);
    }
}
