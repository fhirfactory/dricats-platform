package net.fhirfactory.dricats.platform.middleware.jgroups;

import net.fhirfactory.dricats.internals.events.interfaces.ILocalMessageService;
import net.fhirfactory.dricats.internals.oam.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.tasking.interfaces.LocalTaskServerInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.JGroupsInterface;
import net.fhirfactory.dricats.internals.topology.interfaces.MiddlewareComponentInterface;
import net.fhirfactory.dricats.internals.topology.interfaces.SolutionConfigurationInterface;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.platform.configuration.LocalConfigurationServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class JGroupsNodeManager {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsNodeManager.class);

    //
    // Constants
    //
    private static final Long INITIALISATION_WAIT_PERIOD = 5000L;
    private static final Long MAX_INITIALISATION_WAIT_PERIOD = 30000L;

    //
    // Attributes
    //
    private JChannelInterface metricsChannel;
    private JChannelInterface messagingChannel;
    private JChannelInterface rpcChannel;
    private boolean initialized;
    private LocalDateTime startupInstant;
    private JGroupsInterface applicationComponent;


    //
    // Injected Attributes
    //
    @Inject
    private LocalConfigurationServer localConfigurationServer;
    @Inject
    private ILocalMessageService localMessageServer;
    @Inject
    private ILocalMetricsServerInterface localMetricsServer;
    @Inject
    private LocalTaskServerInterface localTaskServer;
    @Inject
    private ISubsystem subsystemInterface;
    @Inject
    private MiddlewareComponentInterface middlewareComponent;
    @Inject
    private SolutionConfigurationInterface solutionConfigurationInterface;

    //
    // Constructor(s)
    //
    public JGroupsNodeManager() {
        startupInstant = LocalDateTime.now();
    }

    //
    // Post Construct / Initialisation
    //



    //
    // Build My TopologyComponent
    //

    protected JGroupsInterface createTopologyComponent() {
        // TODO: implement proper topology component build
        // Placeholder implementation to keep compilation passing
        return applicationComponent;
    }

    //
    // Build JChannelEndpoint
    //
 /**
    protected JChannelEndpoint createJChannelEndpoint(JChannelConfiguration configObject){

        JChannelEndpoint endpoint = new JChannelEndpoint();

    }
**/
    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return LOG;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

}
