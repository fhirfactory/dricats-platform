/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.relationships;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.topology.reference.common.RelationshipBase;
import net.fhirfactory.dricats.model.topology.reference.relationships.valuesets.RelationshipType;

import java.io.Serial;

/**
 * Represents a Flow relationship in ArchiMate.
 * A Flow relationship represents transfer from one element to another.
 * It models the transfer of, for example, information, value, or material between elements.
 */
public class FlowRelationship extends RelationshipBase {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -730450129990207L;

    //
    // Attributes
    //
    private String flowType;

    //
    // Constructors
    //

    /**
     * Default constructor.
     */
    public FlowRelationship() {
        super();
        setType(RelationshipType.FLOW);
    }

    /**
     * Constructor with source and target IDs.
     *
     * @param sourceId The ID of the source element (the origin of the flow)
     * @param targetId The ID of the target element (the destination of the flow)
     */
    public FlowRelationship(DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(sourceId, targetId);
        setType(RelationshipType.FLOW);
    }

    /**
     * Constructor with name, source and target IDs.
     *
     * @param name The name of the relationship
     * @param sourceId The ID of the source element (the origin of the flow)
     * @param targetId The ID of the target element (the destination of the flow)
     */
    public FlowRelationship(String name, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, sourceId, targetId);
        setType(RelationshipType.FLOW);
    }

    /**
     * Constructor with name, description, source and target IDs.
     *
     * @param name The name of the relationship
     * @param description The description of the relationship
     * @param sourceId The ID of the source element (the origin of the flow)
     * @param targetId The ID of the target element (the destination of the flow)
     */
    public FlowRelationship(String name, String description, DistributableObjectId sourceId, DistributableObjectId targetId) {
        super(name, description, sourceId, targetId);
        setType(RelationshipType.FLOW);
    }

    /**
     * Get the type of flow.
     *
     * @return The type of flow (e.g., "information", "value", "material")
     */
    public String getFlowType() {
        return flowType;
    }

    /**
     * Set the type of flow.
     *
     * @param flowType The type of flow (e.g., "information", "value", "material")
     */
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 1) +
                ", flowType=" + flowType + "]";
    }
}
