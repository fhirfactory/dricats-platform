/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ApplicationFunction extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910104L;

    //
    // Attributes
    //

    private List<ElementReference> applicationServices;
    private List<ElementReference> applicationDataObjects;
    private ElementReference owner;

    //
     // Constructor(s)
    //

    public ApplicationFunction() {
        super();
        this.applicationServices = new ArrayList<>();
        this.applicationDataObjects = new ArrayList<>();
        this.setElementType(ElementTypeEnum.APPLICATION_FUNCTION);
    }

    public ApplicationFunction(String name, String documentation, String specialization) {
        this();
        setShortName(name);
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(specialization, name);
        DistinguishedName newName = new DistinguishedName();
        newName.appendUnqualifiedName(unqualifiedName);
        ElementIdentifier identifier = new ElementIdentifier(newName);
        setIdentifier(identifier);
        setDocumentation(documentation);
        setElementType(ElementTypeEnum.APPLICATION_FUNCTION);
        setSpecialization(specialization);
    }

    public ApplicationFunction(ElementReference parent, String name, String documentation, String specialization) {
        super(parent, name, documentation, specialization, new HashMap<>(), ElementTypeEnum.APPLICATION_FUNCTION);
        this.owner = parent;
        this.applicationServices = new ArrayList<>();
        this.applicationDataObjects = new ArrayList<>();
    }


    //
    // Getters and Setters
    //

    public List<ElementReference> getApplicationServices() {
        return applicationServices;
    }

    public void setApplicationServices(List<ElementReference> applicationServices) {
        this.applicationServices = applicationServices;
    }

    public List<ElementReference> getApplicationDataObjects() {
        return applicationDataObjects;
    }

    public void setApplicationDataObjects(List<ElementReference> applicationDataObjects) {
        this.applicationDataObjects = applicationDataObjects;
    }

    public ElementReference getOwner() {
        return owner;
    }

    public void setOwner(ElementReference owner) {
        this.owner = owner;
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("applicationServices", applicationServices)
                .append("applicationDataObjects", applicationDataObjects)
                .append("owner", owner)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationFunction that = (ApplicationFunction) o;
        return Objects.equals(applicationServices, that.applicationServices) && Objects.equals(applicationDataObjects, that.applicationDataObjects) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), applicationServices, applicationDataObjects, owner);
    }
}
