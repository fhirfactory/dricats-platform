package net.fhirfactory.dricats.platform.middleware.oam.central;

import net.fhirfactory.dricats.internals.oam.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.JChannelOAMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * Central-side OAM metrics consumer.
 *
 * This bean demonstrates how to receive/store metrics coming from satellites via the OAM channel.
 * The JChannelOAMService provides the underlying JGroups connectivity. Here we simply expose an
 * accept method that central code (or message handlers bound to the channel) can call to persist
 * metrics via the local metrics server.
 */
@ApplicationScoped
public class OAMCentralMetricsConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(OAMCentralMetricsConsumer.class);

    @Inject
    JChannelOAMService oamChannel;

    @Inject
    ILocalMetricsServerInterface localMetricsServer;

    /**
     * Accept metrics received from a satellite and store them locally for centralised OAM.
     */
    public void accept(ApplicationComponent component, ApplicationComponentMetricsData metrics){
        LOG.info("[OAM-CENTRAL] accept: component={}, metrics.lastActivity={}", component, metrics.getLastActivityInstant());
        localMetricsServer.addMetrics(component, metrics);
        if (oamChannel != null && oamChannel.getEndpoint() != null && oamChannel.getEndpoint().getMetricsData() != null) {
            oamChannel.getEndpoint().getMetricsData().touchLastActivityInstant();
        }
    }
}
