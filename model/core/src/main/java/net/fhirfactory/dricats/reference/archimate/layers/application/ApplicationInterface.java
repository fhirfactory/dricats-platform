/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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

    private ElementReference owner;
    private List<ElementReference> services;
    private List<ApplicationDataObject> supportedDataObjects;


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

    public ApplicationInterface(ElementReference parent, String name, String documentation, String specialization) {
        super();
        setOwner(parent);
        setName(name);
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName();
        unqualifiedName.setQualifier(specialization);
        unqualifiedName.setValue(name);
        ElementReference reference = SerializationUtils.clone(parent);
        FullyDistinguishedName newName = reference.getLocalObjectId().getFullyDistinguishedName();
        newName.appendUnqualifiedName(unqualifiedName);
        setLocalId(new ObjectId(newName));
        setDocumentation(documentation);
        setSpecialization(specialization);
        this.services = new ArrayList<>();
        this.setElementType(ElementTypeEnum.APPLICATION_INTERFACE);
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization): constructed");
    }

    public ApplicationInterface(ElementReference parent, String name, String documentation, String specialization, URI uri) {
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

    public ElementReference getOwner() {
        return owner;
    }

    public void setOwner(ElementReference owner) {
        this.owner = owner;
    }

    public List<ElementReference> getServices() {
        return services;
    }

    public void setServices(List<ElementReference> services) {
        this.services = services == null ? new ArrayList<>() : services;
    }

    public List<ApplicationDataObject> getSupportedDataObjects() {
        return supportedDataObjects;
    }

    public void setSupportedDataObjects(List<ApplicationDataObject> supportedDataObjects) {
        this.supportedDataObjects = supportedDataObjects == null ? new ArrayList<>() : supportedDataObjects;
    }


    //
    // Business Methods
    //

    public void addService(ElementReference serviceId) {
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
        return new ToStringBuilder(this)
                .append("owner", getOwner())
                .append("services", getServices())
                .append("supportedDataObjects", getSupportedDataObjects())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getLocalId())
                .toString();
    }
}
