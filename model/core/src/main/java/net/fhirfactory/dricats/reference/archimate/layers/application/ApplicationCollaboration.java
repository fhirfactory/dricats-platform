/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationCollaboration extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678910102L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationCollaboration.class);

    // ArchiMate attributes specific to Application Collaboration:
    // - participants: references to participating ApplicationComponent(s) or nested collaborations
    private List<DistributableObjectId> participants;

    public ApplicationCollaboration(){
        super();
        setElementType(ElementTypeEnum.APPLICATION_COLLABORATION);
        this.participants = new ArrayList<>();
    }

    public ApplicationCollaboration(ApplicationCollaboration ori){
        super(ori);
        setElementType(ElementTypeEnum.APPLICATION_COLLABORATION);
        this.participants = new ArrayList<>();
        if(ori != null && ori.getParticipants() != null){
            this.participants.addAll(ori.getParticipants());
        }
    }

    protected Logger getLogger(){ return LOG; }

    public List<DistributableObjectId> getParticipants() { return participants; }
    public void setParticipants(List<DistributableObjectId> participants) { this.participants = participants; }
    public void addParticipant(DistributableObjectId participant){ if(this.participants == null){ this.participants = new ArrayList<>(); } this.participants.add(participant); }
    public void clearParticipants(){ if(this.participants != null){ this.participants.clear(); } }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationCollaboration that = (ApplicationCollaboration) o;
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
