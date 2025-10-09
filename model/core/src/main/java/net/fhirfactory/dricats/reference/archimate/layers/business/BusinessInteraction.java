/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ArchiMate Business Interaction element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - collaboration: reference to a BusinessCollaboration this interaction belongs to
 * - participants: references to participating roles/actors/collaborations
 * - usedServices: references to BusinessService(s) used by this interaction
 * - accessBusinessObjects: references to BusinessObject(s) accessed by this interaction
 */
public class BusinessInteraction extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920107L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessInteraction.class);

    // Element-specific attributes (by reference IDs)
    private DistributableObjectId collaboration;
    private List<DistributableObjectId> participants;
    private List<DistributableObjectId> usedServices;
    private List<DistributableObjectId> accessBusinessObjects;

    public BusinessInteraction(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_INTERACTION);
        this.participants = new ArrayList<>();
        this.usedServices = new ArrayList<>();
        this.accessBusinessObjects = new ArrayList<>();
    }

    public DistributableObjectId getCollaboration() { return collaboration; }
    public void setCollaboration(DistributableObjectId collaboration) { this.collaboration = collaboration; }

    public List<DistributableObjectId> getParticipants() { return participants; }
    public void setParticipants(List<DistributableObjectId> participants) { this.participants = participants == null ? new ArrayList<>() : participants; }
    public void addParticipant(DistributableObjectId participant) { if(participant == null){ return; } if(this.participants == null){ this.participants = new ArrayList<>(); } this.participants.add(participant); }
    public void clearParticipants(){ if(this.participants != null){ this.participants.clear(); } }

    public List<DistributableObjectId> getUsedServices() { return usedServices; }
    public void setUsedServices(List<DistributableObjectId> usedServices) { this.usedServices = usedServices == null ? new ArrayList<>() : usedServices; }
    public void addUsedService(DistributableObjectId service) { if(service == null){ return; } if(this.usedServices == null){ this.usedServices = new ArrayList<>(); } this.usedServices.add(service); }
    public void clearUsedServices(){ if(this.usedServices != null){ this.usedServices.clear(); } }

    public List<DistributableObjectId> getAccessBusinessObjects() { return accessBusinessObjects; }
    public void setAccessBusinessObjects(List<DistributableObjectId> accessBusinessObjects) { this.accessBusinessObjects = accessBusinessObjects == null ? new ArrayList<>() : accessBusinessObjects; }
    public void addAccessBusinessObject(DistributableObjectId businessObject) { if(businessObject == null){ return; } if(this.accessBusinessObjects == null){ this.accessBusinessObjects = new ArrayList<>(); } this.accessBusinessObjects.add(businessObject); }
    public void clearAccessBusinessObjects(){ if(this.accessBusinessObjects != null){ this.accessBusinessObjects.clear(); } }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessInteraction that = (BusinessInteraction) o;
        return Objects.equals(collaboration, that.collaboration) &&
                Objects.equals(participants, that.participants) &&
                Objects.equals(usedServices, that.usedServices) &&
                Objects.equals(accessBusinessObjects, that.accessBusinessObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), collaboration, participants, usedServices, accessBusinessObjects);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+getName()+'\''+
                ", collaborationRef="+(collaboration==null?"null":collaboration)+
                ", participantsCount="+(participants==null?0:participants.size())+
                ", usedServicesCount="+(usedServices==null?0:usedServices.size())+
                ", accessBusinessObjectsCount="+(accessBusinessObjects==null?0:accessBusinessObjects.size())+
                ", documentation='"+getDocumentation()+'\''+
                ", specialization='"+getSpecialization()+'\''+
                ", properties="+getProperties()+
                ", objectID="+getObjectID()+
                ", metadata="+getMetadata()+
                '}';
    }
}
