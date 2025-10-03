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
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.ui.serverside.topology.UITopologyCacheService;
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
        ApplicationComponentSummary application = createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", SoftwareComponentTypeEnum.SUBSYSTEM);
        LOG.info(".createSubsystemOne(): Adding Subsystem to Component Map -> key {}", application.resolveKey());
        getTestComponentServices().getComponents().put(application.resolveKey(), application);

        ApplicationComponentSummary applicationInstance = createApplicationComponent(application.getObjectID(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        application.getSubComponents().add(applicationInstance.getObjectID());
        getTestComponentServices().getComponents().put(applicationInstance.resolveKey(), applicationInstance);
        LOG.info(".createSubsystemOne(): Adding Application Instance to Component Map -> key {}",applicationInstance.resolveKey());

        ApplicationComponentSummary messagingWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "InternalMessagingServices", "Handles inter-component messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(messagingWUPBlock.getObjectID());
        getTestComponentServices().getComponents().put(messagingWUPBlock.resolveKey(), messagingWUPBlock);
        LOG.info(".createSubsystemOne(): Adding Messaging Services to Component Map -> key {}", messagingWUPBlock.resolveKey());

        ApplicationComponentSummary oamWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "OAMServiceBlock", "Collects and serves metrics", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(oamWUPBlock.getObjectID());
        getTestComponentServices().getComponents().put(oamWUPBlock.resolveKey(), oamWUPBlock);
        LOG.info(".createSubsystemOne(): Adding OAM WUP Block to Component Map -> key {}", oamWUPBlock.resolveKey());

        ApplicationComponentSummary hl7v2Conduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v2Conduit", "Handles HL7v2 messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        messagingWUPBlock.getSubComponents().add(hl7v2Conduit.getObjectID());
        getTestComponentServices().getComponents().put(hl7v2Conduit.resolveKey(), hl7v2Conduit);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v2Conduit.resolveKey());

        ApplicationComponentSummary hl7v24InternalDistributor = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v24InternalDistributor", "Handles HL7v24 Message Distribution", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_EGRESS);
        hl7v2Conduit.getSubComponents().add(hl7v24InternalDistributor.getObjectID());
        getTestComponentServices().getComponents().put(hl7v24InternalDistributor.resolveKey(), hl7v24InternalDistributor);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalDistributor.resolveKey());

        ApplicationComponentSummary hl7v24InternalReceiver = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v24InternalReceiver", "Handles HL7v24 Message Distribution", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_INGRES);
        hl7v2Conduit.getSubComponents().add(hl7v24InternalReceiver.getObjectID());
        getTestComponentServices().getComponents().put(hl7v24InternalReceiver.resolveKey(), hl7v24InternalReceiver);
        LOG.info(".createSubsystemOne(): Adding HL7v2 Conduit to Component Map -> key {}", hl7v24InternalReceiver.resolveKey());

        ApplicationComponentSummary fhirConduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "FHIR Conduit", "Handles HL7v2 messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        messagingWUPBlock.getSubComponents().add(fhirConduit.getObjectID());
        getTestComponentServices().getComponents().put(fhirConduit.resolveKey(), fhirConduit);
        LOG.info(".createSubsystemOne(): Adding FHIR Conduit to Component Map -> key {}", fhirConduit.resolveKey());

        ApplicationComponentSummary externalHL7v2Block = createApplicationComponent(applicationInstance.getObjectID(), "ExternalHL7v2Block", "Framework for Receiving & Sending MLLP Message Streams", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(externalHL7v2Block.getObjectID());
        getTestComponentServices().getComponents().put(externalHL7v2Block.resolveKey(), externalHL7v2Block);
        LOG.info(".createSubsystemOne(): Adding External HL7v2 WUP Block to Component Map -> key {}", externalHL7v2Block.resolveKey());

        ApplicationComponentSummary adtReceiver = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        externalHL7v2Block.getSubComponents().add(adtReceiver.getObjectID());
        getTestComponentServices().getComponents().put(adtReceiver.resolveKey(), adtReceiver);
        LOG.info(".createSubsystemOne(): Adding ADT Receiver WUP to Component Map -> key {}", adtReceiver.resolveKey());

        ApplicationComponentSummary pasReceiverEndpoint = createApplicationComponent(adtReceiver.getObjectID(), "PASReceiverEndpoint", "PAS Receiver Endpoint", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_INGRES);
        adtReceiver.getSubComponents().add(pasReceiverEndpoint.getObjectID());
        getTestComponentServices().getComponents().put(pasReceiverEndpoint.resolveKey(), pasReceiverEndpoint);
        LOG.info(".createSubsystemOne(): Adding PAS Receiver Endpoint WUP to Component Map -> key {}", pasReceiverEndpoint.resolveKey());

        ApplicationComponentSummary ehrReceiverEndpoint = createApplicationComponent(adtReceiver.getObjectID(), "EHRReceiverEndpoint", "EHR Receiver Endpoint", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_INGRES);
        adtReceiver.getSubComponents().add(ehrReceiverEndpoint.getObjectID());
        getTestComponentServices().getComponents().put(ehrReceiverEndpoint.resolveKey(), ehrReceiverEndpoint);
        LOG.info(".populateComponentMap(): Adding EHR Receiver Endpoint WUP to Component Map -> key {}", ehrReceiverEndpoint.resolveKey());

        ApplicationComponentSummary adtSender = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTSender", "ADT Trigger Event MLLP Message Sender", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        externalHL7v2Block.getSubComponents().add(adtSender.getObjectID());
        getTestComponentServices().getComponents().put(adtSender.resolveKey(), adtSender);
        LOG.info(".createSubsystemOne(): Adding ADT Sender WUP to Component Map -> key {}", adtSender.resolveKey());

        ApplicationComponentSummary limsSenderEndpoint = createApplicationComponent(adtSender.getObjectID(), "LIMSSenderEndpoint", "LIMS Sender Endpoint", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_EGRESS);
        adtSender.getSubComponents().add(limsSenderEndpoint.getObjectID());
        getTestComponentServices().getComponents().put(limsSenderEndpoint.resolveKey(), limsSenderEndpoint);
        LOG.info(".createSubsystemOne(): Adding LIMS Sender Endpoint WUP to Component Map -> key {}", limsSenderEndpoint.resolveKey());

    }

    private void createSubsystemTwo (){
        String subsystemName = "SubsystemTwo";
        // Build a tiny component tree: application -> child1, child2
        ApplicationComponentSummary application = createApplicationComponent(null, subsystemName, "Top-level DRICaTS platform stub for UI testing", SoftwareComponentTypeEnum.SUBSYSTEM);
        ApplicationComponentSummary applicationInstance = createApplicationComponent(application.getObjectID(), subsystemName + "Instance", "A Single Instance of the "+subsystemName+" Subsystem Application", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE);
        application.getSubComponents().add(applicationInstance.getObjectID());

        ApplicationComponentSummary messagingWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "InternalMessagingServices", "Handles inter-component messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(messagingWUPBlock.getObjectID());

        ApplicationComponentSummary hl7v2Conduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "HL7v2Conduit", "Handles HL7v2 messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        messagingWUPBlock.getSubComponents().add(hl7v2Conduit.getObjectID());
        addInternalInterfaceComponent(hl7v2Conduit, "v24MessageGridIncoming", "HL7v24 Message Grid Incoming Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(hl7v2Conduit, "v24MessageGridOutgoing", "HL7v24 Message Grid Outgoing Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(hl7v2Conduit, "v24LocalIncoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2Conduit, "v24LocalDistribution", "HL7v24 Internal Outgoing Interface");
        ApplicationComponentSummary fhirConduit = createApplicationComponent(messagingWUPBlock.getObjectID(), "FHIR Conduit", "Handles HL7v2 messaging", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        messagingWUPBlock.getSubComponents().add(fhirConduit.getObjectID());
        addInternalInterfaceComponent(fhirConduit, "FHIRMessageGridIncoming", "FHIR Message Grid Incoming Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(fhirConduit, "FHIRMessageGridOutgoing", "FHIR Message Grid Outgoing Interface", InterfaceComponentTypeEnum.JGROUPS_MESSAGING_INTERFACE);
        addInternalInterfaceComponent(fhirConduit, "FHIRLocalIncoming", "FHIR Internal Incoming Interface");
        addInternalInterfaceComponent(fhirConduit, "FHIRLocalDistribution", "FHIR Internal Outgoing Interface");

        ApplicationComponentSummary oamWUPBlock = createApplicationComponent(applicationInstance.getObjectID(), "OAMServiceBlock", "Collects and serves metrics", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(oamWUPBlock.getObjectID());

        ApplicationComponentSummary transformers = createApplicationComponent(applicationInstance.getObjectID(), "MessageTransformers", "Message Transformation Service", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(transformers.getObjectID());
        ApplicationComponentSummary hl7v2OutboundTransform = createApplicationComponent(transformers.getObjectID(), "HL7v2OutboundTransform", "Handles HL7v2 Message Outbound Transformations", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        transformers.getSubComponents().add(hl7v2Conduit.getObjectID());
        addInternalInterfaceComponent(hl7v2OutboundTransform, "v24Incoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2OutboundTransform, "v24Outgoing", "HL7v24 Internal Outgoing Interface");
        ApplicationComponentSummary hl7v2InboundTransform = createApplicationComponent(transformers.getObjectID(), "HL7v2InboundTransform", "Handles HL7v2 Message Inbound Transformations", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        transformers.getSubComponents().add(hl7v2InboundTransform.getObjectID());
        addInternalInterfaceComponent(hl7v2InboundTransform, "v24Incoming", "HL7v24 Internal Incoming Interface");
        addInternalInterfaceComponent(hl7v2InboundTransform, "v24Outgoing", "HL7v24 Internal Outgoing Interface");


        ApplicationComponentSummary externalHL7v2Block = createApplicationComponent(applicationInstance.getObjectID(), "ExtnerlHL7v2Block", "Framework for Receiving & Sending MLLP Message Streams", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);
        applicationInstance.getSubComponents().add(externalHL7v2Block.getObjectID());
        ApplicationComponentSummary adtReceiver = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTReceiver", "ADT Trigger Event MLLP Message Receiver", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        externalHL7v2Block.getSubComponents().add(adtReceiver.getObjectID());
        addInternalInterfaceComponent(adtReceiver, "v24InternalBroadcast", "HL7v24 Internal Broadcast Interface");
        addInternalInterfaceComponent(adtReceiver, "PAS-ADTAxx-EventReceiver", "HL7v24 ADT Trigger Event Receiver MLLP Interface", InterfaceComponentTypeEnum.MLLP_RECEIVER_INTERFACE);
        ApplicationComponentSummary adtSender = createApplicationComponent(externalHL7v2Block.getObjectID(), "ADTSender", "ADT Trigger Event MLLP Message Sender", SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);
        externalHL7v2Block.getSubComponents().add(adtSender.getObjectID());
        addInternalInterfaceComponent(adtSender, "PAS-ADTAxx-EventSender", "HL7v24 ADT Trigger Event Sender MLLP Interface", InterfaceComponentTypeEnum.MLLP_SENDER_INTERFACE);
        addInternalInterfaceComponent(adtSender, "v24InternalReceiver", "HL7v24 Internal Incoming Interface");
        LOG.info(".populateComponentMap(): Adding ADT Sender WUP to Component Map -> key {}", adtSender.resolveKey());


    }

    protected InterfaceComponentSummary createInterfaceComponent(DistributableObjectId parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary child = new InterfaceComponentSummary();
        child.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        child.getMetadata().setCreationDate(LocalDateTime.now());
        child.getMetadata().setLastUpdateDate(LocalDateTime.now());
        child.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        child.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        child.setParent(parent);
        child.setSpecialization(componentType.getName());
        QualifiedName qualifiedName = null;
        if(parent == null){
            qualifiedName = new QualifiedName();
        } else {
            qualifiedName = new QualifiedName(parent.getQualifiedName());
        }
        UnqualifiedName unqName = new UnqualifiedName(componentType.getName(), name);
        qualifiedName.appendUnqualifiedName(unqName);
        child.setName(name);
        child.setDocumentation(doc);
        child.setObjectID(new DistributableObjectId(qualifiedName));
        return child;
    }

    protected void addInternalInterfaceComponent(ApplicationComponentSummary parent, String name, String doc){
        InterfaceComponentSummary receiver = createInterfaceComponent(parent.getObjectID(), name, doc, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE);
        parent.getInterfaces().add(receiver.getObjectID());
        getTestComponentServices().getComponents().put(receiver.resolveKey(), receiver);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", receiver.resolveKey());
    }
    protected void addInternalInterfaceComponent(ApplicationComponentSummary parent, String name, String doc, InterfaceComponentTypeEnum componentType){
        InterfaceComponentSummary receiver = createInterfaceComponent(parent.getObjectID(), name, doc, componentType);
        parent.getInterfaces().add(receiver.getObjectID());
        getTestComponentServices().getComponents().put(receiver.resolveKey(), receiver);
        LOG.info(".populateComponentMap(): Adding Internal Receiver Interface to Component Map -> key {}", receiver.resolveKey());
    }


    protected ApplicationComponentSummary createApplicationComponent(DistributableObjectId parent, String name, String doc, SoftwareComponentTypeEnum componentType){
        ApplicationComponentSummary applicationComponent = new ApplicationComponentSummary();
        applicationComponent.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        applicationComponent.getMetadata().setCreationDate(LocalDateTime.now());
        applicationComponent.getMetadata().setLastUpdateDate(LocalDateTime.now());
        applicationComponent.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        applicationComponent.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        applicationComponent.setParent(parent);
        applicationComponent.setSpecialization(componentType.getType());
        QualifiedName qualifiedName = null;
        if(parent == null){
            qualifiedName = new QualifiedName();
        } else {
            qualifiedName = new QualifiedName(parent.getQualifiedName());
        }
        UnqualifiedName unqName = new UnqualifiedName(componentType.getType(), name);
        qualifiedName.appendUnqualifiedName(unqName);
        applicationComponent.setName(name);
        applicationComponent.setDocumentation(doc);
        applicationComponent.setObjectID(new DistributableObjectId(qualifiedName));
        getTestComponentServices().getComponents().put(applicationComponent.resolveKey(), applicationComponent);
        return applicationComponent;
    }
}
