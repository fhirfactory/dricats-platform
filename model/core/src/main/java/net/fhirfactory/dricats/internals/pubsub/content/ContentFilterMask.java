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
package net.fhirfactory.dricats.internals.pubsub.content;

import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.internals.pubsub.common.FilterMaskBase;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentFilterMask extends FilterMaskBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900001L;
    private static final Logger LOG = LoggerFactory.getLogger(ContentFilterMask.class);

    //
     // Attributes
    //
    private List<MimeTypeEnum> supportedMediaTypes;

    //
    // Constructor(s)
    //
    public ContentFilterMask() {
        super();
        this.supportedMediaTypes = new ArrayList<>();
    }

    //
    // Getters and Setters
    //
    public List<MimeTypeEnum> getSupportedMediaTypes() {
        return supportedMediaTypes;
    }

    public void setMediaType(List<MimeTypeEnum> mediaTypes) {
        this.supportedMediaTypes = mediaTypes;
    }

    protected Logger getLogger() {
        return LOG;
    }

    //
    // Business Methods
    //
    public boolean filter(Topic contentTopic, MimeTypeEnum messageMediaType){
        getLogger().debug(".filter(Topic, MediaType): Entry, contentTopic -> {}, messageMediaType -> {}", contentTopic, messageMediaType);
        boolean mediaTypeTestOutcome = false;
        for(MimeTypeEnum currentMediaType : getSupportedMediaTypes()){
            if(currentMediaType.equals(messageMediaType)){
                mediaTypeTestOutcome = true;
                break;
            }
        }
        if(!mediaTypeTestOutcome){
            getLogger().debug(".filter(): Exit, message media type does not match filter, returning false");
            return(false);
        }
        boolean topicCheckOutcome = filter(contentTopic);
        getLogger().debug(".filter(Topic, MediaType): Exit, topicCheckOutcome -> {}", topicCheckOutcome);
        return(topicCheckOutcome);
    }

    public boolean filter(Topic contentTopic){
        getLogger().debug(".filter(Topic): Entry, contentTopic -> {}", contentTopic);
        Boolean outcome = false;
        for(TopicFilter currentTopicFilter : getEventTopicFilters()){
            if(currentTopicFilter.filter(contentTopic.getTopicName())){
                outcome = true;
                break;
            }
        }
        getLogger().debug(".filter(Topic): Exit, outcome -> {}", outcome);
        return(outcome);
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("supportedMediaTypes", supportedMediaTypes)
                .append("internalEventSource", internalEventSource)
                .append("internalEventTarget", internalEventTarget)
                .append("eventTopicFilters", eventTopicFilters)
                .append("eventOrigin", eventOrigin)
                .append("eventFinalDestination", eventFinalDestination)
                .append("temporalWindow", temporalWindow)
                .toString();
    }
}
