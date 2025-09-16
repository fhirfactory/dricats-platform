/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.reference.relationships;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.reference.common.RelationshipBase;
import net.fhirfactory.dricats.model.reference.relationships.valuesets.RelationshipType;

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
        setType(RelationshipType.ACCESS);
    }

    /**
     * Constructor with source and target IDs.
     *
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(sourceId, targetId);
        setType(RelationshipType.ACCESS);
        this.accessType = AccessType.READ;
    }

    /**
     * Constructor with source, target IDs, and access type.
     *
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     * @param accessType The type of access (READ, WRITE, READ_WRITE)
     */
    public AccessRelationship(DistributableObjectId sourceId, DistributableObjectId targetId, AccessType accessType) {
        super(sourceId, targetId);
        this.accessType = accessType;
        setType(RelationshipType.ACCESS);
    }

    /**
     * Constructor with name, source and target IDs.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(String name, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, sourceId, targetId);
        this.accessType = AccessType.READ;
        setType(RelationshipType.ACCESS);
    }

    /**
     * Constructor with name, source, target IDs, and access type.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     * @param accessType The type of access (READ, WRITE, READ_WRITE)
     */
    public AccessRelationship(String name, DistributableObjectId sourceId, DistributableObjectId targetId, AccessType accessType) {
        super(name, sourceId, targetId);
        this.accessType = accessType;
        setType(RelationshipType.ACCESS);
    }

    /**
     * Constructor with name, description, source and target IDs.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the behavior element)
     * @param targetId The ID of the target element (the data object)
     */
    public AccessRelationship(String name, String description, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, description, sourceId, targetId);
        this.accessType = AccessType.READ;
        setType(RelationshipType.ACCESS);
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
    public AccessRelationship(String name, String description, DistributableObjectId sourceId, DistributableObjectId targetId, AccessType accessType) {
        super(name, description, sourceId, targetId);
        this.accessType = accessType;
        setType(RelationshipType.ACCESS);
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
