package net.fhirfactory.dricats.platform.middleware.jgroups;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.messaging.interfaces.LocalMessageServerInterface;
import net.fhirfactory.dricats.internals.model.oam.interfaces.LocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.model.software.SoftwareComponent;
import net.fhirfactory.dricats.internals.model.software.interfaces.MiddlewareComponentInterface;
import net.fhirfactory.dricats.internals.model.software.interfaces.SubsystemInterface;
import net.fhirfactory.dricats.internals.model.tasking.interfaces.LocalTaskServerInterface;
import net.fhirfactory.dricats.platform.configuration.LocalConfigurationServer;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelConfiguration;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JChannelTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDateTime;

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
    private JChannelEndpoint metricsChannel;
    private JChannelEndpoint messagingChannel;
    private JChannelEndpoint rpcChannel;
    private boolean initialized;
    private LocalDateTime startupInstant;
    private SoftwareComponent topologyComponent;


    //
    // Injected Attributes
    //
    @Inject
    private LocalConfigurationServer localConfigurationServer;
    @Inject
    private LocalMessageServerInterface localMessageServer;
    @Inject
    private LocalMetricsServerInterface localMetricsServer;
    @Inject
    private LocalTaskServerInterface localTaskServer;
    @Inject
    private SubsystemInterface subsystemComponent;
    @Inject
    private MiddlewareComponentInterface middlewareComponent;

    //
    // Constructor(s)
    //
    public JGroupsNodeManager() {
        startupInstant = LocalDateTime.now();
    }

    //
    // Post Construct / Initialisation
    //

    @PostConstruct
    public void initialise(){
        getLogger().debug(".initialise(): Entry");
        if(isInitialized()){
            getLogger().debug(".initialise(): Exit, already initialised");
            return;
        }
        getLogger().trace(".initialise(): Begin Initialisation");
        boolean foundConfigurationObjects = false;
        while(!foundConfigurationObjects){
            Object messagingConfigObject = localConfigurationServer.getConfigurationObject(JChannelTypeEnum.JGROUPS_ENDPOINT_TYPE_MESSAGING.getConfigObjectName());
            Object taskingConfigObject = localConfigurationServer.getConfigurationObject(JChannelTypeEnum.JGROUPS_ENDPOINT_TYPE_TASKING.getConfigObjectName());
            Object metricsConfigObject = localConfigurationServer.getConfigurationObject(JChannelTypeEnum.JGROUPS_ENDPOINT_TYPE_METRICS.getConfigObjectName());
            if( messagingConfigObject == null || taskingConfigObject == null || metricsConfigObject == null ){

            }

        }
    }

    //
    // Build My TopologyComponent
    //

    protected SoftwareComponent createTopologyComponent() {
        DistributableObjectReference parentReference = new DistributableObjectReference(middlewareComponent.getMiddlewareComponent().getInstanceId());
        SoftwareComponent me = new SoftwareComponent()

    }

    //
    // Build JChannelEndpoint
    //

    protected JChannelEndpoint createJChannelEndpoint(JChannelConfiguration configObject){

        JChannelEndpoint endpoint = new JChannelEndpoint()

    }

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
