/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ArchiMate Business Collaboration element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - participants: references to participating roles/actors (DistributableObjectId)
 */
public class BusinessCollaboration extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678920103L;

    // Element-specific attributes
    private List<ElementReference> participants;

    public BusinessCollaboration(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_COLLABORATION);
        this.participants = new ArrayList<>();
    }

    public BusinessCollaboration(BusinessCollaboration ori){
        super(ori);
        this.participants = new ArrayList<>();
        if(ori != null && ori.getParticipants() != null){
            this.participants.addAll(ori.getParticipants());
        }
    }

    public List<ElementReference> getParticipants() { return participants; }
    public void setParticipants(List<ElementReference> participants) { this.participants = participants; }
    public void addParticipant(ElementReference participant){ if(this.participants == null){ this.participants = new ArrayList<>(); } this.participants.add(participant); }
    public void clearParticipants(){ if(this.participants != null){ this.participants.clear(); } }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessCollaboration that = (BusinessCollaboration) o;
        return Objects.equals(participants, that.participants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), participants);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("participants", participants)
                .appendSuper(super.toString())
                .toString();
    }
}
