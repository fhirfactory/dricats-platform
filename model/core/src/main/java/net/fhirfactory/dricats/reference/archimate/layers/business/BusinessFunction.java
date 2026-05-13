/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

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

public class BusinessFunction extends ElementBase implements Serializable {
    @Serial private static final long serialVersionUID = -12345678920106L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessFunction.class);

    // ArchiMate-relevant attributes for BusinessFunction
    // - businessServices: references to BusinessService(s) realized by this function
    // - businessObjects: references to BusinessObject(s) used by this function
    // - owner: owning actor/role
    private List<ElementReference> businessServices;
    private List<ElementReference> businessObjects;
    private ElementReference owner;

    public BusinessFunction(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_FUNCTION);
        this.businessServices = new ArrayList<>();
        this.businessObjects = new ArrayList<>();
    }

    public List<ElementReference> getBusinessServices() { return businessServices; }
    public void setBusinessServices(List<ElementReference> businessServices) { this.businessServices = businessServices; }

    public List<ElementReference> getBusinessObjects() { return businessObjects; }
    public void setBusinessObjects(List<ElementReference> businessObjects) { this.businessObjects = businessObjects; }

    public ElementReference getOwner() { return owner; }
    public void setOwner(ElementReference owner) { this.owner = owner; }

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
