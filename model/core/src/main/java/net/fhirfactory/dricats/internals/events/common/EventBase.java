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
package net.fhirfactory.dricats.internals.events.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationEvent;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EventBase extends ApplicationEvent implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //

    private ElementReference target;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime eventSendDate;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime eventReceiveDate;
    private Map<Integer, ElementReference> history;


    //
    // Constructor(s)
    //

    public EventBase() {
        super();
        setEventSendDate(LocalDateTime.now());
     }

     public EventBase(EventBase ori){
         super(ori);
         this.target = SerializationUtils.clone(ori.getTarget());
         this.eventSendDate = ori.eventSendDate;
         this.eventReceiveDate = ori.eventReceiveDate;
         this.history = new HashMap<>(ori.history);
     }

                      //
                      // Accessors
                      //

    public ElementReference getTarget(){
        return target;
    }

    public void setTarget(ElementReference targetObjectID) {
        this.target = targetObjectID;
    }

    public LocalDateTime getEventSendDate() {
        return eventSendDate;
    }

    public void setEventSendDate(LocalDateTime eventSendDate) {
        this.eventSendDate = eventSendDate;
    }

    public LocalDateTime getEventReceiveDate() {
        return eventReceiveDate;
    }

    public void setEventReceiveDate(LocalDateTime eventReceiveDate) {
        this.eventReceiveDate = eventReceiveDate;
    }

    public Map<Integer, ElementReference> getHistory() {
        return history;
    }

    public void setHistory(Map<Integer, ElementReference> history) {
        this.history = history;
    }

    //
     // Business Methods
    //


    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("target", getTarget())
                .append("eventSendDate", getEventSendDate())
                .append("eventReceiveDate", getEventReceiveDate())
                .append("history", getHistory())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("elementType", getElementType())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("triggeringFunctions", getTriggeringFunctions())
                .append("triggeredFunctions", getTriggeredFunctions())
                .append("associatedData", getAssociatedData())
                .append("source", getSource())
                .toString();
    }
}
