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

/**
 * ArchiMate Business Interface element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - owner: reference to owning role/actor/collaboration exposing the interface
 * - services: references to BusinessService(s) exposed via this interface
 */
public class BusinessInterface extends ElementBase implements Serializable {
    @Serial private static final long serialVersionUID = -12345678920104L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessInterface.class);

    // Element-specific attributes (by reference IDs)
    private ElementReference owner;
    private List<ElementReference> services;

    public BusinessInterface(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_INTERFACE);
        this.services = new ArrayList<>();
    }

    public ElementReference getOwner() { return owner; }
    public void setOwner(ElementReference owner) { this.owner = owner; }

    public List<ElementReference> getServices() { return services; }
    public void setServices(List<ElementReference> services) { this.services = services == null ? new ArrayList<>() : services; }
    public void addService(ElementReference service){ if(service == null){ return; } if(this.services == null){ this.services = new ArrayList<>(); } this.services.add(service); }
    public void clearServices(){ if(this.services != null){ this.services.clear(); } }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessInterface that = (BusinessInterface) o;
        return Objects.equals(owner, that.owner) && Objects.equals(services, that.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner, services);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("owner", owner)
                .append("services", services)
                .appendSuper(super.toString())
                .toString();
    }
}
