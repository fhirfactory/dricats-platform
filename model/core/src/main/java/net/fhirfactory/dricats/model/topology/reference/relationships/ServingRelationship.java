/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.relationships;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.topology.reference.common.RelationshipBase;
import net.fhirfactory.dricats.model.topology.reference.relationships.valuesets.RelationshipType;

import java.io.Serial;

/**
 * Represents a Serving relationship in ArchiMate.
 * A Serving relationship represents that an element provides its functionality to another element.
 * It models that one element provides a service or function that is consumed by another element.
 */
public class ServingRelationship extends RelationshipBase {
    @Serial private static final long serialVersionUID = -730450129990203L;


    /**
     * Default constructor.
     */
    public ServingRelationship() {
        super();
        setType(RelationshipType.SERVING);
    }

    /**
     * Constructor with source and target IDs.
     *
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(sourceId, targetId);
        setType(RelationshipType.SERVING);
    }

    /**
     * Constructor with name, source and target IDs.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(String name, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, sourceId, targetId);
        setType(RelationshipType.SERVING);
    }

    /**
     * Constructor with name, description, source and target IDs.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(String name, String description, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, description, sourceId, targetId);
        setType(RelationshipType.SERVING);
    }
}
