/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.internals.reference.relationships;

import net.fhirfactory.dricats.internals.reference.common.RelationshipBase;
import net.fhirfactory.dricats.internals.reference.relationships.valuesets.RelationshipType;

import java.io.Serial;

public class SpecializationRelationshipBase extends RelationshipBase {
    @Serial private static final long serialVersionUID = -730450129990209L;
    public SpecializationRelationshipBase(){ setType(RelationshipType.SPECIALIZATION); }
}
