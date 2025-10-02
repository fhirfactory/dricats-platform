package net.fhirfactory.dricats.ui.serverside.metrics;

import jakarta.annotation.PostConstruct;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UIMetricsService {
    //
     // Housekeeping
    //
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UIMetricsService.class);

    //
    // Attributes
    //
    private Instant startupInstant;
    private Map<String, ApplicationComponentMetricsData> metricsMap;
    private boolean initialised = false;

    //
     // Constructor(s)
    //

    public UIMetricsService() {
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

    public ApplicationComponentMetricsData getLatestMetricsForComponent(ApplicationComponentSummary component) {
        LOG.debug(".getLatestMetricsForComponent(component={}) invoked", component);
        if (component == null) {
            LOG.warn(".getLatestMetricsForComponent(): Cannot build metrics: component not found for component");
            return null;
        }
        ApplicationComponentMetricsData m = metricsMap.get(component.getObjectID().toToken());
        if (m == null) {
            m = new ApplicationComponentMetricsData();
            m.setComponentID(component.getObjectID());
            m.setParticipantName(Optional.ofNullable(component.getName()).orElse(""));
            metricsMap.put(component.getObjectID().toToken(), m);
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
