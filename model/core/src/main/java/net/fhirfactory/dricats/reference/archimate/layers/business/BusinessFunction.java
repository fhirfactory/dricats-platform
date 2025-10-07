/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusinessFunction extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920106L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessFunction.class);

    // ArchiMate-relevant attributes for BusinessFunction
    // - businessServices: references to BusinessService(s) realized by this function
    // - businessObjects: references to BusinessObject(s) used by this function
    // - owner: owning actor/role
    private List<DistributableObjectId> businessServices;
    private List<DistributableObjectId> businessObjects;
    private DistributableObjectId owner;

    public BusinessFunction(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_FUNCTION);
        this.businessServices = new ArrayList<>();
        this.businessObjects = new ArrayList<>();
    }

    public List<DistributableObjectId> getBusinessServices() { return businessServices; }
    public void setBusinessServices(List<DistributableObjectId> businessServices) { this.businessServices = businessServices; }

    public List<DistributableObjectId> getBusinessObjects() { return businessObjects; }
    public void setBusinessObjects(List<DistributableObjectId> businessObjects) { this.businessObjects = businessObjects; }

    public DistributableObjectId getOwner() { return owner; }
    public void setOwner(DistributableObjectId owner) { this.owner = owner; }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessFunction that = (BusinessFunction) o;
        return Objects.equals(businessServices, that.businessServices) &&
                Objects.equals(businessObjects, that.businessObjects) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), businessServices, businessObjects, owner);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("businessServices", businessServices)
                .append("businessObjects", businessObjects)
                .append("owner", owner)
                .appendSuper(super.toString())
                .toString();
    }
}
