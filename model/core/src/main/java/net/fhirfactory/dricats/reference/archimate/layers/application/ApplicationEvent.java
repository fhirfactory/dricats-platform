/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class ApplicationEvent extends SimpleElementBase {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910108L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationEvent.class);

    //
     // Attributes
    //

    private List<ApplicationFunction> triggeringFunctions;
    private List<ApplicationFunction> triggeredFunctions;
    private List<ApplicationDataObject> associatedData;
    private DistributableObjectId source;

    //
     // Constructor(s)
    //

    public ApplicationEvent(){
        super();
        this.triggeringFunctions = new ArrayList<>();
        this.triggeredFunctions = new ArrayList<>();
        this.associatedData = new ArrayList<>();
        this.source = null;
    }

    public ApplicationEvent(ApplicationEvent ori){
        super(ori);
        this.triggeringFunctions = new ArrayList<>();
        this.triggeringFunctions.addAll(ori.getTriggeringFunctions());
        this.triggeredFunctions = new ArrayList<>();
        this.triggeredFunctions.addAll(ori.getTriggeredFunctions());
        this.associatedData = new ArrayList<>();
        this.associatedData.addAll(ori.getAssociatedData());
        this.source = SerializationUtils.clone(ori.getSource());
    }

    //
    // Accessor(s) / Mutator(s)
    //

    protected Logger getLogger(){ return LOG; }

    public List<ApplicationFunction> getTriggeringFunctions() {
        return triggeringFunctions;
    }

    public void setTriggeringFunctions(List<ApplicationFunction> triggeringFunctions) {
        this.triggeringFunctions = triggeringFunctions;
    }

    public List<ApplicationFunction> getTriggeredFunctions() {
        return triggeredFunctions;
    }

    public void setTriggeredFunctions(List<ApplicationFunction> triggeredFunctions) {
        this.triggeredFunctions = triggeredFunctions;
    }

    public List<ApplicationDataObject> getAssociatedData() {
        return associatedData;
    }

    public void setAssociatedData(List<ApplicationDataObject> associatedData) {
        this.associatedData = associatedData;
    }

    public DistributableObjectId getSource() {
        return source;
    }

    public void setSource(DistributableObjectId source) {
        this.source = source;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("triggeringFunctions", triggeringFunctions)
                .append("triggeredFunctions", triggeredFunctions)
                .append("associatedData", associatedData)
                .append("source", source)
                .appendSuper(super.toString())
                .toString();
    }
}
