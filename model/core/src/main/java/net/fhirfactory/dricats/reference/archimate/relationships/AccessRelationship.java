/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.relationships;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.RelationshipBase;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipTypeEnum;

import java.io.Serial;

/**
 * Represents an Access relationship in ArchiMate.
 * An Access relationship represents the ability of a behavior element to observe or modify
 * information. It models the access that a process, function, or interaction has to a data object.
 */
public class AccessRelationship extends RelationshipBase {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -730450129990204L;

    //
    // Attributes
    //

    private AccessType accessType;

    //
    // Inner Classes
    //

    /**
     * Enumeration of access types.
     */
    public enum AccessType {
        READ,
        WRITE,
        READ_WRITE
    }

    //
    // Constructors
    //

    /**
     * Default constructor.
     */
    public AccessRelationship(){
        super();
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Constructor with source and target IDs.
     *
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(ElementReference sourceId, ElementReference targetId) {
        super(sourceId, targetId, RelationshipTypeEnum.ACCESS);
        setType(RelationshipTypeEnum.ACCESS);
        this.accessType = AccessType.READ;
    }

    /**
     * Constructor with source, target IDs, and access type.
     *
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     * @param accessType The type of access (READ, WRITE, READ_WRITE)
     */
    public AccessRelationship(ElementReference sourceId, ElementReference targetId, AccessType accessType) {
        super(sourceId, targetId, RelationshipTypeEnum.ACCESS);
        this.accessType = accessType;
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Constructor with name, source and target IDs.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(String name, ElementReference sourceId, ElementReference targetId) {
        super(name, "", sourceId, targetId, RelationshipTypeEnum.ACCESS);
        this.accessType = AccessType.READ;
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Constructor with name, source, target IDs, and access type.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     * @param accessType The type of access (READ, WRITE, READ_WRITE)
     */
    public AccessRelationship(String name, ElementReference sourceId, ElementReference targetId, AccessType accessType) {
        super(name, "", sourceId, targetId, RelationshipTypeEnum.ACCESS);
        this.accessType = accessType;
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Constructor with name, description, source and target IDs.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(String name, String description, ElementReference sourceId, ElementReference targetId) {
        super(name, description, sourceId, targetId, RelationshipTypeEnum.ACCESS);
        this.accessType = AccessType.READ;
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Constructor with name, description, source, target IDs, and access type.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     * @param accessType The type of access (READ, WRITE, READ_WRITE)
     */
    public AccessRelationship(String name, String description, ElementReference sourceId, ElementReference targetId, AccessType accessType) {
        super(name, description, sourceId, targetId, RelationshipTypeEnum.ACCESS);
        this.accessType = accessType;
        setType(RelationshipTypeEnum.ACCESS);
    }

    /**
     * Get the access type.
     *
     * @return The access type
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * Set the access type.
     *
     * @param accessType The access type
     */
    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    @Override
    public String toString() {
        return super.toString() + ", accessType=" + accessType + "]";
    }
}
