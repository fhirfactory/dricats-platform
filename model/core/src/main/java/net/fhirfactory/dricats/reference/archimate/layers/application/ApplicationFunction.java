/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationFunction extends SimpleElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910104L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationFunction.class);

    //
     // Attributes
    //

    private List<DistributableObjectId> applicationServices;
    private List<DistributableObjectId> applicationDataObjects;
    private DistributableObjectId owner;

    //
     // Constructor(s)
    //

    public ApplicationFunction() {
        super();
        this.applicationServices = new ArrayList<>();
        this.applicationDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationFunction(): constructed");
    }

    public ApplicationFunction(String name, String documentation, String specialization) {
        super();
        setName(name);
        setDocumentation(documentation);
        setSpecialization(specialization);
    }

    public ApplicationFunction(DistributableObjectId parent, String name, String documentation, String specialization) {
        super(name, documentation, specialization, ElementTypeEnum.APPLICATION_FUNCTION);
        this.owner = parent;
        this.applicationServices = new ArrayList<>();
        this.applicationDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationFunction(parent, name, documentation, specialization): constructed");
    }


    //
    // Getters and Setters
    //
    protected Logger getLogger(){ return LOG; }

    public List<DistributableObjectId> getApplicationServices() {
        return applicationServices;
    }

    public void setApplicationServices(List<DistributableObjectId> applicationServices) {
        this.applicationServices = applicationServices;
    }

    public List<DistributableObjectId> getApplicationDataObjects() {
        return applicationDataObjects;
    }

    public void setApplicationDataObjects(List<DistributableObjectId> applicationDataObjects) {
        this.applicationDataObjects = applicationDataObjects;
    }

    public DistributableObjectId getOwner() {
        return owner;
    }

    public void setOwner(DistributableObjectId owner) {
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
