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

package net.fhirfactory.dricats.internals.pathways;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.Payload;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilterMask;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageFilterMask;
import net.fhirfactory.dricats.internals.topics.Topic;
import net.fhirfactory.dricats.reference.archimate.relationships.FlowRelationship;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PathwayElement extends FlowRelationship implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //
    public static final String ELEMENT_SPECIALIZATION = "PathwayElement";

    private ElementReference utilisedApplicationService;
    private List<MessageFilterMask> ingressMessageFilter;
    private List<MessageFilterMask> egressMessageFilter;
    private List<ContentFilterMask> ingressContentFilter;
    private List<ContentFilterMask> egressContentFilter;
    private List<Topic> supportedTopics;

    //
    // Constructor(s)
    //
    public PathwayElement(){
        super();
        this.supportedTopics = new ArrayList<>();
        this.ingressContentFilter = new ArrayList<>();
        this.egressContentFilter = new ArrayList<>();
        this.ingressMessageFilter = new ArrayList<>();
        this.egressMessageFilter = new ArrayList<>();
        this.utilisedApplicationService = null;
    }

    public PathwayElement(ElementReference enablerComponent, String pathwayElementName, String relationshipDescription, ElementReference ingressPoint, ElementReference egressPoint, List<Topic> supportedTopics){
        this();
        DistinguishedName pathwayElementIdName = new DistinguishedName(enablerComponent.getElementIdentifier().getIdentifierValue());
        RelativeDistinguishedName pathwayElementUnqaulifiedName = new RelativeDistinguishedName(ELEMENT_SPECIALIZATION, pathwayElementName);
        pathwayElementIdName.appendUnqualifiedName(pathwayElementUnqaulifiedName);
        ElementIdentifier elementIdentifier = new ElementIdentifier(pathwayElementIdName);
        setIdentifier(elementIdentifier);
        setShortName(pathwayElementName);
        this.setDocumentation(relationshipDescription);
        this.setTarget(egressPoint);
        this.setSource(ingressPoint);
        this.setFlowType("Information");
        this.setType(RelationshipTypeEnum.FLOW);
        this.setSpecialization(ELEMENT_SPECIALIZATION);
        this.supportedTopics.addAll(supportedTopics);
    }

    //
    // Bean Methods
    //

    public List<MessageFilterMask> getIngressMessageFilter() {
        if(this.ingressMessageFilter == null){
            this.ingressMessageFilter = new ArrayList<>();
        }
        return ingressMessageFilter;
    }
    public void setIngressMessageFilter(List<MessageFilterMask> ingressMessageFilter) {
        this.ingressMessageFilter = ingressMessageFilter;
    }
    public List<MessageFilterMask> getEgressMessageFilter() {
        if(this.egressMessageFilter == null){
            this.egressMessageFilter = new ArrayList<>();
        }
        return egressMessageFilter;
    }
    public void setEgressMessageFilter(List<MessageFilterMask> egressMessageFilter) {
        this.egressMessageFilter = egressMessageFilter;
    }

    public ElementReference getUtilisedApplicationService() {
        return utilisedApplicationService;
    }

    public void setUtilisedApplicationService(ElementReference utilisedApplicationService) {
        this.utilisedApplicationService = utilisedApplicationService;
    }

    public List<Topic> getSupportedTopics() {
        return supportedTopics;
    }

    public void setSupportedTopics(List<Topic> supportedTopics) {
        this.supportedTopics = supportedTopics;
    }

    public List<ContentFilterMask> getIngressContentFilter() {
        return ingressContentFilter;
    }

    public void setIngressContentFilter(List<ContentFilterMask> ingressContentFilter) {
        this.ingressContentFilter = ingressContentFilter;
    }

    public List<ContentFilterMask> getEgressContentFilter() {
        return egressContentFilter;
    }

    public void setEgressContentFilter(List<ContentFilterMask> egressContentFilter) {
        this.egressContentFilter = egressContentFilter;
    }

    //
    // Business Methods
    //

    @JsonIgnore
    public boolean passesIngressContentFilter(Payload payload){
        if(getIngressContentFilter() == null){
            return(true);
        }
        if(getIngressContentFilter().isEmpty()){
            return(true);
        }
        for(ContentFilterMask currentContentFilter : getIngressContentFilter()){
            if(currentContentFilter.filter(payload.getDataTopic(), payload.getDataFormat())){
                return(true);
            }
        }
        return (false);
    }

    @JsonIgnore
    public boolean passesEgressContentFilter(Payload payload){
        if(getEgressContentFilter() == null){
            return(true);
        }
        if(getEgressContentFilter().isEmpty()){
            return(true);
        }
        for(ContentFilterMask currentContentFilter : getEgressContentFilter()){
            if(currentContentFilter.filter(payload.getDataTopic(), payload.getDataFormat())){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore
    public boolean passesIngressMessageFilter(MessageObject message){
        if(getIngressMessageFilter() == null){
            return(true);
        }
        if(getIngressMessageFilter().isEmpty()){
            return(true);
        }
        for(MessageFilterMask currentMessageFilter : getIngressMessageFilter()){
            if(currentMessageFilter.filterMessageObject(message)){
                return(true);
            }
        }
        return(false);
    }

    @JsonIgnore
    public boolean passesEgressMessageFilter(MessageObject message){
        if(getEgressMessageFilter() == null){
            return(true);
        }
        if(getEgressMessageFilter().isEmpty()){
            return(true);
        }
        for(MessageFilterMask currentMessageFilter : getEgressMessageFilter()){
            if(currentMessageFilter.filterMessageObject(message)){
                return(true);
            }
        }
        return(false);
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("utilisedApplicationService", getUtilisedApplicationService())
                .append("ingressMessageFilter", getIngressMessageFilter())
                .append("egressMessageFilter", getEgressMessageFilter())
                .append("ingressContentFilter", getIngressContentFilter())
                .append("egressContentFilter", getEgressContentFilter())
                .append("supportedTopics", getSupportedTopics())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("type", getType())
                .append("source", getSource())
                .append("target", getTarget())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getProperties())
                .append("flowType", getFlowType())
                .toString();
    }
}
