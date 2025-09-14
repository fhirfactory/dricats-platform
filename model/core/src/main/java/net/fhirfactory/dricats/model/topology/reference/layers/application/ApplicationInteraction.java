/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.layers.application;

import net.fhirfactory.dricats.model.topology.reference.common.ElementBase;
import net.fhirfactory.dricats.model.common.DistributableObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationInteraction extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678910106L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInteraction.class);

    // ArchiMate attributes for Application Interaction (by reference IDs)
    // - collaboration: reference to an ApplicationCollaboration this interaction belongs to
    // - participants: references to participating ApplicationComponent(s) or collaborations
    // - usedServices: references to ApplicationService(s) used by this interaction
    // - accessDataObjects: references to ApplicationDataObject(s) accessed by this interaction

    private DistributableObjectId collaboration;
    private List<DistributableObjectId> participants;
    private List<DistributableObjectId> usedServices;
    private List<DistributableObjectId> accessDataObjects;

    public ApplicationInteraction() {
        super();
        this.participants = new ArrayList<>();
        this.usedServices = new ArrayList<>();
        this.accessDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationInteraction(): constructed");
    }

    protected Logger getLogger(){
        return LOG;
    }

    public DistributableObjectId getCollaboration() {
        return collaboration;
    }

    public void setCollaboration(DistributableObjectId collaboration) {
        this.collaboration = collaboration;
    }

    public List<DistributableObjectId> getParticipants() {
        return participants;
    }

    public void setParticipants(List<DistributableObjectId> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public void addParticipant(DistributableObjectId participantId) {
        if (participantId == null) { return; }
        if (this.participants == null) { this.participants = new ArrayList<>(); }
        this.participants.add(participantId);
    }

    public List<DistributableObjectId> getUsedServices() {
        return usedServices;
    }

    public void setUsedServices(List<DistributableObjectId> usedServices) {
        this.usedServices = usedServices == null ? new ArrayList<>() : usedServices;
    }

    public void addUsedService(DistributableObjectId serviceId) {
        if (serviceId == null) { return; }
        if (this.usedServices == null) { this.usedServices = new ArrayList<>(); }
        this.usedServices.add(serviceId);
    }

    public List<DistributableObjectId> getAccessDataObjects() {
        return accessDataObjects;
    }

    public void setAccessDataObjects(List<DistributableObjectId> accessDataObjects) {
        this.accessDataObjects = accessDataObjects == null ? new ArrayList<>() : accessDataObjects;
    }

    public void addAccessDataObject(DistributableObjectId dataObjectId) {
        if (dataObjectId == null) { return; }
        if (this.accessDataObjects == null) { this.accessDataObjects = new ArrayList<>(); }
        this.accessDataObjects.add(dataObjectId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationInteraction that = (ApplicationInteraction) o;
        return Objects.equals(collaboration, that.collaboration) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(usedServices, that.usedServices) &&
                Objects.equals(accessDataObjects, that.accessDataObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), collaboration, participants, usedServices, accessDataObjects);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+getName()+'\''+
                ", collaborationRef="+(collaboration==null?"null":collaboration)+
                ", participantsCount="+(participants==null?0:participants.size())+
                ", usedServicesCount="+(usedServices==null?0:usedServices.size())+
                ", accessDataObjectsCount="+(accessDataObjects==null?0:accessDataObjects.size())+
                ", documentation='"+getDocumentation()+'\''+
                ", specialization='"+getSpecialization()+'\''+
                ", properties="+getProperties()+
                ", objectID="+getObjectID()+
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }
}
