package net.fhirfactory.dricats.middleware.oam.central;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.JChannelMetricsService;
import org.jgroups.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Central-side OAM metrics consumer.
 *
 * Receives metrics coming from satellites via the OAM channel and persists them
 * into the local metrics server (H2-backed implementation).
 */
@ApplicationScoped
public class CentralMetricsConsumer extends JChannelMetricsService {
    private static final Logger LOG = LoggerFactory.getLogger(CentralMetricsConsumer.class);

    @Inject
    ILocalMetricsServerInterface localMetricsServer;

    // --- Public API ---------------------------------------------------------

    /**
     * Accept metrics received from a satellite and store them locally for centralised OAM.
     */
    public void accept(ApplicationComponent component, ApplicationComponentMetricsData metrics){
        if(component == null || metrics == null){
            LOG.warn("[OAM-CENTRAL] accept: component or metrics is null, ignoring. component={}, metrics={} ", component, metrics);
            return;
        }
        LOG.debug("[OAM-CENTRAL] accept: component={}, metrics.lastActivity={}", component, metrics.getLastActivityInstant());
        ApplicationComponentSummary summary = new ApplicationComponentSummary(component);
        localMetricsServer.addMetrics(summary, metrics);
        touchChannelActivity();
    }

    /**
     * Convenience overload: accept a component that already carries metrics inside.
     */
    public void accept(ApplicationComponent component){
        if(component == null){
            LOG.warn("[OAM-CENTRAL] accept(component): component is null");
            return;
        }
        ApplicationComponentMetricsData md = component.getMetricsData();
        if(md == null){
            LOG.warn("[OAM-CENTRAL] accept(component): metricsData is null for component={}", component);
            return;
        }
        accept(component, md);
    }

    /**
     * Convenience overload: accept raw metrics when the component wrapper is not provided.
     * Component identification data may be partially missing in this case; it will still be stored.
     */
    public void accept(ApplicationComponentMetricsData metrics){
        if(metrics == null){
            LOG.warn("[OAM-CENTRAL] accept(metrics): metrics is null");
            return;
        }
        ApplicationComponentSummary placeholder = new ApplicationComponentSummary();
        localMetricsServer.addMetrics(placeholder, metrics);
        touchChannelActivity();
    }

    // --- JGroups Receive ----------------------------------------------------

    @Override
    public void receive(Message msg) {
        if(msg == null){
            return;
        }
        Object payload = msg.getObject();
        try {
            if(payload instanceof ApplicationComponent){
                // Component with metrics embedded
                accept((ApplicationComponent) payload);
                return;
            }
            if(payload instanceof ApplicationComponentMetricsData){
                // Only metrics provided
                accept((ApplicationComponentMetricsData) payload);
                return;
            }
            LOG.info("[OAM-CENTRAL] receive: Unsupported payload type={}, from={}", (payload!=null?payload.getClass().getName():"null"), msg.getSrc());
        } catch (Exception e){
            LOG.warn("[OAM-CENTRAL] receive: failed to process message from {}. Error: {}", msg.getSrc(), e.getMessage(), e);
        }
    }

    // --- Helpers ------------------------------------------------------------

    private void touchChannelActivity(){
        try {
            if (getEndpoint() != null && getEndpoint().getMetricsData() != null) {
                getEndpoint().getMetricsData().touchLastActivityInstant();
            }
        } catch (Exception ex){
            LOG.debug("[OAM-CENTRAL] touchChannelActivity: unable to update channel metrics: {}", ex.getMessage());
        }
    }
}
