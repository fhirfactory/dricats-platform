/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.relationships;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.RelationshipBase;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipTypeEnum;

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
        setType(RelationshipTypeEnum.SERVING);
    }

    /**
     * Constructor with source and target IDs.
     *
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(ElementReference sourceId, ElementReference targetId) {
        super(sourceId, targetId, RelationshipTypeEnum.SERVING);
        setType(RelationshipTypeEnum.SERVING);
    }

    /**
     * Constructor with name, source and target IDs.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(String name, ElementReference sourceId, ElementReference targetId) {
        super(name, "", sourceId, targetId, RelationshipTypeEnum.SERVING);
        setType(RelationshipTypeEnum.SERVING);
    }

    /**
     * Constructor with name, description, source and target IDs.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the serving element)
     * @param targetId The ID of the target element (the element being served)
     */
    public ServingRelationship(String name, String description, ElementReference sourceId, ElementReference targetId) {
        super(name, description, sourceId, targetId, RelationshipTypeEnum.SERVING);
        setType(RelationshipTypeEnum.SERVING);
    }
}
