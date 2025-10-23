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
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.datagrid.topology.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Non-store peer/client for ApplicationComponentSummary cache. Joins the same Infinispan cluster
 * and can interact with cache entries, but delegates persistence to store nodes.
 * If an entry is missing, it will publish a load request so a store node can load and populate it.
 */
@ApplicationScoped
public class TopologyCacheClient implements IApplicationComponentCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(TopologyCacheClient.class);

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
        String key = item.resolveKey();
        return key;
    }

    public Optional<Cache<String, ApplicationComponentSummary>> getCache() { return Optional.ofNullable(cache); }

    public ApplicationComponentSummary getSolutionComponent() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<ApplicationComponentSummary> getContainedComponents(DistributableObjectId parentObjectId, SoftwareComponentTypeEnum componentType) {
        LOG.debug(".getContainedComponents(): Entry, parentObjectId={}, componentType={}", parentObjectId, componentType);
        String key = null;
        if (parentObjectId != null && parentObjectId.getQualifiedName() != null && parentObjectId.getQualifiedName().getCommonName().getValue() != null && !parentObjectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
            key = parentObjectId.getQualifiedName().getCommonName().getValue();
        }
        if(key == null)
            return Collections.emptyList();
        ApplicationComponentSummary parent = get(key);

        if(parent == null){
            LOG.info(".getContainedComponents(): Exit, No subcomponents for id={} (component missing)", parentObjectId);
            return Collections.emptyList();
        }
        if(parent.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT){
            LOG.info(".getContainedComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", parentObjectId);
            return Collections.emptyList();
        }
        List<ApplicationComponentSummary> result = new ArrayList<>();
        // Determine child list based on the specific summary subtype
        if (parent instanceof net.fhirfactory.dricats.internals.oam.topology.SubsystemSummary) {
            net.fhirfactory.dricats.internals.oam.topology.SubsystemSummary subs = (net.fhirfactory.dricats.internals.oam.topology.SubsystemSummary) parent;
            if (subs.getApplicationClusters() != null) {
                for (DistributableObjectId childId : subs.getApplicationClusters()) {
                    String currentKey = childId.getQualifiedName().getCommonName().getValue();
                    ApplicationComponentSummary child = get(currentKey);
                    if (child != null) {
                        result.add((ApplicationComponentSummary) child);
                    } else {
                        LOG.warn(".getContainedComponents(): No child component found for currentKey={}", currentKey);
                    }
                }
            }
            if (subs.getApplicationInstances() != null) {
                for (DistributableObjectId childId : subs.getApplicationInstances()) {
                    String currentKey = childId.getQualifiedName().getCommonName().getValue();
                    ApplicationComponentSummary child = get(currentKey);
                    if (child != null) {
                        result.add( child);
                    } else {
                        LOG.warn(".getContainedComponents(): No child component found for childId={}", childId);
                    }
                }
            }
        } else if (parent instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationClusterSummary) {
            net.fhirfactory.dricats.internals.oam.topology.ApplicationClusterSummary cluster = (net.fhirfactory.dricats.internals.oam.topology.ApplicationClusterSummary) parent;
            if (cluster.getApplicationInstances() != null) {
                for (DistributableObjectId childId : cluster.getApplicationInstances()) {
                    String currentKey = childId.getQualifiedName().getCommonName().getValue();
                    ApplicationComponentSummary child = get(currentKey);
                    if (child != null) {
                        result.add(child);
                    } else {
                        LOG.warn(".getContainedComponents(): No child component found for childId={}", childId);
                    }
                }
            }
        } else if (parent instanceof net.fhirfactory.dricats.internals.oam.topology.ApplicationInstanceSummary) {
            net.fhirfactory.dricats.internals.oam.topology.ApplicationInstanceSummary instance = (net.fhirfactory.dricats.internals.oam.topology.ApplicationInstanceSummary) parent;
            if (instance.getWupGroups() != null) {
                for (DistributableObjectId childId : instance.getWupGroups()) {
                    String childKey = childId.getQualifiedName().getCommonName().getValue();
                    ApplicationComponentSummary child = get(childKey);
                    if (child != null) {
                        result.add(child);
                    } else {
                        LOG.warn(".getContainedComponents(): No child component found for childKey={}", childKey);
                    }
                }
            }
        } else if (parent instanceof net.fhirfactory.dricats.internals.oam.topology.WUPGroupSummary) {
            net.fhirfactory.dricats.internals.oam.topology.WUPGroupSummary group = (net.fhirfactory.dricats.internals.oam.topology.WUPGroupSummary) parent;
            if (group.getWorkUnitProcessors() != null) {
                for (DistributableObjectId childId : group.getWorkUnitProcessors()) {
                    String childKey = childId.getQualifiedName().getCommonName().getValue();
                    ApplicationComponentSummary child = get(childKey);
                    if (child != null) {
                        result.add( child);
                    } else {
                        LOG.warn(".getContainedComponents(): No child component found for childKey={}", childKey);
                    }
                }
            }
        }
        LOG.info(".getContainedComponents(): Exit, Returning {} subcomponents for parentObjectId={}", result.size(), parentObjectId);
        return result;



    }

}
