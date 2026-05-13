package net.fhirfactory.dricats.datagrid.common.metricsgrid;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Common implementation of the ILocalMetricsServerInterface.
 * Provides an in-memory cache of metrics data.
 */
@ApplicationScoped
public class LocalMetricsServer implements ILocalMetricsServerInterface {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMetricsServer.class);

    private final ConcurrentHashMap<ApplicationComponent, ApplicationComponentMetricsData> latestMetricsCache;
    private final List<ApplicationComponentMetricsData> historicalMetricsBuffer;
    private final Object bufferLock = new Object();

    public LocalMetricsServer() {
        this.latestMetricsCache = new ConcurrentHashMap<>();
        this.historicalMetricsBuffer = new ArrayList<>();
    }

    @Override
    public void addMetrics(ApplicationComponent softwareComponent, ApplicationComponentMetricsData metricsData) {
        LOG.debug(".addMetrics(): Entry, softwareComponent={}, metricsData={}", softwareComponent, metricsData);
        if (softwareComponent == null || metricsData == null) {
            return;
        }
        // Update latest cache
        latestMetricsCache.put(softwareComponent, metricsData);

        // Add to historical buffer
        synchronized (bufferLock) {
            historicalMetricsBuffer.add(metricsData);
            // Optional: limit buffer size to prevent memory leaks in long-running processes
            if (historicalMetricsBuffer.size() > 5000) {
                historicalMetricsBuffer.remove(0);
            }
        }
    }

    @Override
    public ApplicationComponentMetricsData getMetrics(ApplicationComponent softwareComponent) {
        LOG.debug(".getMetrics(): Entry, softwareComponent={}", softwareComponent);
        if (softwareComponent == null) {
            return null;
        }
        return latestMetricsCache.get(softwareComponent);
    }

    @Override
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        LOG.debug(".getMetrics(): Entry, metricsStartTime={}, metricsEndTime={}", metricsStartTime, metricsEndTime);
        synchronized (bufferLock) {
            List<ApplicationComponentMetricsData> matchedMetrics = historicalMetricsBuffer.stream()
                    .filter(metrics -> {
                        if (metrics.getLastActivityInstant() == null) {
                            return false;
                        }
                        LocalDateTime activityTime = LocalDateTime.ofInstant(metrics.getLastActivityInstant(), ZoneId.systemDefault());
                        return (activityTime.isAfter(metricsStartTime) || activityTime.isEqual(metricsStartTime)) &&
                               (activityTime.isBefore(metricsEndTime) || activityTime.isEqual(metricsEndTime));
                    })
                    .toList();
            return matchedMetrics;
        }
    }

    @Override
    public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription) {
        LOG.warn(".registerFailedComponent(): component={}, failure={}", softwareComponent, failureDescription);
        // In-memory implementation could potentially store failures in a separate structure if needed.
        // For now, we just log it as the interface doesn't define a getter for failures.
    }

    public Map<ApplicationComponent, ApplicationComponentMetricsData> getLatestMetricsCache() {
        return Collections.unmodifiableMap(latestMetricsCache);
    }
}
