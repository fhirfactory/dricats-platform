/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

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

/**
 * ArchiMate Business Event element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - triggeringBehaviors: references to BusinessProcess/Function/Interaction that raise this event
 * - triggeredBehaviors: references to BusinessProcess/Function/Interaction that are started by this event
 * - associatedBusinessObjects: references to BusinessObject(s) relevant to this event
 * - source: optional reference to the origin of the event (actor/role/object/etc.)
 * - interrupting: whether the event interrupts an ongoing behavior
 */
public class BusinessEvent extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920108L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessEvent.class);

    // Element-specific attributes (by reference IDs)
    private List<DistributableObjectId> triggeringBehaviors;
    private List<DistributableObjectId> triggeredBehaviors;
    private List<DistributableObjectId> associatedBusinessObjects;
    private DistributableObjectId source;
    private boolean interrupting;

    public BusinessEvent(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_EVENT);
        this.triggeringBehaviors = new ArrayList<>();
        this.triggeredBehaviors = new ArrayList<>();
        this.associatedBusinessObjects = new ArrayList<>();
        this.source = null;
        this.interrupting = false;
    }

    public BusinessEvent(BusinessEvent ori){
        super(ori);
        this.triggeringBehaviors = new ArrayList<>();
        if(ori != null && ori.getTriggeringBehaviors() != null){ this.triggeringBehaviors.addAll(ori.getTriggeringBehaviors()); }
        this.triggeredBehaviors = new ArrayList<>();
        if(ori != null && ori.getTriggeredBehaviors() != null){ this.triggeredBehaviors.addAll(ori.getTriggeredBehaviors()); }
        this.associatedBusinessObjects = new ArrayList<>();
        if(ori != null && ori.getAssociatedBusinessObjects() != null){ this.associatedBusinessObjects.addAll(ori.getAssociatedBusinessObjects()); }
        this.source = ori == null ? null : ori.getSource();
        this.interrupting = ori != null && ori.isInterrupting();
    }

    public List<DistributableObjectId> getTriggeringBehaviors() { return triggeringBehaviors; }
    public void setTriggeringBehaviors(List<DistributableObjectId> triggeringBehaviors) { this.triggeringBehaviors = triggeringBehaviors == null ? new ArrayList<>() : triggeringBehaviors; }
    public void addTriggeringBehavior(DistributableObjectId behavior){ if(behavior == null){ return; } if(this.triggeringBehaviors == null){ this.triggeringBehaviors = new ArrayList<>(); } this.triggeringBehaviors.add(behavior); }
    public void clearTriggeringBehaviors(){ if(this.triggeringBehaviors != null){ this.triggeringBehaviors.clear(); } }

    public List<DistributableObjectId> getTriggeredBehaviors() { return triggeredBehaviors; }
    public void setTriggeredBehaviors(List<DistributableObjectId> triggeredBehaviors) { this.triggeredBehaviors = triggeredBehaviors == null ? new ArrayList<>() : triggeredBehaviors; }
    public void addTriggeredBehavior(DistributableObjectId behavior){ if(behavior == null){ return; } if(this.triggeredBehaviors == null){ this.triggeredBehaviors = new ArrayList<>(); } this.triggeredBehaviors.add(behavior); }
    public void clearTriggeredBehaviors(){ if(this.triggeredBehaviors != null){ this.triggeredBehaviors.clear(); } }

    public List<DistributableObjectId> getAssociatedBusinessObjects() { return associatedBusinessObjects; }
    public void setAssociatedBusinessObjects(List<DistributableObjectId> associatedBusinessObjects) { this.associatedBusinessObjects = associatedBusinessObjects == null ? new ArrayList<>() : associatedBusinessObjects; }
    public void addAssociatedBusinessObject(DistributableObjectId businessObject){ if(businessObject == null){ return; } if(this.associatedBusinessObjects == null){ this.associatedBusinessObjects = new ArrayList<>(); } this.associatedBusinessObjects.add(businessObject); }
    public void clearAssociatedBusinessObjects(){ if(this.associatedBusinessObjects != null){ this.associatedBusinessObjects.clear(); } }

    public DistributableObjectId getSource() { return source; }
    public void setSource(DistributableObjectId source) { this.source = source; }

    public boolean isInterrupting() { return interrupting; }
    public void setInterrupting(boolean interrupting) { this.interrupting = interrupting; }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessEvent that = (BusinessEvent) o;
        return interrupting == that.interrupting &&
                Objects.equals(triggeringBehaviors, that.triggeringBehaviors) &&
                Objects.equals(triggeredBehaviors, that.triggeredBehaviors) &&
                Objects.equals(associatedBusinessObjects, that.associatedBusinessObjects) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), triggeringBehaviors, triggeredBehaviors, associatedBusinessObjects, source, interrupting);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("triggeringBehaviors", triggeringBehaviors)
                .append("triggeredBehaviors", triggeredBehaviors)
                .append("associatedBusinessObjects", associatedBusinessObjects)
                .append("source", source)
                .append("interrupting", interrupting)
                .appendSuper(super.toString())
                .toString();
    }
}
