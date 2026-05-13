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

public class BusinessRole extends ElementBase {
    //
     // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678920102L;

    // ArchiMate-relevant attributes for BusinessRole
    // - responsibilities: free-text list of responsibilities
    // - actors: references to BusinessActor(s) that realize this role
    // - owner: owning organizational entity (e.g., actor/collaboration)
    private List<String> responsibilities;
    private List<ElementReference> actors;
    private ElementReference owner;

    //
    // Constructor(s)
    //

    public BusinessRole(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_ROLE);
        this.responsibilities = new ArrayList<>();
        this.actors = new ArrayList<>();
    }

    //
     // Bean Methods
    //

    public List<String> getResponsibilities() { return responsibilities; }
    public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }

    public List<ElementReference> getActors() { return actors; }
    public void setActors(List<ElementReference> actors) { this.actors = actors; }

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
        BusinessRole that = (BusinessRole) o;
        return Objects.equals(responsibilities, that.responsibilities) &&
                Objects.equals(actors, that.actors) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), responsibilities, actors, owner);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("responsibilities", responsibilities)
                .append("actors", actors)
                .append("owner", owner)
                .appendSuper(super.toString())
                .toString();
    }
}
