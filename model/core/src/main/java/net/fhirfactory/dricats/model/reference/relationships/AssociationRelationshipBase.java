/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.reference.relationships;

import net.fhirfactory.dricats.model.reference.common.RelationshipBase;
import net.fhirfactory.dricats.model.reference.relationships.valuesets.RelationshipType;

import java.io.Serial;

public class AssociationRelationshipBase extends RelationshipBase {
    @Serial private static final long serialVersionUID = -730450129990208L;
    public AssociationRelationshipBase(){ setType(RelationshipType.ASSOCIATION); }
}
