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

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.pubsub.common.SubscriptionBase;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationFunction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class MessageSubscription extends SubscriptionBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678200051L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageSubscription.class);

    //
    // Attributes
    //

    private MessageSubscriptionMask messageSubscriptionMask;

    //
    // Constructors
    //

    public MessageSubscription() {
        super();
    }

    public MessageSubscription(DistributableObjectId subscriber, ApplicationFunction subscriberFunction) {
        super(subscriber, subscriberFunction, "Message");
    }

    public MessageSubscription(DistributableObjectId subscriber, ApplicationFunction subscriberFunction, MessageSubscriptionMask subscriptionMask) {
        super(subscriber, subscriberFunction, "Message");
        this.messageSubscriptionMask = subscriptionMask;
    }

    //
    // Getters and Setters
    //

    public MessageSubscriptionMask getMessageSubscriptionMask() {
        return messageSubscriptionMask;
    }

    public void setMessageSubscriptionMask(MessageSubscriptionMask messageSubscriptionMask) {
        this.messageSubscriptionMask = messageSubscriptionMask;
    }

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    //
    // Business Methods
    //
    /**
     * Filter a MessageObject against this subscription's criteria.
     * Supports wildcard '*' in source/target QualifiedName values, and in messageId bounds.
     * Null criteria are treated as "no restriction" for that field.
     */
    public boolean filterMessage(MessageObject message){
        getLogger().debug(".filterMessage(): Entry, message -> {}", message);
        if(message == null){
            return false;
        }
        // Source mask
        Boolean sourceFilterOutcome = getMessageSubscriptionMask().filterInternalEventSource(message.getSource());
        if(!sourceFilterOutcome){
            getLogger().debug(".filterMessage(): Exit, message source does not match mask, returning false");
            return false;
        }
        // Target mask
        Boolean targetFilterOutcome = getMessageSubscriptionMask().filterInternalEventTarget(message.getTarget());
        if(!targetFilterOutcome){
            getLogger().debug(".filterMessage(): Exit, message target does not match mask, returning false");
            return false;
        }
        // Date window (use send date if available else receive date)
        Boolean temporalWindowOutcome = getMessageSubscriptionMask().filterEventTemporalWindow(message.getEventSendDate());
        if(!temporalWindowOutcome){
            getLogger().debug(".filterMessage(): Exit, message time not within temporal window, returning false");
            return false;
        }
        // Topics
        if(message.getMessagePayload() == null){
            getLogger().debug(".filterMessage(): Exit, message payload is null, returning false");
            return false;
        }
        Boolean topicFilterOutcome = getMessageSubscriptionMask().filterEventTopics(message.getMessagePayload().getDataTopic());
        if(!topicFilterOutcome){
            getLogger().debug(".filterMessage(): Exit, message topic does not match mask, returning false");
            return false;
        }
        // Message Origin
        boolean messageOriginOutcome = getMessageSubscriptionMask().getEventOrigin().contentEquals("*");
        if(!messageOriginOutcome) {
            messageOriginOutcome = getMessageSubscriptionMask().filterEventOrigin(message.getMetadata().getSourceSystemName());
            if (!messageOriginOutcome) {
                getLogger().debug(".filterMessage(): Exit, message origin does not match mask, returning false");
                return false;
            }
        }
        return(true);
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageSubscriptionMask", messageSubscriptionMask)
                .appendSuper(super.toString())
                .toString();
    }
}
