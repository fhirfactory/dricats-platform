/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.internals.pubsub.common;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract public class FilterMaskBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    protected ApplicationComponentIdMask internalEventSource;
    protected ApplicationComponentIdMask internalEventTarget;
    protected List<TopicFilter> eventTopicFilters;
    protected Boolean allowAllTopics;
    protected String eventOrigin;
    protected String eventFinalDestination;
    protected EventTemporalWindow temporalWindow;

    //
    // Constructors
    //

    public FilterMaskBase(){
        super();
        setEventOrigin("*");
        setEventFinalDestination("*");
        setTemporalWindow(new EventTemporalWindow());
        eventTopicFilters = new ArrayList<>();
        allowAllTopics = true;
    }

    public FilterMaskBase(ApplicationComponentIdMask internalEventSource, ApplicationComponentIdMask internalEventTarget, List<TopicFilter> eventTopicFilters, String eventOrigin, String eventFinalDestination, EventTemporalWindow temporalWindow ){
        super();
        this.eventTopicFilters = new ArrayList<>();
        setInternalEventSource(internalEventSource);
        setInternalEventTarget(internalEventTarget);
        getEventTopicFilters().addAll(eventTopicFilters);
        setEventOrigin(eventOrigin);
        setEventFinalDestination(eventFinalDestination);
        setTemporalWindow(temporalWindow);
        if(eventTopicFilters.isEmpty()){
            allowAllTopics = true;
        }
        else{
            allowAllTopics = false;
        }
    }

    //
    // Getters and Setters
    //

    abstract protected Logger getLogger();

    public ApplicationComponentIdMask getInternalEventSource() {
        return internalEventSource;
    }

    public void setInternalEventSource(ApplicationComponentIdMask internalEventSource) {
        this.internalEventSource = internalEventSource;
    }

    public ApplicationComponentIdMask getInternalEventTarget() {
        return internalEventTarget;
    }

    public void setInternalEventTarget(ApplicationComponentIdMask internalEventTarget) {
        this.internalEventTarget = internalEventTarget;
    }

    public List<TopicFilter> getEventTopicFilters() {
        return this.eventTopicFilters;
    }

    public void setEventTopicFilters(List<TopicFilter> eventTopicFilters) {
        this.eventTopicFilters = new ArrayList<>();
        this.eventTopicFilters.addAll(eventTopicFilters);
        if(eventTopicFilters.isEmpty()){
            allowAllTopics = true;
        }
        else{
            allowAllTopics = false;
        }
    }

    public String getEventOrigin() {
        if(this.eventOrigin == null){
            return("*");
        }
        return eventOrigin;
    }

    public Boolean isAllowAllTopics(){
        return allowAllTopics;
    }
    public void setAllowAllTopics(Boolean allowAllTopics){
        this.allowAllTopics = allowAllTopics;
    }

    public void setEventOrigin(String eventOrigin) {
        this.eventOrigin = eventOrigin;
    }

    public String getEventFinalDestination() {
        if(this.eventFinalDestination == null){
            return("*");
        }
        return eventFinalDestination;
    }

    public void setEventFinalDestination(String eventFinalDestination) {
        this.eventFinalDestination = eventFinalDestination;
    }

    public EventTemporalWindow getTemporalWindow() {
        return temporalWindow;
    }

    public void setTemporalWindow(EventTemporalWindow temporalWindow) {
        this.temporalWindow = temporalWindow;
    }


    //
    // Business Methods
    //


    public Boolean filterEventOrigin(String testOrigin){
        getLogger().trace("filterEventOrigin(): Entry, testOrigin -> {}", testOrigin);
        if(getEventOrigin() == null){
            getLogger().trace("filterEventOrigin(): Exit, eventOrigin is null, returning true");
            return(true);
        }
        if(getEventOrigin().contentEquals("*")){
            getLogger().trace("filterEventOrigin(): Exit, eventOrigin is '*', returning true");
            return(true);
        }
        if(testOrigin == null){
            getLogger().trace("filterEventOrigin(): Exit, testOrigin is null, returning false");
            return(false);
        }
        Boolean originFilterOutcome = getEventOrigin().contentEquals(testOrigin);
        getLogger().trace("filterEventOrigin(): Exit, originFilterOutcome -> {}", originFilterOutcome);
        return(originFilterOutcome);
    }

    public Boolean filterEventFinalDestination(String testDestination){
        getLogger().trace("filterEventFinalDestination(): Entry, testDestination -> {}", testDestination);
        if(getEventFinalDestination() == null){
            getLogger().trace("filterEventFinalDestination(): Exit, eventFinalDestination is null, returning true");
            return(true);
        }
        if(getEventFinalDestination().contentEquals("*")){
            getLogger().trace("filterEventFinalDestination(): Exit, eventFinalDestination is '*', returning true");
            return(true);
        }
        if(testDestination == null){
            getLogger().trace("filterEventFinalDestination(): Exit, testDestination is null, returning false");
            return(false);
        }
        Boolean destinationFilterOutcome = getEventFinalDestination().contentEquals(testDestination);
        getLogger().trace("filterEventFinalDestination(): Exit, destinationFilterOutcome -> {}", destinationFilterOutcome);
        return(destinationFilterOutcome);
    }

    public Boolean filterEventTemporalWindow(EffectiveDate testDate){
        getLogger().trace("filterEventTemporalWindow(): Entry, testDate -> {}", testDate);
        if(testDate == null){
            getLogger().trace("filterEventTemporalWindow(): Exit, testDate is null, returning false");
            return(false);
        }
        if(getTemporalWindow() == null){
            getLogger().trace("filterEventTemporalWindow(): Exit, temporalWindow is null, returning true");
            return(true);
        }
        Boolean temporalWindowTestOutcome = getTemporalWindow().isWithinTemporalWindow(testDate);
        getLogger().trace("filterEventTemporalWindow(): Exit, temporalWindowTestOutcome -> {}", temporalWindowTestOutcome);
        return(temporalWindowTestOutcome);
    }

    public Boolean filterEventTemporalWindow(LocalDateTime testDateTime){
        getLogger().trace("filterEventTemporalWindow(): Entry, testDateTime -> {}", testDateTime);
        if(testDateTime == null){
            getLogger().trace("filterEventTemporalWindow(): Exit, testDateTime is null, returning false");
            return(false);
        }
        if(getTemporalWindow() == null){
            getLogger().trace("filterEventTemporalWindow(): Exit, temporalWindow is null, returning true");
            return(true);
        }
        Boolean temporalWindowTestOutcome = getTemporalWindow().isWithinTemporalWindow(testDateTime);
        getLogger().trace("filterEventTemporalWindow(): Exit, temporalWindowTestOutcome -> {}", temporalWindowTestOutcome);
        return(temporalWindowTestOutcome);
    }

    public Boolean filterInternalEventSource(ElementReference testComponentId){
        getLogger().trace("filterInternalEventSource(): Entry, testComponentId -> {}", testComponentId);
        if(testComponentId == null){
            getLogger().trace("filterInternalEventSource(): Exit, testComponentId is null, returning false");
            return(false);
        }
        if(getInternalEventSource() == null){
            getLogger().trace("filterInternalEventSource(): Exit, internalEventSource is null, returning false");
            return(false);
        }
        else{
            Boolean qualifiedNameFilterOutcome = getInternalEventSource().getComponentIdMask().filter(testComponentId.getLocalObjectId().getFullyDistinguishedName());
            getLogger().trace("filterInternalEventSource(): Exit, qualifiedNameFilterOutcome -> {}", qualifiedNameFilterOutcome);
            return(qualifiedNameFilterOutcome);
        }
    }

    public Boolean filterInternalEventTarget(ElementReference testComponentId){
        getLogger().trace("filterInternalEventTarget(): Entry, testComponentId -> {}", testComponentId);
        if(testComponentId == null){
            getLogger().trace("filterInternalEventTarget(): Exit, testComponentId is null, returning false");
            return(false);
        }
        if(getInternalEventTarget() == null){
            getLogger().trace("filterInternalEventTarget(): Exit, internalEventTarget is null, returning false");
            return(false);
        }
        else{
            Boolean qualifiedNameFilterOutcome = getInternalEventTarget().getComponentIdMask().filter(testComponentId.getLocalObjectId().getFullyDistinguishedName());
            getLogger().trace("filterInternalEventTarget(): Exit, qualifiedNameFilterOutcome -> {}", qualifiedNameFilterOutcome);
            return(qualifiedNameFilterOutcome);
        }
    }

    public Boolean filterEventTopics(Topic testTopic){
        getLogger().trace("filterEventTopics(): Entry, testTopic -> {}", testTopic);
        if(testTopic == null){
            getLogger().trace("filterEventTopics(): Exit, testTopic is null, returning false");
            return(false);
        }
        if(getEventTopicFilters() == null){
            getLogger().trace("filterEventTopics(): Exit, eventTopics is null, returning true");
            return(false);
        }
        boolean filterOutcome = false;
        for(TopicFilter currentTopicFilter : getEventTopicFilters()){
            if(currentTopicFilter.filter(testTopic.getTopicName())){
                filterOutcome = true;
                break;
            }
        }
        getLogger().trace("filterEventTopics(): Exit, filterOutcome -> {}", filterOutcome);
        return(filterOutcome);
    }



    //
    // Standard Methods
    //

    @Override
    public String toString() {
        ToStringBuilder stringBuilder = new ToStringBuilder(this)
                .append("internalEventSource", internalEventSource)
                .append("internalEventTarget", internalEventTarget);
        int counter = 0;
        for(TopicFilter currentTopicFilter : getEventTopicFilters()){
            stringBuilder.append("eventTopicFilter"+"["+counter+"]", currentTopicFilter);
            counter++;
        }
        stringBuilder.append("eventOrigin", eventOrigin)
                .append("eventFinalDestination", eventFinalDestination)
                .append("temporalWindow", temporalWindow)
                .appendSuper(super.toString());
        return(stringBuilder.toString());
    }
}
