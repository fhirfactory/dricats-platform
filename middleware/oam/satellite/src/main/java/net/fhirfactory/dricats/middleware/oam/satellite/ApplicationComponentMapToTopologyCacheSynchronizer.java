package net.fhirfactory.dricats.middleware.oam.satellite;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.datagrid.satellite.topologygrid.ApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Periodically synchronises local InMemoryApplicationComponentStore to the clustered
 * ApplicationComponentCacheClient. This performs an upsert (put) of all locally known
 * ApplicationComponent entries into the distributed cache. It does not attempt to remove
 * entries from the cache if they are missing locally.
 */
@ApplicationScoped
public class ApplicationComponentMapToTopologyCacheSynchronizer {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponentMapToTopologyCacheSynchronizer.class);

    @Inject
    ILocalApplicationComponentMap localApplicationComponentMap;

    @Inject
    ApplicationComponentCacheClient cacheClient;

    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void start() {
        long initialDelaySec = Long.getLong("application.component.sync.initial.delay.seconds", 5L);
        long intervalSec = Long.getLong("application.component.sync.interval.seconds", 30L);

        this.scheduler = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("app-comp-sync"));
        this.scheduler.scheduleAtFixedRate(this::syncOnce, initialDelaySec, intervalSec, TimeUnit.SECONDS);
        LOG.info("ApplicationComponentStoreToCacheSynchronizer started (initialDelaySec={}, intervalSec={})", initialDelaySec, intervalSec);
    }

    @PreDestroy
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        LOG.info("ApplicationComponentStoreToCacheSynchronizer stopped");
    }

    protected void syncOnce() {
        try {
            if (localApplicationComponentMap == null || cacheClient == null) {
                LOG.warn("syncOnce: dependencies not available (localStore={}, cacheClient={})", localApplicationComponentMap, cacheClient);
                return;
            }
            Collection<ApplicationComponent> components = localApplicationComponentMap.getAll();
            int total = components.size();
            int success = 0;
            for (ApplicationComponent ac : components) {
                if (ac == null) { continue; }
                try {
                    ApplicationComponentSummary summary = new ApplicationComponentSummary(ac);
                    cacheClient.put(summary);
                    success++;
                } catch (Exception ex) {
                    LOG.debug("syncOnce: failed to put component into cache (component={})", safeName(ac), ex);
                }
            }
            LOG.trace("ApplicationComponent sync completed: total={} success={} nodeCachePresent={}", total, success, cacheClient.getCache().isPresent());
        } catch (Exception e) {
            LOG.debug("syncOnce: unexpected error while synchronising store to cache", e);
        }
    }

    private String safeName(ApplicationComponent ac) {
        try {
            return ac.getName();
        } catch (Exception e) {
            return Objects.toString(ac);
        }
    }

    static class DaemonThreadFactory implements ThreadFactory {
        private final String prefix;
        DaemonThreadFactory(String prefix) { this.prefix = prefix; }
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName(prefix + "-" + t.getId());
            t.setDaemon(true);
            return t;
        }
    }
}
