/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusinessService extends ElementBase {
    //
     // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678920109L;

    // ArchiMate-relevant attributes for BusinessService
    // - external: whether the service is externally visible
    // - serviceLevel: service level description (SLA)
    // - interfaces: BusinessInterface(s) exposing the service
    // - contracts: Contract(s) governing the service
    // - owner: actor/role that owns the service
    private boolean external;
    private String serviceLevel;
    private List<ElementReference> interfaces;
    private List<ElementReference> contracts;
    private ElementReference owner;

    //
     // Constructor(s)
    //

    public BusinessService(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_SERVICE);
        this.interfaces = new ArrayList<>();
        this.contracts = new ArrayList<>();
    }

    //
     // Bean Methods
    //

    public boolean isExternal() { return external; }
    public void setExternal(boolean external) { this.external = external; }

    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }

    public List<ElementReference> getInterfaces() { return interfaces; }
    public void setInterfaces(List<ElementReference> interfaces) { this.interfaces = interfaces; }

    public List<ElementReference> getContracts() { return contracts; }
    public void setContracts(List<ElementReference> contracts) { this.contracts = contracts; }

    public ElementReference getOwner() { return owner; }
    public void setOwner(ElementReference owner) { this.owner = owner; }

    //
    // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessService that = (BusinessService) o;
        return external == that.external &&
                Objects.equals(serviceLevel, that.serviceLevel) &&
                Objects.equals(interfaces, that.interfaces) &&
                Objects.equals(contracts, that.contracts) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), external, serviceLevel, interfaces, contracts, owner);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("external", external)
                .append("serviceLevel", serviceLevel)
                .append("interfaces", interfaces)
                .append("contracts", contracts)
                .append("owner", owner)
                .appendSuper(super.toString())
                .toString();
    }
}
