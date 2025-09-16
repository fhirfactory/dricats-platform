/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.reference.common;

import net.fhirfactory.dricats.model.common.DistributableObject;
import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.reference.relationships.valuesets.RelationshipType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Common reference for ArchiMate Relationships.
 * Provides common attributes:
 * - name (optional)
 * - documentation
 * - specialization (stereotype)
 * - properties (key/value)
 * - type (RelationshipType)
 * - sourceRef and targetRef (by DistributableObjectId)
 */
public abstract class RelationshipBase extends DistributableObject implements Serializable {
    @Serial private static final long serialVersionUID = -730450129990101L;
    private static final Logger LOG = LoggerFactory.getLogger(RelationshipBase.class);

    private String name;
    private String documentation;
    private String specialization;
    private Map<String, String> properties;

    private RelationshipType type;
    private DistributableObjectId source;
    private DistributableObjectId target;

    //
    // Constructor(s)
    //
    public RelationshipBase() {
        super();
        this.properties = new HashMap<>();
        getLogger().trace("RelationshipBase(): constructed");
    }

    public RelationshipBase(RelationshipBase ori) {
        super(ori);
        this.name = ori.name;
        this.documentation = ori.documentation;
        this.specialization = ori.specialization;
        this.properties = new HashMap<>(ori.properties);
        this.type = ori.type;
        this.source = ori.source;
        this.target = ori.target;
        getLogger().trace("RelationshipBase(ori): constructed");
    }

    public RelationshipBase(String name, String documentation, String specialization, Map<String, String> properties, RelationshipType type, DistributableObjectId source, DistributableObjectId target) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.specialization = specialization;
        this.properties = properties;
        this.type = type;
        this.source = source;
        this.target = target;
        getLogger().trace("RelationshipBase(source, target): constructed");
    }

    public RelationshipBase(DistributableObjectId source, DistributableObjectId target) {
        super();
        this.source = source;
        this.target = target;
        this.properties = new HashMap<>();
        getLogger().trace("RelationshipBase(source, target): constructed");
    }

    public RelationshipBase(String name, DistributableObjectId source, DistributableObjectId target) {
        super();
        this.name = name;
        this.source = source;
        this.target = target;
        this.properties = new HashMap<>();
        getLogger().trace("RelationshipBase(source, target): constructed");
    }

    public RelationshipBase(String name, String documentation, DistributableObjectId source, DistributableObjectId target) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.source = source;
        this.target = target;
        this.properties = new HashMap<>();
        getLogger().trace("RelationshipBase(source, target): constructed");
    }

    //
    // Bean Methods
    //

    protected Logger getLogger(){
        return LOG;
    }

    public RelationshipType getType() { return type; }
    public void setType(RelationshipType type) { this.type = type; }

    public DistributableObjectId getSource() { return source; }
    public void setSource(DistributableObjectId source) { this.source = source; }

    public DistributableObjectId getTarget() { return target; }
    public void setTarget(DistributableObjectId target) { this.target = target; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Map<String, String> getProperties() { return properties; }
    public void setProperties(Map<String, String> properties) { this.properties = properties; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipBase that = (RelationshipBase) o;
        return type == that.type &&
                Objects.equals(name, that.name) &&
                Objects.equals(documentation, that.documentation) &&
                Objects.equals(specialization, that.specialization) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Objects.equals(getObjectID(), that.getObjectID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, documentation, specialization, properties, source, target, getObjectID());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "type="+type+
                ", name='"+name+'\''+
                ", documentation='"+documentation+'\''+
                ", specialization='"+specialization+'\''+
                ", properties="+properties+
                ", sourceRef='"+ source +'\''+
                ", targetRef='"+ target +'\''+
                ", objectID="+getObjectID()+
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }
}
