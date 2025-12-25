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
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.Payload;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilterMask;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageFilterMask;
import net.fhirfactory.dricats.internals.topics.Topic;
import net.fhirfactory.dricats.reference.archimate.relationships.FlowRelationship;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PathwayElement extends FlowRelationship implements Serializable {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(PathwayElement.class);
    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //
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
    }

    public PathwayElement(ElementReference enablerComponent, String pathwayElementId, String pathwayElementName, String relationshipDescription, ElementReference ingressPoint, ElementReference egressPoint, List<Topic> supportedTopics){
        super();
        FullyDistinguishedName pathwayElementIdName = new FullyDistinguishedName(enablerComponent.getLocalObjectId().getFullyDistinguishedName());
        RelativeDistinguishedName pathwayElementUnqaulifiedName = new RelativeDistinguishedName("PathwayElement", pathwayElementId);
        pathwayElementIdName.appendUnqualifiedName(pathwayElementUnqaulifiedName);
        ObjectId derivedId = new ObjectId(pathwayElementIdName);
        this.setName(pathwayElementName);
        this.setDocumentation(relationshipDescription);
        this.setTarget(egressPoint);
        this.setSource(ingressPoint);
        this.setFlowType("Information");
        this.setType(RelationshipTypeEnum.FLOW);
        this.setSpecialization("PathwayElement");
        this.supportedTopics = new ArrayList<>();
        this.supportedTopics.addAll(supportedTopics);
    }

    //
    // Bean Methods
    //

    public List<MessageFilterMask> getIngressMessageFilter() {
        return ingressMessageFilter;
    }
    public void setIngressMessageFilter(List<MessageFilterMask> ingressMessageFilter) {
        this.ingressMessageFilter = ingressMessageFilter;
    }
    public List<MessageFilterMask> getEgressMessageFilter() {
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
        getLogger().debug(".passesIngressContentFilter(): Entry, payload -> {}", payload);
        if(getIngressContentFilter() == null){
            getLogger().debug(".passesIngressContentFilter(): Exit, ingressContentFilter is null, returning true");
            return(true);
        }
        if(getIngressContentFilter().isEmpty()){
            getLogger().debug(".passesIngressContentFilter(): Exit, ingressContentFilter is empty, returning true");
            return(true);
        }
        for(ContentFilterMask currentContentFilter : getIngressContentFilter()){
            getLogger().trace(".passesIngressContentFilter(): Processing ingress content filter {}", currentContentFilter);
            if(currentContentFilter.filter(payload.getDataTopic(), payload.getDataFormat())){
                getLogger().debug(".passesIngressContentFilter(): Exit, ingress content filter {} matched, returning true", currentContentFilter);
                return(true);
            }
        }
        getLogger().debug(".passesIngressContentFilter(): Exit, ingress content filter did not match, returning false");
        return (false);
    }

    @JsonIgnore
    public boolean passesEgressContentFilter(Payload payload){
        getLogger().debug(".passesEgressContentFilter(): Entry, payload -> {}", payload);
        if(getEgressContentFilter() == null){
            getLogger().debug(".passesEgressContentFilter(): Exit, egressContentFilter is null, returning true");
            return(true);
        }
        if(getEgressContentFilter().isEmpty()){
            getLogger().debug(".passesEgressContentFilter(): Exit, egressContentFilter is empty, returning true");
            return(true);
        }
        for(ContentFilterMask currentContentFilter : getEgressContentFilter()){
            getLogger().trace(".passesEgressContentFilter(): Processing egress content filter {}", currentContentFilter);
            if(currentContentFilter.filter(payload.getDataTopic(), payload.getDataFormat())){
                getLogger().debug(".passesEgressContentFilter(): Exit, egress content filter {} matched, returning true", currentContentFilter);
                return(true);
            }
        }
        getLogger().debug(".passesEgressContentFilter(): Exit, egress content filter did not match, returning false");
        return(false);
    }

    @JsonIgnore
    public boolean passesIngressMessageFilter(MessageObject message){
        getLogger().debug(".passesIngressMessageFilter(): Entry, message -> {}", message);
        if(getIngressMessageFilter() == null){
            getLogger().debug(".passesIngressMessageFilter(): Exit, ingressMessageFilter is null, returning true");
            return(true);
        }
        if(getIngressMessageFilter().isEmpty()){
            getLogger().debug(".passesIngressMessageFilter(): Exit, ingressMessageFilter is empty, returning true");
            return(true);
        }
        for(MessageFilterMask currentMessageFilter : getIngressMessageFilter()){
            getLogger().trace(".passesIngressMessageFilter(): Processing ingress message filter {}", currentMessageFilter);
            if(currentMessageFilter.filterMessageObject(message)){
                getLogger().debug(".passesIngressMessageFilter(): Exit, ingress message filter {} matched, returning true", currentMessageFilter);
                return(true);
            }
        }
        getLogger().debug(".passesIngressMessageFilter(): Exit, ingress message filter did not match, returning false");
        return(false);
    }

    @JsonIgnore
    public boolean passesEgressMessageFilter(MessageObject message){
        getLogger().debug(".passesEgressMessageFilter(): Entry, message -> {}", message);
        if(getEgressMessageFilter() == null){
            getLogger().debug(".passesEgressMessageFilter(): Exit, egressMessageFilter is null, returning true");
            return(true);
        }
        if(getEgressMessageFilter().isEmpty()){
            getLogger().debug(".passesEgressMessageFilter(): Exit, egressMessageFilter is empty, returning true");
            return(true);
        }
        for(MessageFilterMask currentMessageFilter : getEgressMessageFilter()){
            getLogger().trace(".passesEgressMessageFilter(): Processing egress message filter {}", currentMessageFilter);
            if(currentMessageFilter.filterMessageObject(message)){
                getLogger().debug(".passesEgressMessageFilter(): Exit, egress message filter {} matched, returning true", currentMessageFilter);
                return(true);
            }
        }
        getLogger().debug(".passesEgressMessageFilter(): Exit, egress message filter did not match, returning false");
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
                .append("flowType", getFlowType())
                .append("type", getType())
                .append("source", getSource())
                .append("target", getTarget())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getProperties())
                .append("identifiers", getIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("id", getLocalId())
                .append("metadata", getMetadata())
                .toString();
    }
}
