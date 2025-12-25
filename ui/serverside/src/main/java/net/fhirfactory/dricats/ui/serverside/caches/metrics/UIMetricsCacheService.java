package net.fhirfactory.dricats.ui.serverside.caches.metrics;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UIMetricsCacheService {
    //
     // Housekeeping
    //
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UIMetricsCacheService.class);

    //
    // Attributes
    //
    private Instant startupInstant;
    private Map<String, ApplicationComponentMetricsData> metricsMap;
    private boolean initialised = false;

    //
     // Constructor(s)
    //

    public UIMetricsCacheService() {
        LOG.debug("TestMetricsService() invoked");
        startupInstant = Instant.now();
        metricsMap = new HashMap<>();
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
        String key = Optional.ofNullable(component.getObjectID()).map(id -> id.getCommonId()).map(t -> t.getToken()).orElse("");
        ApplicationComponentMetricsData m = metricsMap.get(key);
        if (m == null) {
            m = new ApplicationComponentMetricsData();
            m.setComponentID(component.getObjectID());
            m.setParticipantName(Optional.ofNullable(component.getName()).orElse(""));
            metricsMap.put(key, m);
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
}
