/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.common;

import net.fhirfactory.dricats.model.common.DistributableObject;
import net.fhirfactory.dricats.model.topology.reference.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Common reference for ArchiMate Elements.
 * Provides common ArchiMate attributes across elements per ArchiMate standard:
 * - name (mandatory)
 * - documentation (aka description)
 * - specialization (stereotype)
 * - properties (key/value)
 */
public abstract class ElementBase extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -730450129990001L;
    private static final Logger LOG = LoggerFactory.getLogger(ElementBase.class);

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
    public ElementBase(){
        super();
        this.properties = new HashMap<>();
        getLogger().trace("ElementBase(): constructed");
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
        ElementBase that = (ElementBase) o;
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
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }
}
