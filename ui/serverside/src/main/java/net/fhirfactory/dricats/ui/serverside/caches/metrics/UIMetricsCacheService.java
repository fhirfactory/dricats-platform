package net.fhirfactory.dricats.ui.serverside.caches.metrics;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UIMetricsCacheService implements ILocalMetricsServerInterface {
    //
     // Housekeeping
    //
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UIMetricsCacheService.class);

    //
    // Attributes
    //
    private Instant startupInstant;
    private ConcurrentHashMap<String, ApplicationComponentMetricsData> latestMetricsCache;
    private List<ApplicationComponentMetricsData> historicalMetricsBuffer;
    private final Object bufferLock = new Object();
    private boolean initialised = false;

    //
     // Constructor(s)
    //

    public UIMetricsCacheService() {
        LOG.debug("UIMetricsCacheService() invoked");
        startupInstant = Instant.now();
        latestMetricsCache = new ConcurrentHashMap<>();
        historicalMetricsBuffer = new ArrayList<>();
    }

    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        if (!initialised) {
            initialised = true;
        }
        LOG.debug(".initialise(): Exit");
    }

    //
     // Methods
    //

    public ApplicationComponentMetricsData getLatestMetricsForComponent(ApplicationComponent component) {
        LOG.debug(".getLatestMetricsForComponent(component={}) invoked", component);
        if (component == null) {
            LOG.warn(".getLatestMetricsForComponent(): Cannot build metrics: component not found for component");
            return null;
        }
        String key = component.resolveElementInstanceKey();
        ApplicationComponentMetricsData m = latestMetricsCache.get(key);
        if (m == null) {
            m = new ApplicationComponentMetricsData();
            m.setComponentID(component.getElementInstanceId());
            m.setParticipantName(Optional.ofNullable(component.getIdentifier().getIdentifierValue().getCommonName().getName()).orElse(""));
            latestMetricsCache.put(key, m);
        }
        m.setComponentStatus("OK");
        m.getMessagingStatistics().incrementEgressMessageAttemptCount();
        m.getMessagingStatistics().incrementEgressMessageSuccessCount();
        m.getMessagingStatistics().incrementIngresMessageCount();
        m.setComponentStartupInstant(startupInstant);
        m.setLastActivityInstant(Instant.now());
        LOG.info(".getLatestMetricsForComponent(): Built latest metrics ApplicationComponentMetricsData={}", m);
        return m;
    }

    @Override
    public void addMetrics(ApplicationComponent softwareComponent, ApplicationComponentMetricsData metricsData) {
        LOG.debug(".addMetrics(): Entry, softwareComponent={}, metricsData={}", softwareComponent, metricsData);
        if (softwareComponent == null || metricsData == null) {
            return;
        }
        String key = softwareComponent.resolveElementInstanceKey();
        // Update latest cache
        latestMetricsCache.put(key, metricsData);

        // Add to historical buffer
        synchronized (bufferLock) {
            historicalMetricsBuffer.add(metricsData);
            // Limit buffer size
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
        String key = softwareComponent.resolveElementInstanceKey();
        return latestMetricsCache.remove(key);
    }

    @Override
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        LOG.debug(".getMetrics(): Entry, metricsStartTime={}, metricsEndTime={}", metricsStartTime, metricsEndTime);
        synchronized (bufferLock) {
            List<ApplicationComponentMetricsData> metricsToRemove = historicalMetricsBuffer.stream()
                    .filter(metrics -> {
                        if (metrics.getLastActivityInstant() == null) {
                            return false;
                        }
                        LocalDateTime activityTime = LocalDateTime.ofInstant(metrics.getLastActivityInstant(), ZoneId.systemDefault());
                        return (activityTime.isAfter(metricsStartTime) || activityTime.isEqual(metricsStartTime)) &&
                                (activityTime.isBefore(metricsEndTime) || activityTime.isEqual(metricsEndTime));
                    })
                    .toList();
            historicalMetricsBuffer.removeAll(metricsToRemove);
            return metricsToRemove;
        }
    }

    @Override
    public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription) {
        LOG.warn(".registerFailedComponent(): component={}, failure={}", softwareComponent, failureDescription);
    }
}
