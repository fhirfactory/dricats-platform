package net.fhirfactory.dricats.middleware.oam.satellite;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.JChannelMetricsService;
import org.jgroups.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Satellite-side OAM metrics publisher.
 *
 * This bean demonstrates how to use the JChannelOAMService to participate in the OAM channel
 * and exchange metrics data with the central instance. For now, the actual wire-level exchange
 * is handled by the JGroups infrastructure; we update the endpoint metrics and push the data into
 * the local metrics server which can be queried/forwarded by the JGroups layer.
 */
@ApplicationScoped
public class SatelliteMetricsPublisher extends JChannelMetricsService {
    private static final Logger LOG = LoggerFactory.getLogger(SatelliteMetricsPublisher.class);

    @Inject
    ILocalMetricsServerInterface localMetricsServer;

    /**
     * Publish metrics gathered for a local software component. The JChannelOAMService is initialised via CDI and
     * participates in the OAM JGroups channel. This call ensures our endpoint metrics reflect activity and
     * stores the metrics into the local metrics server so it is available for collection by the channel/watchdog.
     */
    public void publishMetrics(ApplicationComponentSummary component, ApplicationComponentMetricsData metrics){
        LOG.debug("[OAM-SAT] publishMetrics: component={}, metrics.lastActivity={}", component, metrics.getLastActivityInstant());
        // Record locally
        localMetricsServer.addMetrics(component, metrics);
        // Touch channel endpoint metrics to reflect activity on OAM channel
        if (getEndpoint() != null && getEndpoint().getMetricsData() != null) {
            getEndpoint().getMetricsData().touchLastActivityInstant();
        }
    }

    @Override
    public void receive(Message msg) {

    }
}
