/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.common.object.ManagedObject;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
public abstract class RelationshipBase extends ManagedObject implements Serializable {
    @Serial
    private static final long serialVersionUID = -730450129990101L;

    private String documentation;
    private String specialization;
    private Map<String, String> properties;

    private RelationshipTypeEnum type;
    private ElementReference source;
    private ElementReference target;

    //
    // Constructor(s)
    //
    /**
     * Default constructor.
     * Initializes the properties map.
     */
    public RelationshipBase() {
        super();
        this.properties = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param ori The original RelationshipBase to copy from.
     */
    public RelationshipBase(RelationshipBase ori) {
        this(ori.getShortName(), ori.getIdentifier().getIdentifierValue(), ori.getDocumentation(), ori.getSpecialization(), ori.getProperties(), ori.getType(), ori.getSource(), ori.getTarget());
    }

    /**
     * Constructs a new RelationshipBase with the specified attributes.
     * Automatically builds the distinguished name from the relationship type and short name.
     *
     * @param shortName The short name for this relationship.
     * @param documentation Documentation or description for this relationship.
     * @param source The source object reference.
     * @param target The target object reference.
     * @param type The type of ArchiMate relationship.
     */
    public RelationshipBase(String shortName, String documentation, ElementReference source, ElementReference target, RelationshipTypeEnum type) {
        this(shortName, null, documentation, null, null, type, source, target);
        DistinguishedName distinguishedName = new DistinguishedName();
        RelativeDistinguishedName relativeDistinguishedName = new RelativeDistinguishedName(type.toString(), shortName);
        distinguishedName.appendUnqualifiedName(relativeDistinguishedName);
        ElementIdentifier elementIdentifier = new ElementIdentifier(distinguishedName);
        setIdentifier(elementIdentifier);
    }

    /**
     * Constructs a new RelationshipBase with detailed attributes including specialization and properties.
     * Automatically builds the distinguished name from the relationship type and short name.
     *
     * @param shortName The short name for this relationship.
     * @param documentation Documentation or description for this relationship.
     * @param specialization The specialization or stereotype for this relationship.
     * @param properties A map of key/value properties for this relationship.
     * @param type The type of ArchiMate relationship.
     * @param source The source object reference.
     * @param target The target object reference.
     */
    public RelationshipBase(String shortName, String documentation, String specialization, Map<String, String> properties, RelationshipTypeEnum type, ElementReference source, ElementReference target) {
        this(shortName, null, documentation, specialization, properties, type, source, target);
        DistinguishedName distinguishedName = new DistinguishedName();
        RelativeDistinguishedName relativeDistinguishedName = new RelativeDistinguishedName(type.toString(), shortName);
        distinguishedName.appendUnqualifiedName(relativeDistinguishedName);
        ElementIdentifier elementIdentifier = new ElementIdentifier(distinguishedName);
        setIdentifier(elementIdentifier);
    }

    /**
     * Constructs a new RelationshipBase with both short and long names, specialization, and properties.
     *
     * @param shortName The short name for this relationship.
     * @param longName The distinguished name for this relationship.
     * @param documentation Documentation or description for this relationship.
     * @param specialization The specialization or stereotype for this relationship.
     * @param properties A map of key/value properties for this relationship.
     * @param type The type of ArchiMate relationship.
     * @param source The source object reference.
     * @param target The target object reference.
     */
    public RelationshipBase(String shortName, DistinguishedName longName, String documentation, String specialization, Map<String, String> properties, RelationshipTypeEnum type, ElementReference source, ElementReference target) {
        super();
        this.properties = new HashMap<>();
        setShortName(shortName);
        ElementIdentifier elementIdentifier = new ElementIdentifier(longName);
        setIdentifier(elementIdentifier);
        if(StringUtils.isEmpty(documentation)){
            String sourceName = source.getElementIdentifier().getIdentifierValue().getUnqualifiedName().getValue();
            String targetName = target.getElementIdentifier().getIdentifierValue().getUnqualifiedName().getValue();
            String relationshipType = type.toString();
            documentation = relationshipType + ": " + sourceName + "->" + targetName;
        }
        setDocumentation(documentation);
        setSpecialization(specialization);
        setProperties(properties);
        setType(type);
        setSource(source);
        setTarget(target);
    }

    /**
     * Constructs a new RelationshipBase from source and target references and the relationship type.
     * Automatically generates short and long names based on the source and target names.
     *
     * @param source The source object reference. Cannot be null.
     * @param target The target object reference. Cannot be null.
     * @param relationshipType The type of ArchiMate relationship.
     * @throws IllegalArgumentException if source or target is null.
     */
    public RelationshipBase(ElementReference source, ElementReference target, RelationshipTypeEnum relationshipType) {
        this();
        if(source == null || target == null){
            throw new IllegalArgumentException("Source and target relationship objects cannot be null");
        }
        CommonName sourceCommonName = source.getElementIdentifier().getIdentifierValue().getCommonName();
        CommonName targetCommonName = target.getElementIdentifier().getIdentifierValue().getCommonName();
        String longNameStr = sourceCommonName + "->" + targetCommonName;
        DistinguishedName distinguishedName = new DistinguishedName();
        RelativeDistinguishedName relativeDistinguishedName = new RelativeDistinguishedName(relationshipType.toString(),longNameStr);
        distinguishedName.appendUnqualifiedName(relativeDistinguishedName);

        String sourceName = source.getElementIdentifier().getIdentifierValue().getUnqualifiedName().getValue();
        String targetName = target.getElementIdentifier().getIdentifierValue().getUnqualifiedName().getValue();
        String shortName = sourceName + "->" + targetName;

        setSource(source);
        setTarget(target);
        ElementIdentifier elementIdentifier = new ElementIdentifier(distinguishedName);
        setIdentifier(elementIdentifier);
        setShortName(shortName);
        setType(relationshipType);
        setDocumentation(relationshipType.toString() + ": " + shortName);
    }

    /**
     * Constructs a new RelationshipBase with specified names and object references.
     *
     * @param shortName The short name for this relationship.
     * @param longName The distinguished name for this relationship.
     * @param source The source object reference. Cannot be null.
     * @param target The target object reference. Cannot be null.
     * @param relationshipType The type of ArchiMate relationship.
     * @throws IllegalArgumentException if source or target is null.
     */
    public RelationshipBase(String shortName, DistinguishedName longName, ElementReference source, ElementReference target, RelationshipTypeEnum relationshipType) {
        this(shortName, longName, relationshipType.toString() + ": " + shortName, null, null, relationshipType, source, target);
    }

    /**
     * Constructs a new RelationshipBase with specified names, documentation, and object references.
     *
     * @param shortName The short name for this relationship.
     * @param longName The distinguished name for this relationship.
     * @param documentation Documentation or description for this relationship.
     * @param source The source object reference.
     * @param target The target object reference.
     * @param relationshipType The type of ArchiMate relationship.
     */
    public RelationshipBase(String shortName, DistinguishedName longName, String documentation, ElementReference source, ElementReference target, RelationshipTypeEnum relationshipType) {
        this(shortName, longName, documentation, null, null, relationshipType, source, target);
    }

    //
    // Bean Methods
    //

    public RelationshipTypeEnum getType() { return type; }
    public void setType(RelationshipTypeEnum type) { this.type = type; }

    public ElementReference getSource() { return source; }
    public void setSource(ElementReference source) { this.source = source; }

    public ElementReference getTarget() { return target; }
    public void setTarget(ElementReference target) { this.target = target; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Map<String, String> getProperties() { return properties; }
    public void setProperties(Map<String, String> properties) {
        if(this.properties == null){
            this.properties = new HashMap<>();
        } else {
            this.properties.clear();
        }
        if(properties != null && !properties.isEmpty()) {
            this.properties.putAll(properties);
        }
    }

    @JsonIgnore
    public ElementReference getReference(){
        ElementReference reference = new ElementReference();
        String specialization = getSpecialization();
        if(specialization == null || specialization.isEmpty()){
            specialization = "unknown";
        }
        reference.setElementSpecialisation(specialization);
        String elementType = getType().toString();
        if(elementType == null || elementType.isEmpty()){
            elementType = "unknown";
        }
        reference.setElementType(elementType);
        StringBuilder sb = new StringBuilder();
        sb.append(elementType);
        sb.append("(").append(specialization).append(")");
        sb.append(":").append(getIdentifier().getIdentifierValue().getCommonName().getName());
        reference.setReferenceDescription(sb.toString());
        ElementIdentifier elementIdentifier = SerializationUtils.clone(getIdentifier());
        reference.setElementIdentifier(elementIdentifier);
        reference.setElementInstanceId(getElementInstanceId());
        return reference;
    }

    //
     // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationshipBase that = (RelationshipBase) o;
        return type == that.type &&
                Objects.equals(getShortName(), that.getShortName()) &&
                Objects.equals(getIdentifier(), that.getIdentifier()) &&
                Objects.equals(documentation, that.documentation) &&
                Objects.equals(specialization, that.specialization) &&
                Objects.equals(properties, that.properties) &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Objects.equals(getElementInstanceId(), that.getElementInstanceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, getShortName(), getIdentifier(), documentation, specialization, properties, source, target, getElementInstanceId());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getProperties())
                .append("type", getType())
                .append("source", getSource())
                .append("target", getTarget())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .toString();
    }
}
