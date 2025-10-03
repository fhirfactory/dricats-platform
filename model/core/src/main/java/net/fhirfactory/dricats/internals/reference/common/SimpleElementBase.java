/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.internals.reference.common;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.SimpleDistributableObject;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Common reference for ArchiMate Elements other than ApplicationComponent, DataObject and selected others.
 * Provides common ArchiMate attributes across elements per ArchiMate standard:
 * - name (mandatory)
 * - documentation (aka description)
 * - specialization (stereotype)
 * - properties (key/value)
 */
public abstract class SimpleElementBase extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -730450129220001L;
    private static final Logger LOG = LoggerFactory.getLogger(SimpleElementBase.class);

    //
    // Attributes
    //
    private String name;
    private String documentation;
    private String specialization;
    private Map<String, String> properties;
    private ElementTypeEnum elementType;

    //
    // Constructor(s)
    //
    public SimpleElementBase(){
        super();
        this.properties = new HashMap<>();
        getLogger().trace("ElementBase(): constructed");
    }

    public SimpleElementBase(String name, String documentation, String specialization, Map<String, String> properties, ElementTypeEnum elementType) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.specialization = specialization;
        this.properties = new HashMap<>(properties);
        this.properties.putAll(properties);
        this.elementType = elementType;
        getLogger().trace("ElementBase(name, documentation, specialization, properties, elementType): constructed");
    }

    public SimpleElementBase(SimpleElementBase ori) {
        super(ori);
        this.name = ori.name;
        this.documentation = ori.documentation;
        this.specialization = ori.specialization;
        this.properties = new HashMap<>();
        this.properties.putAll(ori.properties);
        this.elementType = ori.elementType;
        getLogger().trace("ElementBase(ori): constructed");
    }

    public SimpleElementBase(String name, String documentation, String specialization, ElementTypeEnum elementType) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.specialization = specialization;
        this.properties = new HashMap<>();
        this.elementType = elementType;
        getLogger().trace("ElementBase(name, documentation, specialization, elementType): constructed");
    }

    //
    // Bean Methods
    //
    protected Logger getLogger(){
        return LOG;
    }

    public ElementTypeEnum getElementType() {
        return elementType;
    }

    public void setElementType(ElementTypeEnum elementType) {
        this.elementType = elementType;
    }

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
        SimpleElementBase that = (SimpleElementBase) o;
        return Objects.equals(getObjectID(), that.getObjectID()) &&
                Objects.equals(name, that.name) &&
                elementType == that.elementType &&
                Objects.equals(documentation, that.documentation) &&
                Objects.equals(specialization, that.specialization) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectID(), elementType, name, documentation, specialization, properties);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+name+'\''+
                ", elementType="+elementType+
                ", documentation='"+documentation+'\''+
                ", specialization='"+specialization+'\''+
                ", properties="+properties+
                ", objectID="+getObjectID()+
                ", metadata="+getMetadata()+
                '}';
    }

    //
    // Resolve a Key
    //

    public String resolveKey() {
        String key = null;
        try {
            DistributableObjectId objectId = this.getObjectID();
            if (objectId != null && objectId.getQualifiedName() != null && objectId.getQualifiedName().getCommonName().getValue() != null && !objectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
                key = objectId.getQualifiedName().getCommonName().getValue();
            }
        } catch (Exception e) {
            // ignore
        }
        return key;
    }
}
