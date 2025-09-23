package net.fhirfactory.dricats.platform.middleware.oam.satellite;

import net.fhirfactory.dricats.internals.oam.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.JChannelOAMService;
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
public class OAMSatelliteMetricsPublisher {
    private static final Logger LOG = LoggerFactory.getLogger(OAMSatelliteMetricsPublisher.class);

    @Inject
    JChannelOAMService oamChannel;

    @Inject
    ILocalMetricsServerInterface localMetricsServer;

    /**
     * Publish metrics gathered for a local software component. The JChannelOAMService is initialised via CDI and
     * participates in the OAM JGroups channel. This call ensures our endpoint metrics reflect activity and
     * stores the metrics into the local metrics server so it is available for collection by the channel/watchdog.
     */
    public void publishMetrics(ApplicationComponent component, ApplicationComponentMetricsData metrics){
        LOG.debug("[OAM-SAT] publishMetrics: component={}, metrics.lastActivity={}", component, metrics.getLastActivityInstant());
        // Record locally
        localMetricsServer.addMetrics(component, metrics);
        // Touch channel endpoint metrics to reflect activity on OAM channel
        if (oamChannel != null && oamChannel.getEndpoint() != null && oamChannel.getEndpoint().getMetricsData() != null) {
            oamChannel.getEndpoint().getMetricsData().touchLastActivityInstant();
        }
    }
}
