/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.ui.uitest.testdata;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.internals.pubsub.common.ApplicationComponentIdMask;
import net.fhirfactory.dricats.internals.pubsub.common.EventTemporalWindow;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilterMask;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.topology.implementation.factories.ApplicationComponentFactory;
import net.fhirfactory.dricats.internals.topology.implementation.factories.ApplicationInterfaceFactory;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.PathwayResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


@ApplicationScoped
public class TopologyTestResourceSetBuilder {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(PathwayResourceHandler.class);

    //
    // Attributes
    //
    private boolean initialized = false;

    @Inject
    private UITopologyCacheService uiTopologyCacheService;

    @Inject
    private ApplicationComponentFactory applicationComponentFactory;

    @Inject
    private WorkUnitProcessorTestResourceBuilder wupResourceBuilder;

    @Inject
    private ApplicationInterfaceFactory interfaceFactory;


    //
     // Constructor(s)
    //
    public TopologyTestResourceSetBuilder(){
        LOG.debug(".constructor(): Entry");
        LOG.debug(".constructor(): Exit");
    }

    //
    // Post Construct (for dependency injection)
    //
    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        if (!initialized) {
            createSubsystemOne();
            createSubsystemTwo();
            initialized = true;
        }
        LOG.debug(".initialise(): Exit");
    }

    //
    // Accessors and Mutators
    //

    public UITopologyCacheService getTestComponentServices(){
        return uiTopologyCacheService;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public Logger getLogger(){
        return LOG;
    }


    //
    // Test Data Loader
    //

    private void createSubsystemOne (){
        String subsystemName = "SubsystemOne";
        // Build a tiny component tree: application -> child1, child2
        ApplicationComponent application = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", ApplicationComponentSpecialisationEnum.SUBSYSTEM);
        LOG.info(".createSubsystemOne(): Adding Subsystem to Component Map -> key {}", application.resolveElementInstanceKey());
        uiTopologyCacheService.addComponent(application);

        ApplicationComponent applicationInstance = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(application.getReference(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        addChild(application, applicationInstance);
        uiTopologyCacheService.addComponent(applicationInstance);
        LOG.info(".createSubsystemOne(): Adding Application Instance to Component Map -> key {}",applicationInstance.resolveElementInstanceKey());

        ApplicationComponent messagingWUPBlock = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "InternalMessagingServices", "Handles inter-component messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, messagingWUPBlock);
        uiTopologyCacheService.addComponent(messagingWUPBlock);
        LOG.info(".createSubsystemOne(): Adding Messaging Services to Component Map -> key {}", messagingWUPBlock.resolveElementInstanceKey());

        ApplicationComponent oamWUPBlock = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "OAMServiceBlock", "Collects and serves metrics", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, oamWUPBlock);
        uiTopologyCacheService.addComponent(oamWUPBlock);
        LOG.info(".createSubsystemOne(): Adding OAM WUP Block to Component Map -> key {}", oamWUPBlock.resolveElementInstanceKey());

        ApplicationComponent hl7v2Conduit = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(messagingWUPBlock.getReference(), "HL7v2Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, hl7v2Conduit);
        uiTopologyCacheService.addComponent(hl7v2Conduit);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v2Conduit.resolveElementInstanceKey());

        ApplicationComponent hl7v24InternalDistributor = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(messagingWUPBlock.getReference(), "HL7v24InternalDistributor", "Handles HL7v24 Message Distribution", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS);
        addChild(messagingWUPBlock, hl7v24InternalDistributor);
        uiTopologyCacheService.addComponent(hl7v24InternalDistributor);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalDistributor.resolveElementInstanceKey());

        ApplicationComponent hl7v24InternalReceiver = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(messagingWUPBlock.getReference(), "HL7v24InternalReceiver", "Handles HL7v24 Message Distribution", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(messagingWUPBlock, hl7v24InternalReceiver);
        uiTopologyCacheService.addComponent(hl7v24InternalReceiver);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalReceiver.resolveElementInstanceKey());

        ApplicationComponent fhirConduit = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(messagingWUPBlock.getReference(), "FHIR-Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, fhirConduit);
        uiTopologyCacheService.addComponent(fhirConduit);
        LOG.info(".createSubsystemOne(): Adding FHIR Conduit to Component Map -> key {}", fhirConduit.resolveElementInstanceKey());

        ApplicationComponent externalHL7v2Block = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "ExternalHL7v2Block", "Framework for Receiving & Sending MLLP Message Streams", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, externalHL7v2Block);
        uiTopologyCacheService.addComponent(externalHL7v2Block);
        LOG.info(".createSubsystemOne(): Adding External HL7v2 WUP Block to Component Map -> key {}", externalHL7v2Block.resolveElementInstanceKey());

        ApplicationComponent adtReceiver = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtReceiver);
        uiTopologyCacheService.addComponent(adtReceiver);
        LOG.info(".createSubsystemOne(): Adding ADT Receiver WUP to Component Map -> key {}", adtReceiver.resolveElementInstanceKey());

        ApplicationComponent pasReceiverEndpoint = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "PASReceiverEndpoint", "PAS Receiver Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(externalHL7v2Block, pasReceiverEndpoint);
        uiTopologyCacheService.addComponent(pasReceiverEndpoint);
        LOG.info(".createSubsystemOne(): Adding PAS Receiver Endpoint WUP to Component Map -> key {}", pasReceiverEndpoint.resolveElementInstanceKey());

        ApplicationComponent ehrReceiverEndpoint = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "EHRReceiverEndpoint", "EHR Receiver Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(externalHL7v2Block, ehrReceiverEndpoint);
        uiTopologyCacheService.addComponent(ehrReceiverEndpoint);
        LOG.info(".populateComponentMap(): Adding EHR Receiver Endpoint WUP to Component Map -> key {}", ehrReceiverEndpoint.resolveElementInstanceKey());

        ApplicationComponent adtSender = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "ADTSender", "ADT Trigger Event MLLP Message Sender", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtSender);
        uiTopologyCacheService.addComponent(adtSender);
        LOG.info(".createSubsystemOne(): Adding ADT Sender WUP to Component Map -> key {}", adtSender.resolveElementInstanceKey());

        ApplicationComponent limsSenderEndpoint = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "LIMSSenderEndpoint", "LIMS Sender Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS);
        addChild(externalHL7v2Block, limsSenderEndpoint);
        uiTopologyCacheService.addComponent(limsSenderEndpoint);
        LOG.info(".createSubsystemOne(): Adding LIMS Sender Endpoint WUP to Component Map -> key {}", limsSenderEndpoint.resolveElementInstanceKey());

    }

    private void createSubsystemTwo (){
        getLogger().info(".createSubsystemTwo(): Entry");
        String subsystemName = "SubsystemTwo";
        // Build a tiny component tree: application -> child1, child2

        ApplicationComponent application = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", ApplicationComponentSpecialisationEnum.SUBSYSTEM);
        ApplicationComponent applicationInstance = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(application.getReference(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        addChild(application, applicationInstance);
        uiTopologyCacheService.addComponent(application);
        uiTopologyCacheService.addComponent(applicationInstance);


        //
        // Internal Messaging WUP Block
        //

        ApplicationComponent messagingWUPBlock = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "InternalMessagingServices", "Handles inter-component messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, messagingWUPBlock);
        uiTopologyCacheService.addComponent(messagingWUPBlock);

        WorkUnitProcessor hl7v2Conduit = wupResourceBuilder.createWUP(messagingWUPBlock.getReference(), "InternalMessageProcessor-HL7v24", "Handles HL7v2 messaging");
        addChild(messagingWUPBlock, hl7v2Conduit);
        uiTopologyCacheService.addComponent(hl7v2Conduit);
        wupResourceBuilder.createInternalIngressInterface(hl7v2Conduit, "MessageGrid-HL7v24Incoming", "HL7v24 Message Grid Incoming Interface");
        wupResourceBuilder.createInternalIngressInterface(hl7v2Conduit, "CamelDirect-HL7v24Incoming", "HL7v24 Internal Incoming Interface", "ADT", "*");
        wupResourceBuilder.createInternalEgressInterface(hl7v2Conduit, "CamelDirect-HL7v24Outgoing", "HL7v24 Internal Outgoing Interface");
        wupResourceBuilder.createInternalEgressInterface(hl7v2Conduit, "MessageGrid-HL7v24Outgoing", "HL7v24 Message Grid Outgoing Interface");

        WorkUnitProcessor fhirConduit = wupResourceBuilder.createWUP(messagingWUPBlock.getReference(), "FHIRConduit", "Handles FHIR messaging");
        addChild(messagingWUPBlock, fhirConduit);
        uiTopologyCacheService.addComponent(fhirConduit);
        wupResourceBuilder.createInternalIngressInterface(fhirConduit, "MessageGrid-FHIRIncoming", "FHIR Message Grid Incoming Interface");
        wupResourceBuilder.createInternalIngressInterface(fhirConduit, "CamelDirect-FHIRIncoming", "FHIR Internal Incoming Interface");
        wupResourceBuilder.createInternalEgressInterface(fhirConduit, "CamelDirect-FHIROutgoing", "FHIR Internal Outgoing Interface");
        wupResourceBuilder.createInternalEgressInterface(fhirConduit, "MessageGrid-FHIROutgoing", "FHIR Message Grid Outgoing Interface");

        //
        // OAM Work Unit Processor Block
        //

        ApplicationComponent oamWUPBlock = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "OAMServiceBlock", "Metrics, Topology and Pub/Sub Management", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, oamWUPBlock);
        uiTopologyCacheService.addComponent(oamWUPBlock);
        WorkUnitProcessor metricsServices = wupResourceBuilder.createWUP(oamWUPBlock.getReference(), "MetricsServices", "Metrics Capture, Synchronisation & Publishing");
        addChild(oamWUPBlock, metricsServices);
        uiTopologyCacheService.addComponent(metricsServices);
        wupResourceBuilder.createInternalEgressInterface(metricsServices, "MetricsGrid-Publishing", "Metrics Publishing Grid Interface");
        wupResourceBuilder.createInternalIngressInterface(metricsServices, "MetricsGrid-Ingestion", "Metrics Ingestion Grid Interface");

        WorkUnitProcessor topologyServices = wupResourceBuilder.createWUP(oamWUPBlock.getReference(), "TopologyServices", "Topology Capture, Synchronisation & Publishing");
        addChild(oamWUPBlock, topologyServices);
        uiTopologyCacheService.addComponent(topologyServices);
        wupResourceBuilder.createInternalEgressInterface(topologyServices, "TopologyGrid-Publishing", "Topology Publishing Grid Interface");
        wupResourceBuilder.createInternalIngressInterface(topologyServices, "TopologyGrid-Ingestion", "Topology Ingestion Grid Interface");


        //
         // Content Transformation Services Work Unit Processor Group
        //

        ApplicationComponent transformers = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "MsgTransform", "Message Transformation Service", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, transformers);
        uiTopologyCacheService.addComponent(transformers);

        WorkUnitProcessor hl7v2OutboundTransform = wupResourceBuilder.createWUP(transformers.getReference(), "HL7v2-Outbound", "Handles HL7v2 Message Outbound Transformations");
        addChild(transformers, hl7v2OutboundTransform);
        uiTopologyCacheService.addComponent(hl7v2OutboundTransform);
        wupResourceBuilder.createInternalIngressInterface(hl7v2OutboundTransform, "In-HL7v24", "HL7v24 Internal Incoming Interface");
        wupResourceBuilder.createInternalEgressInterface(hl7v2OutboundTransform, "Out-HL7v24", "HL7v24 Internal Outgoing Interface");

        WorkUnitProcessor hl7v2InboundTransform = wupResourceBuilder.createWUP(transformers.getReference(), "TransformWork-HL7v2Inbound", "Handles HL7v2 Message Inbound Transformations");
        addChild(transformers, hl7v2InboundTransform);
        uiTopologyCacheService.addComponent(hl7v2InboundTransform);
        wupResourceBuilder.createInternalIngressInterface(hl7v2InboundTransform, "In-HL7v24", "HL7v24 Internal Incoming Interface");
        wupResourceBuilder.createInternalEgressInterface(hl7v2InboundTransform, "Out-HL7v24", "HL7v24 Internal Outgoing Interface");


        ApplicationComponent externalHL7v2Block = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(applicationInstance.getReference(), "Interact", "Framework for Receiving & Sending External MLLP Message Streams", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, externalHL7v2Block);
        uiTopologyCacheService.addComponent(externalHL7v2Block);

        ApplicationComponent adtReceiver = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtReceiver);
        uiTopologyCacheService.addComponent(adtReceiver);

        addExternalIngressInterfaceComponent(adtReceiver, "PAS-ADTAxx-EventReceiver", "HL7v24 ADT Trigger Event Receiver MLLP Interface", InterfaceComponentTypeEnum.MLLP_RECEIVER_INTERFACE);

        ApplicationComponent adtSender = (ApplicationComponent) applicationComponentFactory.createApplicationComponent(externalHL7v2Block.getReference(), "ADTSender", "ADT Trigger Event MLLP Message Sender", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtSender);
        uiTopologyCacheService.addComponent(adtSender);

        addExternalEgressInterfaceComponent(adtSender, "PAS-ADTAxx-EventSender", "HL7v24 ADT Trigger Event Sender MLLP Interface", InterfaceComponentTypeEnum.MLLP_SENDER_INTERFACE);

        LOG.info(".createSubsystemTwo(): Exit");
    }

    protected ContentFilterMask createContentFilter(){
        ContentFilterMask newFilter = new ContentFilterMask();
        newFilter.getSupportedMediaTypes().add(MimeTypeEnum.TXT);
        TopicFilter topicFilter = new TopicFilter();
        DistinguishedName qualifiedNameMask = new DistinguishedName();
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Domain", "Health"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Standard", "HL7v2"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Group", "ADT"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Trigger", "*"));
        topicFilter.setMask(qualifiedNameMask);
        topicFilter.setIncludeContained(true);
        ApplicationComponentIdMask idMask = new ApplicationComponentIdMask();
        QualifiedNameMask sourceMask = new QualifiedNameMask();
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(new RelativeDistinguishedName("CamelRoute", "*"));
        sourceMask.setMask(qualifiedName);
        sourceMask.setIncludeContained(true);
        idMask.setComponentIdMask(sourceMask);
        newFilter.setInternalEventSource(idMask);
        newFilter.setEventFinalDestination("*");
        newFilter.setEventOrigin("*");
        newFilter.setTemporalWindow(new EventTemporalWindow());
        newFilter.getEventTopicFilters().add(topicFilter);
        return newFilter;
    }

    protected void addEgressInterfaceComponent(ApplicationComponent parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        WUPInterfaceBase child = createInterfaceComponent(parent.getReference(), name, doc, componentType);
        if(child instanceof EgressApplicationInterface){
            EgressApplicationInterface egress = (EgressApplicationInterface) child;
            ContentFilterMask contentFilterMask = createContentFilter();
            ContentFilter contentFilter = new ContentFilter();
            contentFilter.setContentFilterMask(contentFilterMask);
            if(egress.getPublishedContent() == null){
                egress.setPublishedContent(new net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.PublicationSet());
            }
            egress.getPublishedContent().getContentFilters().add(contentFilter);
            if(parent instanceof WorkUnitProcessor){
                ((WorkUnitProcessor) parent).getEgressInterfaces().add(egress.getReference());
            }
        }
        uiTopologyCacheService.addComponent(child);
    }

    protected WUPInterfaceBase createInterfaceComponent(ElementReference parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        WUPInterfaceBase child = (WUPInterfaceBase) interfaceFactory.createInterface(parent, name, doc, componentType, new HashMap<>());
        return child;
    }

    protected void addExternalIngressInterfaceComponent(ApplicationComponent parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        WUPInterfaceBase receiver = createInterfaceComponent(parent.getReference(), name, doc, componentType);
        WUPInterfaceBase createdInternalSender = createInterfaceComponent(parent.getReference(), name + "Sender", doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_SENDER_INTERFACE);
        if(parent instanceof WorkUnitProcessor){
            if(createdInternalSender instanceof EgressApplicationInterface){
                ((WorkUnitProcessor) parent).getEgressInterfaces().add(createdInternalSender.getReference());
            }
            if( receiver instanceof IngresApplicationInterface){
                ((WorkUnitProcessor) parent).getIngresInterfaces().add(receiver.getReference());
            }
        }
        uiTopologyCacheService.addComponent(receiver);
        uiTopologyCacheService.addComponent(createdInternalSender);
        LOG.info(".addExternalIngressInterfaceComponent(): Adding Ingress Interface to Component Map -> key {}", receiver.resolveElementInstanceKey());
        LOG.info(".addExternalIngressInterfaceComponent(): Adding Internal Sender Interface to Component Map -> key {}", createdInternalSender.resolveElementInstanceKey());
    }

    protected void addExternalEgressInterfaceComponent(ApplicationComponent parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        WUPInterfaceBase createdInterface = createInterfaceComponent(parent.getReference(), name, doc, componentType);
        if(createdInterface instanceof EgressApplicationInterface) {
            EgressApplicationInterface egress = (EgressApplicationInterface) createdInterface;
            ContentFilterMask contentFilterMask = createContentFilter();
            ContentFilter contentFilter = new ContentFilter();
            contentFilter.setContentFilterMask(contentFilterMask);
            if(egress.getPublishedContent() == null){
                egress.setPublishedContent(new net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.PublicationSet());
            }
            egress.getPublishedContent().getContentFilters().add(contentFilter);
        }
        WUPInterfaceBase createdInternalReceiver = createInterfaceComponent(parent.getReference(), name + "Receiver", doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE);
        if(parent instanceof WorkUnitProcessor){
            ((WorkUnitProcessor) parent).getEgressInterfaces().add(createdInterface.getReference());
            ((WorkUnitProcessor) parent).getIngresInterfaces().add(createdInternalReceiver.getReference());
        }
        uiTopologyCacheService.addComponent(createdInterface);
        uiTopologyCacheService.addComponent(createdInternalReceiver);
        LOG.info(".addExternalEgressInterfaceComponent(): Adding Egress Interface to Component Map -> key {}", createdInterface.resolveElementInstanceKey());
        LOG.info(".addExternalEgressInterfaceComponent(): Adding Internal Receiver Interface to Component Map -> key {}", createdInternalReceiver.resolveElementInstanceKey());
    }


    protected void addInternalInterfaceComponent(ApplicationComponent parent, String name, String doc){
        WUPInterfaceBase interfaceComponent = createInterfaceComponent(parent.getReference(), name, doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE);
        if(parent instanceof WorkUnitProcessor){
            if(interfaceComponent instanceof EgressApplicationInterface){
                ((WorkUnitProcessor) parent).getEgressInterfaces().add(interfaceComponent.getReference());
            } else {
                ((WorkUnitProcessor) parent).getIngresInterfaces().add(interfaceComponent.getReference());
            }
        }
        uiTopologyCacheService.addComponent(interfaceComponent);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", interfaceComponent.resolveElementInstanceKey());
    }

    protected void addInternalInterfaceComponent(ApplicationComponent parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        WUPInterfaceBase createdInterface = createInterfaceComponent(parent.getReference(), name, doc, componentType);
        if(parent instanceof WorkUnitProcessor){
            if(createdInterface instanceof EgressApplicationInterface){
                ((WorkUnitProcessor) parent).getEgressInterfaces().add(createdInterface.getReference());
            } else {
                ((WorkUnitProcessor) parent).getIngresInterfaces().add( createdInterface.getReference());
            }
        }
        uiTopologyCacheService.addComponent(createdInterface);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", createdInterface.resolveElementInstanceKey());
    }


    private void addChild(ApplicationComponent parent, ApplicationComponent child){
        if(parent != null && child != null) {
            parent.addSubComponent(child.getReference());
        }
    }
}


