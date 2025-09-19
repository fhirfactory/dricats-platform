/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.internals.reference.layers.application;

import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.reference.common.ElementBase;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an Application Interface in the Archimate application layer.
 * An Application Interface represents a point of access where application services
 * are made available to a user, another application component, or a node.
 */
public class ApplicationInterface extends ElementBase {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910103L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInterface.class);

    //
    // Constants
    //


    // ArchiMate attributes for Application Interface (by reference IDs)
    // - owner: reference to owning element (typically an ApplicationComponent)
    // - services: references to ApplicationService(s) exposed/required by this interface

    private DistributableObjectId owner;
    private List<DistributableObjectId> services;


    //
    // Constructor(s)
    //
    public ApplicationInterface() {
        super();
        this.services = new ArrayList<>();
        this.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        getLogger().trace("ApplicationInterface(): constructed");
    }

    public ApplicationInterface(String name, String documentation, String specialization) {
        super();
        setName(name);
        setDocumentation(documentation);
        setSpecialization(specialization);
        this.services = new ArrayList<>();
        this.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        getLogger().trace("ApplicationInterface(name, documentation, specialization): constructed");
    }

    public ApplicationInterface(DistributableObjectId parent, String name, String documentation, String specialization) {
        super();
        setOwner(parent);
        setName(name);
        UnqualifiedName unqualifiedName = new UnqualifiedName();
        unqualifiedName.setQualifier(specialization);
        unqualifiedName.setValue(name);
        DistributableObjectId distributableObjectId = SerializationUtils.clone(parent);
        distributableObjectId.getQualifiedName().appendUnqualifiedName(unqualifiedName);
        setObjectID(distributableObjectId);
        setId(distributableObjectId.getQualifiedName().getCommonName());
        setDocumentation(documentation);
        setSpecialization(specialization);
        this.services = new ArrayList<>();
        this.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization): constructed");
    }

    public ApplicationInterface(DistributableObjectId parent, String name, String documentation, String specialization, URI uri) {
        this(parent, name, documentation,specialization);
        String uriString = uri.toString();
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    //
    // Bean Methods
    //
    protected Logger getLogger(){
        return LOG;
    }

    public DistributableObjectId getOwner() {
        return owner;
    }

    public void setOwner(DistributableObjectId owner) {
        this.owner = owner;
    }

    public List<DistributableObjectId> getServices() {
        return services;
    }

    public void setServices(List<DistributableObjectId> services) {
        this.services = services == null ? new ArrayList<>() : services;
    }




    //
    // Business Methods
    //

    public void addService(DistributableObjectId serviceId) {
        if (serviceId == null) { return; }
        if (this.services == null) { this.services = new ArrayList<>(); }
        this.services.add(serviceId);
    }

    //
    // Utility Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationInterface that = (ApplicationInterface) o;
        return Objects.equals(owner, that.owner) && Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner, services);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+getName()+'\''+
                ", ownerRef="+(owner==null?"null":owner)+
                ", servicesCount="+(services==null?0:services.size())+
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
