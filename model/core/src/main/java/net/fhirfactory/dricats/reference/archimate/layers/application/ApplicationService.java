/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an Application Service in the Archimate application layer.
 * An Application Service exposes automated behavior that supports business or other application
 * elements. It is:
 * - exposed via one or more Application Interfaces
 * - provided (owned) by an Application Component
 * - realized by one or more Application Functions/Processes
 * - may use or manipulate Application Data Objects
 */
public class ApplicationService extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910107L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);

    //
    // Attributes (by reference IDs)
    //
    private ElementReference owner; // providing ApplicationComponent
    private List<ElementReference> interfaces; // exposing ApplicationInterface(s)
    private List<ElementReference> realizingApplicationFunctions; // realizing ApplicationFunction(s)/Process(es)
    private List<ElementReference> usedApplicationDataObjects; // ApplicationDataObject(s) used/exposed by the service

    //
    // Constructor(s)
    //
    public ApplicationService() {
        super();
        this.interfaces = new ArrayList<>();
        this.realizingApplicationFunctions = new ArrayList<>();
        this.usedApplicationDataObjects = new ArrayList<>();
        setElementType(ElementTypeEnum.APPLICATION_SERVICE);
        getLogger().trace("ApplicationService(): constructed");
    }

    public ApplicationService(String name, String documentation, String specialization) {
        super(name, documentation, specialization, ElementTypeEnum.APPLICATION_SERVICE);
        this.interfaces = new ArrayList<>();
        this.realizingApplicationFunctions = new ArrayList<>();
        this.usedApplicationDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationService(name, documentation, specialization): constructed");
    }

    public ApplicationService(ElementReference owner, String name, String documentation, String specialization) {
        super(name, documentation, specialization, ElementTypeEnum.APPLICATION_SERVICE);
        this.owner = owner;
        this.interfaces = new ArrayList<>();
        this.realizingApplicationFunctions = new ArrayList<>();
        this.usedApplicationDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationService(owner, name, documentation, specialization): constructed");
    }

    //
    // Bean Methods
    //
    protected Logger getLogger(){ return LOG; }

    public ElementReference getOwner() {
        return owner;
    }

    public void setOwner(ElementReference owner) {
        this.owner = owner;
    }

    public List<ElementReference> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<ElementReference> interfaces) {
        this.interfaces = interfaces == null ? new ArrayList<>() : interfaces;
    }

    public List<ElementReference> getRealizingApplicationFunctions() {
        return realizingApplicationFunctions;
    }

    public void setRealizingApplicationFunctions(List<ElementReference> realizingApplicationFunctions) {
        this.realizingApplicationFunctions = realizingApplicationFunctions == null ? new ArrayList<>() : realizingApplicationFunctions;
    }

    public List<ElementReference> getUsedApplicationDataObjects() {
        return usedApplicationDataObjects;
    }

    public void setUsedApplicationDataObjects(List<ElementReference> usedApplicationDataObjects) {
        this.usedApplicationDataObjects = usedApplicationDataObjects == null ? new ArrayList<>() : usedApplicationDataObjects;
    }

    //
    // Business Methods
    //
    public void addInterface(ElementReference interfaceId) {
        if (interfaceId == null) { return; }
        if (this.interfaces == null) { this.interfaces = new ArrayList<>(); }
        this.interfaces.add(interfaceId);
    }

    public void addRealizingApplicationFunction(ElementReference functionId) {
        if (functionId == null) { return; }
        if (this.realizingApplicationFunctions == null) { this.realizingApplicationFunctions = new ArrayList<>(); }
        this.realizingApplicationFunctions.add(functionId);
    }

    public void addUsedApplicationDataObject(ElementReference dataObjectId) {
        if (dataObjectId == null) { return; }
        if (this.usedApplicationDataObjects == null) { this.usedApplicationDataObjects = new ArrayList<>(); }
        this.usedApplicationDataObjects.add(dataObjectId);
    }

    //
    // Standard Methods
    //
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationService that = (ApplicationService) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(interfaces, that.interfaces) &&
                Objects.equals(realizingApplicationFunctions, that.realizingApplicationFunctions) &&
                Objects.equals(usedApplicationDataObjects, that.usedApplicationDataObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner, interfaces, realizingApplicationFunctions, usedApplicationDataObjects);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("owner", owner)
                .append("interfaces", interfaces)
                .append("realizingApplicationFunctions", realizingApplicationFunctions)
                .append("usedApplicationDataObjects", usedApplicationDataObjects)
                .appendSuper(super.toString())
                .toString();
    }
}
