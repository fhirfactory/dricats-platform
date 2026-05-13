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
package net.fhirfactory.dricats.internals.pubsub.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.events.messages.MessagePayload;
import net.fhirfactory.dricats.internals.pubsub.common.FilterMaskBase;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class MessageFilterMask extends FilterMaskBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageFilterMask.class);

    //
    // Constructors
    //
    public MessageFilterMask() {
        super();
    }

    //
    // Accessors
    //

    @JsonIgnore
    @Override
    protected Logger getLogger() {
        return LOG;
    }

    //
    // Business Methods
    //

    public Boolean filterMessageObject(MessageObject testMessageObject){
        getLogger().trace("filterMessageObject(): Entry, testMessageObject -> {}", testMessageObject);
        boolean topicFilterPassed = false;
        boolean messageOriginFilterPassed = false;
        boolean messageDestinationFilterPassed = false;
        boolean messageSourceFilterPassed = false;
        boolean messageTargetFilterPassed = false;
        boolean messageTemporalWindowFilterPassed = false;

        if(testMessageObject == null){
            getLogger().trace("filterMessageObject(): Exit, testMessageObject is null, returning false");
            return(false);
        }
        if(getEventTopicFilters() == null || getEventTopicFilters().isEmpty()){
            getLogger().trace("filterMessageObject(): Exit, eventTopics is null, returning false");
            if(allowAllTopics){
                topicFilterPassed = true;
            } else {
                getLogger().debug("filterMessageObject(): Exit, eventTopics is null AND allowAllTopics is false, returning false");
                return(false);
            }
        }

        MessagePayload messagePayload = null;
        Topic messageTopic = null;
        DistinguishedName messageTopicName = null;
        try{
            messagePayload = testMessageObject.getMessagePayload();
            messageTopic = messagePayload.getDataTopic();
            messageTopicName = messageTopic.getTopicName();
        } catch(Exception e){
            getLogger().warn("filterMessageObject(): Message has no Payload Topic");
            if(allowAllTopics){
                topicFilterPassed = true;
            } else {
                getLogger().debug("filterMessageObject(): Message has no Payload Topic or QualifiedName AND allowAllTopics is false, returning false");
                return(false);
            }
        }

        for(TopicFilter currentTopicFilter : getEventTopicFilters()){
            if(currentTopicFilter.filter(messageTopicName)){
                topicFilterPassed = true;
                break;
            }
        }

        if(!topicFilterPassed && !allowAllTopics){
            getLogger().debug("filterMessageObject(): Exit, topicFilterPassed is false AND allowAllTopics is false, returning false");
            return(false);
        }
        messageOriginFilterPassed = true;
        messageDestinationFilterPassed = true;
        messageTemporalWindowFilterPassed = filterEventTemporalWindow(testMessageObject.getEventReceiveDate());
        messageSourceFilterPassed = filterInternalEventSource(testMessageObject.getSource());
        messageTargetFilterPassed = filterInternalEventTarget(testMessageObject.getTarget());

        Boolean filterOutcome = messageOriginFilterPassed && messageDestinationFilterPassed && messageTemporalWindowFilterPassed && messageSourceFilterPassed && messageTargetFilterPassed;

        getLogger().trace("filterMessageObject(): Exit, filterOutcome -> {}", filterOutcome);
        return(filterOutcome);
    }
}
