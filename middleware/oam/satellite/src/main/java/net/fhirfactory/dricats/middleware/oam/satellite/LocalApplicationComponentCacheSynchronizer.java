package net.fhirfactory.dricats.middleware.oam.satellite;

import net.fhirfactory.dricats.datagrid.satellite.topologygrid.ApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Keeps the distributed ApplicationComponent cache in sync with the local in-memory map
 * and periodically verifies presence. This runs on OAM Satellite nodes and acts as a
 * publisher/repair loop: on startup it publishes all known components, then on a fixed
 * interval it checks that each local component exists in the distributed cache and
 * republishes any missing entries.
 */
@ApplicationScoped
public class LocalApplicationComponentCacheSynchronizer {
    private static final Logger LOG = LoggerFactory.getLogger(LocalApplicationComponentCacheSynchronizer.class);

    // Default constructor for CDI proxies
    protected LocalApplicationComponentCacheSynchronizer() {
        this.localMap = null;
        this.cacheClient = null;
    }

    // System properties to tune behaviour
    private static final String PROP_ENABLED = "oam.satellite.appcomp.sync.enabled"; // default true
    private static final String PROP_INTERVAL_MS = "oam.satellite.appcomp.sync.interval.ms"; // default 30000

    private final ILocalApplicationComponentMap localMap;
    private final ApplicationComponentCacheClient cacheClient;

    private ScheduledExecutorService scheduler;

    @Inject
    public LocalApplicationComponentCacheSynchronizer(ILocalApplicationComponentMap localMap,
                                                      ApplicationComponentCacheClient cacheClient) {
        this.localMap = localMap;
        this.cacheClient = cacheClient;
        init();
    }

    private void init() {
        if (!isEnabled()) {
            LOG.info("LocalApplicationComponentCacheSynchronizer disabled via system property: {}=false", PROP_ENABLED);
            return;
        }
        LOG.info("Starting LocalApplicationComponentCacheSynchronizer... (intervalMs={})", intervalMs());
        try {
            // Initial one-off publish of all local components
            publishAllLocal();

            // Start background periodic verifier
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "LocalAppCompCacheSync");
                t.setDaemon(true);
                return t;
                });
            scheduler.scheduleWithFixedDelay(this::verifyAndRepair, intervalMs(), intervalMs(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOG.warn("Failed to start LocalApplicationComponentCacheSynchronizer", e);
        }
    }

    public void stop() {
        if (scheduler != null) {
            try {
                scheduler.shutdownNow();
            } catch (Exception e) {
                LOG.debug("Error shutting down LocalApplicationComponentCacheSynchronizer scheduler", e);
            }
        }
        LOG.info("Stopped LocalApplicationComponentCacheSynchronizer");
    }

    protected boolean isEnabled() {
        return Boolean.parseBoolean(System.getProperty(PROP_ENABLED, "true"));
    }

    protected long intervalMs() {
        return Long.getLong(PROP_INTERVAL_MS, 30_000L);
    }

    private void publishAllLocal() {
        try {
            Collection<ApplicationComponent> all = localMap.getAll();
            int published = 0;
            for (ApplicationComponent ac : all) {
                try {
                    ApplicationComponentSummary summary = new ApplicationComponentSummary(ac);
                    String key = safeKey(summary);
                    if (key == null) {
                        LOG.debug("publishAllLocal: skip component without resolvable key: {}", ac.getName());
                        continue;
                    }
                    cacheClient.put(summary);
                    published++;
                } catch (Exception ex) {
                    LOG.debug("publishAllLocal: failed to publish component {}", safeName(ac), ex);
                }
            }
            LOG.info("publishAllLocal: Published {} ApplicationComponents to distributed cache (of {} local)", published, all.size());
        } catch (Exception e) {
            LOG.warn("publishAllLocal: unexpected error", e);
        }
    }

    private void verifyAndRepair() {
        try {
            Collection<ApplicationComponent> all = localMap.getAll();
            int repaired = 0;
            for (ApplicationComponent ac : all) {
                try {
                    ApplicationComponentSummary summary = new ApplicationComponentSummary(ac);
                    String key = safeKey(summary);
                    if (key == null) { continue; }

                    boolean present = cacheClient.containsOrLoad(key);
                    if (!present) {
                        cacheClient.put(summary);
                        repaired++;
                        LOG.debug("verifyAndRepair: re-published missing component key={}", key);
                    }
                } catch (Exception ex) {
                    LOG.debug("verifyAndRepair: error checking component {}", safeName(ac), ex);
                }
            }
            if (repaired > 0) {
                LOG.info("verifyAndRepair: repaired {} missing ApplicationComponents", repaired);
            }
        } catch (Exception e) {
            LOG.debug("verifyAndRepair: unexpected error", e);
        }
    }

    private static String safeKey(ApplicationComponentSummary summary) {
        if (summary == null) { return null; }
        try {
            String key = summary.resolveKey();
            return (key != null && !key.isEmpty()) ? key : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String safeName(ApplicationComponent ac) {
        if (ac == null) { return "<null>"; }
        return Objects.toString(ac.getName(), "<unnamed>");
    }
}
