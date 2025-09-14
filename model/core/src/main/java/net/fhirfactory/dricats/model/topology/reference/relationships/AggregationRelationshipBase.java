/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.relationships;

import net.fhirfactory.dricats.model.topology.reference.common.RelationshipBase;
import net.fhirfactory.dricats.model.topology.reference.relationships.valuesets.RelationshipType;

import java.io.Serial;

public class AggregationRelationshipBase extends RelationshipBase {
    @Serial private static final long serialVersionUID = -730450129990210L;
    public AggregationRelationshipBase(){ setType(RelationshipType.AGGREGATION); }
}
