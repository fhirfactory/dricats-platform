/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.datatypes.CodeableConcept;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ArchiMate Business Object element.
 * Element-specific attributes added in addition to ElementBase:
 * - owner: reference to the owning business role/actor/collaboration
 * - state: lifecycle state of the business object (e.g., draft, active, obsolete)
 * - dataType: classification/type of the object (e.g., Policy, Report, Invoice)
 * - version: free-text version label
 * - representations: references to Representation elements depicting this object
 * - composite: whether this object is a composition of other business objects
 */
public class BusinessObject extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678920110L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessObject.class);

    // Element-specific attributes
    private DistributableObjectId owner;
    private CodeableConcept state;
    private CodeableConcept dataType;
    private String version;
    private List<DistributableObjectId> representations;
    private boolean composite;

    public BusinessObject(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_OBJECT);
        this.representations = new ArrayList<>();
        this.state = new CodeableConcept();
        this.dataType = new CodeableConcept();
    }

    public BusinessObject(BusinessObject ori){
        super(ori);
        this.owner = ori.getOwner();
        this.state = ori.getState();
        this.dataType = ori.getDataType();
        this.version = ori.getVersion();
        this.representations = ori.getRepresentations() != null ? new ArrayList<>(ori.getRepresentations()) : new ArrayList<>();
        this.composite = ori.isComposite();
    }

    // Getters/Setters
    public DistributableObjectId getOwner() { return owner; }
    public void setOwner(DistributableObjectId owner) { this.owner = owner; }

    public CodeableConcept getState() { return state; }
    public void setState(CodeableConcept state) { this.state = state; }

    public CodeableConcept getDataType() { return dataType; }
    public void setDataType(CodeableConcept dataType) { this.dataType = dataType; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<DistributableObjectId> getRepresentations() { return representations; }
    public void setRepresentations(List<DistributableObjectId> representations) { this.representations = representations; }
    public void addRepresentation(DistributableObjectId representation) { if(this.representations == null){ this.representations = new ArrayList<>(); } this.representations.add(representation); }
    public void clearRepresentations(){ if(this.representations != null){ this.representations.clear(); } }

    public boolean isComposite() { return composite; }
    public void setComposite(boolean composite) { this.composite = composite; }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessObject that = (BusinessObject) o;
        return composite == that.composite &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(state, that.state) &&
                Objects.equals(dataType, that.dataType) &&
                Objects.equals(version, that.version) &&
                Objects.equals(representations, that.representations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), owner, state, dataType, version, representations, composite);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("owner", owner)
                .append("state", state)
                .append("dataType", dataType)
                .append("version", version)
                .append("representations", representations)
                .append("composite", composite)
                .appendSuper(super.toString())
                .toString();
    }
}
