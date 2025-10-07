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

public class BusinessService extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920109L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessService.class);

    // ArchiMate-relevant attributes for BusinessService
    // - external: whether the service is externally visible
    // - serviceLevel: service level description (SLA)
    // - interfaces: BusinessInterface(s) exposing the service
    // - contracts: Contract(s) governing the service
    // - owner: actor/role that owns the service
    private boolean external;
    private String serviceLevel;
    private List<DistributableObjectId> interfaces;
    private List<DistributableObjectId> contracts;
    private DistributableObjectId owner;

    public BusinessService(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_SERVICE);
        this.interfaces = new ArrayList<>();
        this.contracts = new ArrayList<>();
    }

    public boolean isExternal() { return external; }
    public void setExternal(boolean external) { this.external = external; }

    public String getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(String serviceLevel) { this.serviceLevel = serviceLevel; }

    public List<DistributableObjectId> getInterfaces() { return interfaces; }
    public void setInterfaces(List<DistributableObjectId> interfaces) { this.interfaces = interfaces; }

    public List<DistributableObjectId> getContracts() { return contracts; }
    public void setContracts(List<DistributableObjectId> contracts) { this.contracts = contracts; }

    public DistributableObjectId getOwner() { return owner; }
    public void setOwner(DistributableObjectId owner) { this.owner = owner; }

    @Override
    protected Logger getLogger(){ return LOG; }

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
