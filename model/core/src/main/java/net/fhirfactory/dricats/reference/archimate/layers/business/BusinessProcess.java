/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ArchiMate Business Process element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - performers: references to roles/actors/collaborations that perform this process
 * - usedServices: references to BusinessService(s) used during the process
 * - inputBusinessObjects: BusinessObject(s) consumed by the process
 * - outputBusinessObjects: BusinessObject(s) produced by the process
 * - triggeringEvents: BusinessEvent(s) that start this process
 * - resultingEvents: BusinessEvent(s) that are emitted by this process
 */
public class BusinessProcess extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678920105L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessProcess.class);

    // Element-specific attributes (by references)
    private List<ElementReference> performers;
    private List<ElementReference> usedServices;
    private List<ElementReference> inputBusinessObjects;
    private List<ElementReference> outputBusinessObjects;
    private List<ElementReference> triggeringEvents;
    private List<ElementReference> resultingEvents;

    public BusinessProcess() {
        super();
        setElementType(ElementTypeEnum.BUSINESS_PROCESS);
        this.performers = new ArrayList<>();
        this.usedServices = new ArrayList<>();
        this.inputBusinessObjects = new ArrayList<>();
        this.outputBusinessObjects = new ArrayList<>();
        this.triggeringEvents = new ArrayList<>();
        this.resultingEvents = new ArrayList<>();
    }

    // Getters/Setters and helpers
    public List<ElementReference> getPerformers() {
        return performers;
    }

    public void setPerformers(List<ElementReference> performers) {
        this.performers = performers == null ? new ArrayList<>() : performers;
    }

    public void addPerformer(ElementReference performer) {
        if (performer == null) {
            return;
        }
        if (this.performers == null) {
            this.performers = new ArrayList<>();
        }
        this.performers.add(performer);
    }

    public void clearPerformers() {
        if (this.performers != null) {
            this.performers.clear();
        }
    }

    public List<ElementReference> getUsedServices() {
        return usedServices;
    }

    public void setUsedServices(List<ElementReference> usedServices) {
        this.usedServices = usedServices == null ? new ArrayList<>() : usedServices;
    }

    public void addUsedService(ElementReference service) {
        if (service == null) {
            return;
        }
        if (this.usedServices == null) {
            this.usedServices = new ArrayList<>();
        }
        this.usedServices.add(service);
    }

    public void clearUsedServices() {
        if (this.usedServices != null) {
            this.usedServices.clear();
        }
    }

    public List<ElementReference> getInputBusinessObjects() {
        return inputBusinessObjects;
    }

    public void setInputBusinessObjects(List<ElementReference> inputBusinessObjects) {
        this.inputBusinessObjects = inputBusinessObjects == null ? new ArrayList<>() : inputBusinessObjects;
    }

    public void addInputBusinessObject(ElementReference businessObject) {
        if (businessObject == null) {
            return;
        }
        if (this.inputBusinessObjects == null) {
            this.inputBusinessObjects = new ArrayList<>();
        }
        this.inputBusinessObjects.add(businessObject);
    }

    public void clearInputBusinessObjects() {
        if (this.inputBusinessObjects != null) {
            this.inputBusinessObjects.clear();
        }
    }

    public List<ElementReference> getOutputBusinessObjects() {
        return outputBusinessObjects;
    }

    public void setOutputBusinessObjects(List<ElementReference> outputBusinessObjects) {
        this.outputBusinessObjects = outputBusinessObjects == null ? new ArrayList<>() : outputBusinessObjects;
    }

    public void addOutputBusinessObject(ElementReference businessObject) {
        if (businessObject == null) {
            return;
        }
        if (this.outputBusinessObjects == null) {
            this.outputBusinessObjects = new ArrayList<>();
        }
        this.outputBusinessObjects.add(businessObject);
    }

    public void clearOutputBusinessObjects() {
        if (this.outputBusinessObjects != null) {
            this.outputBusinessObjects.clear();
        }
    }

    public List<ElementReference> getTriggeringEvents() {
        return triggeringEvents;
    }

    public void setTriggeringEvents(List<ElementReference> triggeringEvents) {
        this.triggeringEvents = triggeringEvents == null ? new ArrayList<>() : triggeringEvents;
    }

    public void addTriggeringEvent(ElementReference event) {
        if (event == null) {
            return;
        }
        if (this.triggeringEvents == null) {
            this.triggeringEvents = new ArrayList<>();
        }
        this.triggeringEvents.add(event);
    }

    public void clearTriggeringEvents() {
        if (this.triggeringEvents != null) {
            this.triggeringEvents.clear();
        }
    }

    public List<ElementReference> getResultingEvents() {
        return resultingEvents;
    }

    public void setResultingEvents(List<ElementReference> resultingEvents) {
        this.resultingEvents = resultingEvents == null ? new ArrayList<>() : resultingEvents;
    }

    public void addResultingEvent(ElementReference event) {
        if (event == null) {
            return;
        }
        if (this.resultingEvents == null) {
            this.resultingEvents = new ArrayList<>();
        }
        this.resultingEvents.add(event);
    }

    public void clearResultingEvents() {
        if (this.resultingEvents != null) {
            this.resultingEvents.clear();
        }
    }

    //
     // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BusinessProcess that = (BusinessProcess) o;
        return Objects.equals(performers, that.performers) &&
                Objects.equals(usedServices, that.usedServices) &&
                Objects.equals(inputBusinessObjects, that.inputBusinessObjects) &&
                Objects.equals(outputBusinessObjects, that.outputBusinessObjects) &&
                Objects.equals(triggeringEvents, that.triggeringEvents) &&
                Objects.equals(resultingEvents, that.resultingEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), performers, usedServices, inputBusinessObjects, outputBusinessObjects, triggeringEvents, resultingEvents);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("performers", performers)
                .append("usedServices", usedServices)
                .append("inputBusinessObjects", inputBusinessObjects)
                .append("outputBusinessObjects", outputBusinessObjects)
                .append("triggeringEvents", triggeringEvents)
                .append("resultingEvents", resultingEvents)
                .appendSuper(super.toString())
                .toString();
    }
}
