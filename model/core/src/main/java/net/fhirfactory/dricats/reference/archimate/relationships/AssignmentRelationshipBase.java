/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.relationships;

import net.fhirfactory.dricats.reference.archimate.common.RelationshipBase;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipType;

import java.io.Serial;

public class AssignmentRelationshipBase extends RelationshipBase {
    @Serial private static final long serialVersionUID = -730450129990201L;
    public AssignmentRelationshipBase(){ setType(RelationshipType.ASSIGNMENT); }
}
