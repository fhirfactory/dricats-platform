/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.datatypes.Address;
import net.fhirfactory.dricats.internals.datatypes.CodeableConcept;
import net.fhirfactory.dricats.internals.datatypes.ContactPoint;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusinessActor extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678920101L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessActor.class);

    // ArchiMate-relevant attributes
    // - category: classification of the actor (e.g., person, organization)
    // - internal: whether the actor is internal to the enterprise (visibility)
    // - roles: roles fulfilled by this actor (references)
    // - contactPoints: free-form contact references for the actor
    private CodeableConcept category;
    private boolean internal;
    private List<ElementReference> roles;
    private List<ContactPoint> contactPoints;
    private List<Address> addresses;

    public BusinessActor(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_ACTOR);
        this.roles = new ArrayList<>();
        this.contactPoints = new ArrayList<>();
        this.addresses = new ArrayList<>();
        this.category = new CodeableConcept();
    }

    public BusinessActor(BusinessActor ori){
        super(ori);
        this.category = ori.getCategory();
        this.internal = ori.isInternal();
        this.roles = ori.getRoles();
        this.addresses = ori.getAddresses();
        this.contactPoints = ori.getContactPoints();
    }

    // Getters/Setters

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    public void addAddress(Address address) { this.addresses.add(address); }
    public void removeAddress(Address address) { this.addresses.remove(address); }
    public void clearAddresses() { this.addresses.clear(); }
    public boolean hasAddresses() { return !this.addresses.isEmpty(); }

    public CodeableConcept getCategory() { return category; }
    public void setCategory(CodeableConcept category) { this.category = category; }

    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }

    public List<ElementReference> getRoles() { return roles; }
    public void setRoles(List<ElementReference> roles) { this.roles = roles; }

    public List<ContactPoint> getContactPoints() { return contactPoints; }
    public void setContactPoints(List<ContactPoint> contactPoints) { this.contactPoints = contactPoints; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessActor that = (BusinessActor) o;
        return internal == that.internal &&
                Objects.equals(category, that.category) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(contactPoints, that.contactPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), category, internal, roles, contactPoints);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("category", category)
                .append("internal", internal)
                .append("roles", roles)
                .append("contactPoints", contactPoints)
                .appendSuper(super.toString())
                .toString();
    }
}
