package net.fhirfactory.dricats.datagrid.satellite.messagegrid;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageSubscription;
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
 * Non-store peer/client for MessageSubscription cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class MessageSubscriptionCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSubscriptionCacheClient.class);

    public static final String DEFAULT_CACHE_NAME = "MessageSubscriptionCache";
    public static final String DEFAULT_LOAD_REQUEST_CACHE_NAME = "MessageSubscriptionLoadRequests";

    private DefaultCacheManager cacheManager;
    private Cache<String, MessageSubscription> cache;
    private Cache<String, String> loadRequestCache;

    @PostConstruct
    public void start() {
        try {
            LOG.info("Starting MessageSubscriptionCacheClient (clustered, non-store)...");
            String clusterName = System.getProperty("message.subscription.store.cluster.name", "dricats-message-subscription-cluster");
            String nodeName = System.getProperty("message.subscription.store.node.name");

            GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
            global.transport().defaultTransport().clusterName(clusterName);
            if (nodeName != null && !nodeName.isEmpty()) {
                global.transport().nodeName(nodeName);
            }
            String jgroupsConfig = System.getProperty("message.subscription.store.jgroups.config");
            String jgroupsStack = System.getProperty("message.subscription.store.jgroups.stack");
            if (jgroupsConfig != null && !jgroupsConfig.isEmpty()) {
                global.transport().addProperty("configurationFile", jgroupsConfig);
                if (jgroupsStack != null && !jgroupsStack.isEmpty()) {
                    global.transport().addProperty("stack", jgroupsStack);
                }
                LOG.info("MessageSubscriptionCacheClient: Using custom JGroups config file='{}' stack='{}'", jgroupsConfig, jgroupsStack);
            } else {
                LOG.info("MessageSubscriptionCacheClient: Using default JGroups transport (no custom config provided)");
            }

            this.cacheManager = new DefaultCacheManager(global.build());

            ConfigurationBuilder base = new ConfigurationBuilder();

            String cacheModeProp = System.getProperty("message.subscription.store.cache.mode", "dist").trim().toLowerCase();
            int owners = Integer.getInteger("message.subscription.store.cache.owners", 2);
            if ("repl".equals(cacheModeProp)) {
                base.clustering().cacheMode(CacheMode.REPL_SYNC);
            } else {
                base.clustering().cacheMode(CacheMode.DIST_SYNC).hash().numOwners(owners);
            }

            String cacheName = System.getProperty("message.subscription.store.cache.name", DEFAULT_CACHE_NAME);
            this.cacheManager.defineConfiguration(cacheName, base.build());
            this.cache = this.cacheManager.getCache(cacheName);
            LOG.info("MessageSubscriptionCacheClient: cache={} ready in cluster='{}' node='{}' mode='{}' owners={}",
                    cacheName, clusterName, nodeName, cacheModeProp, owners);

            String reqCacheName = System.getProperty("message.subscription.store.load.request.cache.name", DEFAULT_LOAD_REQUEST_CACHE_NAME);
            ConfigurationBuilder reqCfg = new ConfigurationBuilder();
            reqCfg.clustering().cacheMode(CacheMode.REPL_SYNC);
            long ttlMs = Long.getLong("message.subscription.store.load.request.ttl.ms", 60_000L);
            reqCfg.expiration().lifespan(ttlMs, TimeUnit.MILLISECONDS);
            this.cacheManager.defineConfiguration(reqCacheName, reqCfg.build());
            this.loadRequestCache = this.cacheManager.getCache(reqCacheName);
            LOG.info("MessageSubscriptionCacheClient: load-request cache={} ready (ttlMs={})", reqCacheName, ttlMs);
        } catch (Exception e) {
            LOG.error("Failed to start MessageSubscriptionCacheClient", e);
            throw new RuntimeException("Failed to start MessageSubscriptionCacheClient", e);
        }
    }

    @PreDestroy
    public void stop() {
        LOG.info("Stopping MessageSubscriptionCacheClient...");
        try {
            if (cache != null) {
                cache.stop();
            }
            if (cacheManager != null) {
                cacheManager.stop();
            }
        } catch (Exception e) {
            LOG.warn("Error while stopping MessageSubscriptionCacheClient", e);
        }
        LOG.info("MessageSubscriptionCacheClient stopped");
    }

    public void put(MessageSubscription item) {
        String key = resolveKey(item);
        cache.put(key, item);
    }

    public MessageSubscription get(String key) {
        MessageSubscription existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        containsOrLoad(key);
        return cache.get(key);
    }

    public MessageSubscription remove(String key) {
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

    protected String resolveKey(MessageSubscription item) {
        String key = item.resolveKey();
        return key;
    }

    public Optional<Cache<String, MessageSubscription>> getCache() {
        return Optional.ofNullable(cache);
    }
}
