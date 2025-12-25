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

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.ui.model.topology.base.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.ui.model.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.ui.model.topology.base.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.pubsub.common.ApplicationComponentIdMask;
import net.fhirfactory.dricats.internals.pubsub.common.EventTemporalWindow;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilterMask;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.PathwayResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


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
        ApplicationComponentSummary application = createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", ApplicationComponentSpecialisationEnum.SUBSYSTEM);
        LOG.info(".createSubsystemOne(): Adding Subsystem to Component Map -> key {}", application.resolveKey());
        getTestComponentServices().getComponents().put(application.resolveKey(), application);

        ApplicationComponentSummary applicationInstance = createApplicationComponent(application.getObjectID(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        addChild(application, applicationInstance);
        getTestComponentServices().getComponents().put(applicationInstance.resolveKey(), applicationInstance);
        LOG.info(".createSubsystemOne(): Adding Application Instance to Component Map -> key {}",applicationInstance.resolveKey());

        ApplicationComponentSummary messagingWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "InternalMessagingServices", "Handles inter-component messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, messagingWUPBlock);
        getTestComponentServices().getComponents().put(messagingWUPBlock.resolveKey(), messagingWUPBlock);
        LOG.info(".createSubsystemOne(): Adding Messaging Services to Component Map -> key {}", messagingWUPBlock.resolveKey());

        ApplicationComponentSummary oamWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "OAMServiceBlock", "Collects and serves metrics", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, oamWUPBlock);
        getTestComponentServices().getComponents().put(oamWUPBlock.resolveKey(), oamWUPBlock);
        LOG.info(".createSubsystemOne(): Adding OAM WUP Block to Component Map -> key {}", oamWUPBlock.resolveKey());

        ApplicationComponentSummary hl7v2Conduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v2Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, hl7v2Conduit);
        getTestComponentServices().getComponents().put(hl7v2Conduit.resolveKey(), hl7v2Conduit);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v2Conduit.resolveKey());

        ApplicationComponentSummary hl7v24InternalDistributor = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v24InternalDistributor", "Handles HL7v24 Message Distribution", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS);
        addChild(messagingWUPBlock, hl7v24InternalDistributor);
        getTestComponentServices().getComponents().put(hl7v24InternalDistributor.resolveKey(), hl7v24InternalDistributor);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalDistributor.resolveKey());

        ApplicationComponentSummary hl7v24InternalReceiver = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v24InternalReceiver", "Handles HL7v24 Message Distribution", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(messagingWUPBlock, hl7v24InternalReceiver);
        getTestComponentServices().getComponents().put(hl7v24InternalReceiver.resolveKey(), hl7v24InternalReceiver);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalReceiver.resolveKey());

        ApplicationComponentSummary fhirConduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "FHIR Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, fhirConduit);
        getTestComponentServices().getComponents().put(fhirConduit.resolveKey(), fhirConduit);
        LOG.info(".createSubsystemOne(): Adding FHIR Conduit to Component Map -> key {}", fhirConduit.resolveKey());

        ApplicationComponentSummary externalHL7v2Block = createApplicationComponent(applicationInstance.getObjectID(), "ExternalHL7v2Block", "Framework for Receiving & Sending MLLP Message Streams", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, externalHL7v2Block);
        getTestComponentServices().getComponents().put(externalHL7v2Block.resolveKey(), externalHL7v2Block);
        LOG.info(".createSubsystemOne(): Adding External HL7v2 WUP Block to Component Map -> key {}", externalHL7v2Block.resolveKey());

        ApplicationComponentSummary adtReceiver = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtReceiver);
        getTestComponentServices().getComponents().put(adtReceiver.resolveKey(), adtReceiver);
        LOG.info(".createSubsystemOne(): Adding ADT Receiver WUP to Component Map -> key {}", adtReceiver.resolveKey());

        ApplicationComponentSummary pasReceiverEndpoint = createApplicationComponent(externalHL7v2Block.getObjectID(), "PASReceiverEndpoint", "PAS Receiver Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(externalHL7v2Block, pasReceiverEndpoint);
        getTestComponentServices().getComponents().put(pasReceiverEndpoint.resolveKey(), pasReceiverEndpoint);
        LOG.info(".createSubsystemOne(): Adding PAS Receiver Endpoint WUP to Component Map -> key {}", pasReceiverEndpoint.resolveKey());

        ApplicationComponentSummary ehrReceiverEndpoint = createApplicationComponent(externalHL7v2Block.getObjectID(), "EHRReceiverEndpoint", "EHR Receiver Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES);
        addChild(externalHL7v2Block, ehrReceiverEndpoint);
        getTestComponentServices().getComponents().put(ehrReceiverEndpoint.resolveKey(), ehrReceiverEndpoint);
        LOG.info(".populateComponentMap(): Adding EHR Receiver Endpoint WUP to Component Map -> key {}", ehrReceiverEndpoint.resolveKey());

        ApplicationComponentSummary adtSender = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTSender", "ADT Trigger Event MLLP Message Sender", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtSender);
        getTestComponentServices().getComponents().put(adtSender.resolveKey(), adtSender);
        LOG.info(".createSubsystemOne(): Adding ADT Sender WUP to Component Map -> key {}", adtSender.resolveKey());

        ApplicationComponentSummary limsSenderEndpoint = createApplicationComponent(externalHL7v2Block.getObjectID(), "LIMSSenderEndpoint", "LIMS Sender Endpoint", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS);
        addChild(externalHL7v2Block, limsSenderEndpoint);
        getTestComponentServices().getComponents().put(limsSenderEndpoint.resolveKey(), limsSenderEndpoint);
        LOG.info(".createSubsystemOne(): Adding LIMS Sender Endpoint WUP to Component Map -> key {}", limsSenderEndpoint.resolveKey());

    }

    private void createSubsystemTwo (){
        String subsystemName = "SubsystemTwo";
        // Build a tiny component tree: application -> child1, child2

        ApplicationComponentSummary application = createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", ApplicationComponentSpecialisationEnum.SUBSYSTEM);
        ApplicationComponentSummary applicationInstance = createApplicationComponent(application.getObjectID(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        addChild(application, applicationInstance);

        ApplicationComponentSummary messagingWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "InternalMessagingServices", "Handles inter-component messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, messagingWUPBlock);

        ApplicationComponentSummary hl7v2Conduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v2Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, hl7v2Conduit);
        addInternalInterfaceComponent(hl7v2Conduit, "v24MessageGridIncoming", "HL7v24 Message Grid Incoming Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(hl7v2Conduit, "v24MessageGridOutgoing", "HL7v24 Message Grid Outgoing Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(hl7v2Conduit, "v24LocalIncoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2Conduit, "v24LocalDistribution", "HL7v24 Internal Outgoing Interface");
        ApplicationComponentSummary fhirConduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "FHIR Conduit", "Handles HL7v2 messaging", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(messagingWUPBlock, fhirConduit);
        addInternalInterfaceComponent(fhirConduit, "FHIRMessageGridIncoming", "FHIR Message Grid Incoming Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(fhirConduit, "FHIRMessageGridOutgoing", "FHIR Message Grid Outgoing Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(fhirConduit, "FHIRLocalIncoming", "FHIR Internal Incoming Interface");
        addInternalInterfaceComponent(fhirConduit, "FHIRLocalDistribution", "FHIR Internal Outgoing Interface");

        ApplicationComponentSummary oamWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "OAMServiceBlock", "Collects and serves metrics", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, oamWUPBlock);

        ApplicationComponentSummary transformers = createApplicationComponent(applicationInstance.getObjectID(), "MessageTransformers", "Message Transformation Service", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, transformers);
        ApplicationComponentSummary hl7v2OutboundTransform = createApplicationComponent(transformers.getObjectID(), "HL7v2OutboundTransform", "Handles HL7v2 Message Outbound Transformations", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(transformers, hl7v2OutboundTransform);
        addInternalInterfaceComponent(hl7v2OutboundTransform, "v24Incoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2OutboundTransform, "v24Outgoing", "HL7v24 Internal Outgoing Interface");
        ApplicationComponentSummary hl7v2InboundTransform = createApplicationComponent(transformers.getObjectID(), "HL7v2InboundTransform", "Handles HL7v2 Message Inbound Transformations", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(transformers, hl7v2InboundTransform);
        addInternalInterfaceComponent(hl7v2InboundTransform, "v24Incoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2InboundTransform, "v24Outgoing", "HL7v24 Internal Outgoing Interface");


        ApplicationComponentSummary externalHL7v2Block = createApplicationComponent(applicationInstance.getObjectID(), "ExtnerlHL7v2Block", "Framework for Receiving & Sending MLLP Message Streams", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        addChild(applicationInstance, externalHL7v2Block);
        ApplicationComponentSummary adtReceiver = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtReceiver);
        addExternalIngressInterfaceComponent(adtReceiver, "PAS-ADTAxx-EventReceiver", "HL7v24 ADT Trigger Event Receiver MLLP Interface", InterfaceComponentTypeEnum.MLLP_RECEIVER_INTERFACE);
        ApplicationComponentSummary adtSender = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTSender", "ADT Trigger Event MLLP Message Sender", ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        addChild(externalHL7v2Block, adtSender);
        addExternalEgressInterfaceComponent(adtSender, "PAS-ADTAxx-EventSender", "HL7v24 ADT Trigger Event Sender MLLP Interface", InterfaceComponentTypeEnum.MLLP_SENDER_INTERFACE);
        LOG.info(".populateComponentMap(): Adding ADT Sender WUP to Component Map -> key {}", adtSender.resolveKey());


    }

    protected ContentFilterMask createContentFilter(){
        ContentFilterMask newFilter = new ContentFilterMask();
        newFilter.getSupportedMediaTypes().add(MimeTypeEnum.TXT);
        TopicFilter topicFilter = new TopicFilter();
        FullyDistinguishedName qualifiedNameMask = new FullyDistinguishedName();
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Domain", "Health"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Standard", "HL7v2"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Group", "ADT"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Trigger", "*"));
        topicFilter.setMask(qualifiedNameMask);
        topicFilter.setIncludeContained(true);
        ApplicationComponentIdMask idMask = new ApplicationComponentIdMask();
        QualifiedNameMask sourceMask = new QualifiedNameMask();
        FullyDistinguishedName qualifiedName = new FullyDistinguishedName();
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

    protected void addEgressInterfaceComponent(ApplicationComponentSummary parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary child = createInterfaceComponent(parent.getObjectID(), name, doc, componentType);
        switch (componentType) {
            case MLLP_SENDER_INTERFACE:
            case INTERNAL_CAMEL_SENDER_INTERFACE:
            case HTTP_CLIENT_INTERFACE:
            case JGROUPS_MESSAGING_INTERFACE:
                EgressInterfaceComponentSummary egress = (EgressInterfaceComponentSummary) child;
                ContentFilterMask contentFilterMask = createContentFilter();
                ContentFilter contentFilter = new ContentFilter();
                contentFilter.setContentFilterMask(contentFilterMask);
                egress.getPublishedContent().getContentFilters().add(contentFilter);
                ((WUPSummary) parent).getEgressInterfaces().add(egress);
                break;
            default:
                // do nothing
                break;
        }
        getTestComponentServices().getComponents().put(child.resolveKey(), child);
    }

    protected InterfaceComponentSummary createInterfaceComponent(DistributableObjectId parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary child = null;
        switch (componentType) {
            case MLLP_SENDER_INTERFACE:
            case INTERNAL_CAMEL_SENDER_INTERFACE:
            case HTTP_CLIENT_INTERFACE:
            case JGROUPS_MESSAGING_INTERFACE:
                child = new EgressInterfaceComponentSummary();
                break;
            case MLLP_RECEIVER_INTERFACE:
            case INTERNAL_CAMEL_RECEIVER_INTERFACE:
            case HTTP_SERVER_INTERFACE:
            case INTERNAL_DATAGRID:
            case JGROUPS_RMI_INTERFACE:
                child = new IngressInterfaceComponentSummary();
                break;
            default:
                throw new IllegalArgumentException("Unsupported InterfaceComponentTypeEnum: " + componentType);
        }
        child.setSpecialization(componentType.getCode());
        child.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        child.getMetadata().setCreationDate(LocalDateTime.now());
        child.getMetadata().setLastUpdateDate(LocalDateTime.now());
        child.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        child.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        child.setParent(parent);
        child.setSpecialization(componentType.getName());
        FullyDistinguishedName qualifiedName;
        if(parent == null){
            qualifiedName = new FullyDistinguishedName();
        } else {
            qualifiedName = new FullyDistinguishedName(parent.getQualifiedName());
        }
        RelativeDistinguishedName unqName = new RelativeDistinguishedName(componentType.getName(), name);
        qualifiedName.appendUnqualifiedName(unqName);
        child.setName(name);
        child.setDocumentation(doc);
        child.setObjectID(new DistributableObjectId(qualifiedName));
        return child;
    }

    protected void addExternalIngressInterfaceComponent(ApplicationComponentSummary parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary receiver = createInterfaceComponent(parent.getObjectID(), name, doc, componentType);
        InterfaceComponentSummary createdInternalSender = createInterfaceComponent(parent.getObjectID(), name + "Sender", doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_SENDER_INTERFACE);
        if(parent instanceof WUPSummary){
            if(createdInternalSender instanceof EgressInterfaceComponentSummary){
                ((WUPSummary) parent).getEgressInterfaces().add((EgressInterfaceComponentSummary) createdInternalSender);
            }
            if( receiver instanceof IngressInterfaceComponentSummary){
                ((WUPSummary) parent).getIngressInterfaces().add((IngressInterfaceComponentSummary) receiver);
            }
        }
        getTestComponentServices().getComponents().put(receiver.resolveKey(), receiver);
        getTestComponentServices().getComponents().put(createdInternalSender.resolveKey(), createdInternalSender);
        LOG.info(".addExternalIngressInterfaceComponent(): Adding Ingress Interface to Component Map -> key {}", receiver.resolveKey());
        LOG.info(".addExternalIngressInterfaceComponent(): Adding Internal Sender Interface to Component Map -> key {}", createdInternalSender.resolveKey());
    }

    protected void addExternalEgressInterfaceComponent(ApplicationComponentSummary parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary createdInterface = createInterfaceComponent(parent.getObjectID(), name, doc, componentType);
        ContentFilterMask contentFilterMask = createContentFilter();
        ContentFilter contentFilter = new ContentFilter();
        contentFilter.setContentFilterMask(contentFilterMask);
        ((EgressInterfaceComponentSummary)createdInterface).getPublishedContent().getContentFilters().add(contentFilter);
        InterfaceComponentSummary createdInternalReceiver = createInterfaceComponent(parent.getObjectID(), name + "Receiver", doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE);
        if(parent instanceof WUPSummary){
            ((WUPSummary) parent).getEgressInterfaces().add((EgressInterfaceComponentSummary)createdInterface);
            ((WUPSummary) parent).getIngressInterfaces().add((IngressInterfaceComponentSummary) createdInternalReceiver);
        }
        getTestComponentServices().getComponents().put(createdInterface.resolveKey(), createdInterface);
        getTestComponentServices().getComponents().put(createdInternalReceiver.resolveKey(), createdInternalReceiver);
        LOG.info(".addExternalEgressInterfaceComponent(): Adding Egress Interface to Component Map -> key {}", createdInterface.resolveKey());
        LOG.info(".addExternalEgressInterfaceComponent(): Adding Internal Receiver Interface to Component Map -> key {}", createdInternalReceiver.resolveKey());
    }


    protected void addInternalInterfaceComponent(ApplicationComponentSummary parent, String name, String doc){
        InterfaceComponentSummary interfaceComponent = createInterfaceComponent(parent.getObjectID(), name, doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE);
        if(parent instanceof WUPSummary){
            if(interfaceComponent instanceof EgressInterfaceComponentSummary){
                ((WUPSummary) parent).getEgressInterfaces().add((EgressInterfaceComponentSummary)interfaceComponent);
            } else {
                ((WUPSummary) parent).getIngressInterfaces().add((IngressInterfaceComponentSummary)interfaceComponent);
            }
        }
        getTestComponentServices().getComponents().put(interfaceComponent.resolveKey(), interfaceComponent);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", interfaceComponent.resolveKey());
    }

    protected void addInternalInterfaceComponent(ApplicationComponentSummary parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary createdInterface = createInterfaceComponent(parent.getObjectID(), name, doc, componentType);
        if(parent instanceof WUPSummary){
            if(createdInterface instanceof EgressInterfaceComponentSummary){
                ((WUPSummary) parent).getEgressInterfaces().add((EgressInterfaceComponentSummary) createdInterface);
            } else {
                ((WUPSummary) parent).getIngressInterfaces().add((IngressInterfaceComponentSummary) createdInterface);
            }
        }
        getTestComponentServices().getComponents().put(createdInterface.resolveKey(), createdInterface);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", createdInterface.resolveKey());
    }


    private void addChild(ApplicationComponentSummary parent, ApplicationComponentSummary child){
        if(parent instanceof SubsystemSummary){
            SubsystemSummary subs = (SubsystemSummary) parent;
            if(child instanceof ApplicationClusterSummary){
                subs.getApplicationClusters().add(child.getObjectID());
            } else if(child instanceof ApplicationInstanceSummary){
                subs.getApplicationInstances().add(child.getObjectID());
            } else {
                subs.getApplicationInstances().add(child.getObjectID());
            }
        } else if(parent instanceof ApplicationClusterSummary){
            ApplicationClusterSummary cluster = (ApplicationClusterSummary) parent;
            if(child instanceof ApplicationInstanceSummary){
                cluster.getApplicationInstances().add(child.getObjectID());
            }
        } else if(parent instanceof ApplicationInstanceSummary){
            ApplicationInstanceSummary instance = (ApplicationInstanceSummary) parent;
            if(child instanceof WUPGroupSummary){
                instance.getWupGroups().add(child.getObjectID());
            }
        } else if(parent instanceof WUPGroupSummary){
            WUPGroupSummary group = (WUPGroupSummary) parent;
            if(child instanceof WUPSummary){
                group.getWorkUnitProcessors().add(child.getObjectID());
            }
        }
    }

    protected ApplicationComponentSummary createApplicationComponent(DistributableObjectId parent, String name, String doc, ApplicationComponentSpecialisationEnum componentType){
        ApplicationComponentSummary applicationComponent;
        switch (componentType) {
            case SUBSYSTEM:
                applicationComponent = new SubsystemSummary();
                break;
            case SUBSYSTEM_APPLICATION_CLUSTER:
                applicationComponent = new ApplicationClusterSummary();
                break;
            case SUBSYSTEM_APPLICATION_INSTANCE:
                applicationComponent = new ApplicationInstanceSummary();
                break;
            case SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP:
                applicationComponent = new WUPGroupSummary();
                break;
            case SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR:
            case SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES:
            case SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS:
                applicationComponent = new WUPSummary();
                break;
            default:
                applicationComponent = new WUPSummary();
        }
        applicationComponent.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        applicationComponent.getMetadata().setCreationDate(LocalDateTime.now());
        applicationComponent.getMetadata().setLastUpdateDate(LocalDateTime.now());
        applicationComponent.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        applicationComponent.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        applicationComponent.setSpecialization(componentType.getType());
        applicationComponent.setParent(parent);
        applicationComponent.setComponentStatus(new ApplicationComponentStatusSummary());
        applicationComponent.getComponentStatus().setComponentStatus("OK");
        applicationComponent.getComponentStatus().setComponentStatusDescription("Component Operating within normal parameters");
        FullyDistinguishedName qualifiedName;
        if(parent == null){
            qualifiedName = new FullyDistinguishedName();
        } else {
            qualifiedName = new FullyDistinguishedName(parent.getQualifiedName());
        }
        RelativeDistinguishedName unqName = new RelativeDistinguishedName(componentType.getType(), name);
        qualifiedName.appendUnqualifiedName(unqName);
        applicationComponent.setName(name);
        applicationComponent.setDocumentation(doc);
        applicationComponent.setObjectID(new DistributableObjectId(qualifiedName));
        getTestComponentServices().getComponents().put(applicationComponent.resolveKey(), applicationComponent);
        return applicationComponent;
    }
}
