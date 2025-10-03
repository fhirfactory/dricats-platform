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
package net.fhirfactory.dricats.internals.pubsub.notifications;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.events.notifications.NotificationObject;
import net.fhirfactory.dricats.internals.pubsub.common.SubscriptionBase;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationFunction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class NotificationSubscription extends SubscriptionBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678200051L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSubscription.class);

    //
    // Attributes
    //

    private NotificationSubscriptionMask notificationSubscriptionMask;


    //
    // Constructors
    //

    public NotificationSubscription() {
        super();
    }

    public NotificationSubscription(DistributableObjectId subscriber, ApplicationFunction subscriberFunction) {
        super(subscriber, subscriberFunction, "Notification");
    }

    public NotificationSubscription(DistributableObjectId subscriber, ApplicationFunction subscriberFunction, NotificationSubscriptionMask subscriptionMask) {
        super(subscriber, subscriberFunction, "Notification");
        this.notificationSubscriptionMask = subscriptionMask;
    }

    //
    // Getters and Setters
    //

    public NotificationSubscriptionMask getMessageSubscriptionMask() {
        return notificationSubscriptionMask;
    }

    public void setMessageSubscriptionMask(NotificationSubscriptionMask notificationSubscriptionMask) {
        this.notificationSubscriptionMask = notificationSubscriptionMask;
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
     * Supports wildcard '*' in source/target QualifiedName values, and in notificationId bounds.
     * Null criteria are treated as "no restriction" for that field.
     */
    public boolean filterNotification(NotificationObject notification){
        getLogger().debug(".filterNotification(): Entry, notification -> {}", notification);
        if(notification == null){
            return false;
        }
        // Source mask
        Boolean sourceFilterOutcome = getMessageSubscriptionMask().filterInternalEventSource(notification.getSource());
        if(!sourceFilterOutcome){
            getLogger().debug(".filterNotification(): Exit, notification source does not match mask, returning false");
            return false;
        }
        // Target mask
        Boolean targetFilterOutcome = getMessageSubscriptionMask().filterInternalEventTarget(notification.getTarget());
        if(!targetFilterOutcome){
            getLogger().debug(".filterNotification(): Exit, notification target does not match mask, returning false");
            return false;
        }
        // Date window (use send date if available else receive date)
        Boolean temporalWindowOutcome = getMessageSubscriptionMask().filterEventTemporalWindow(notification.getEventSendDate());
        if(!temporalWindowOutcome){
            getLogger().debug(".filterNotification(): Exit, notification time not within temporal window, returning false");
            return false;
        }
        // Topics
        if(notification.getNotificationPayload() == null){
            getLogger().debug(".filterNotification(): Exit, notification payload is null, returning false");
            return false;
        }
        Boolean topicFilterOutcome = getMessageSubscriptionMask().filterEventTopics(notification.getNotificationPayload().getDataTopic());
        if(!topicFilterOutcome){
            getLogger().debug(".filterNotification(): Exit, notification topic does not match mask, returning false");
            return false;
        }
        // Message Origin
        boolean notificationOriginOutcome = getMessageSubscriptionMask().getEventOrigin().contentEquals("*");
        if(!notificationOriginOutcome) {
            notificationOriginOutcome = getMessageSubscriptionMask().filterEventOrigin(notification.getMetadata().getSourceSystemName());
            if (!notificationOriginOutcome) {
                getLogger().debug(".filterNotification(): Exit, notification origin does not match mask, returning false");
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
                .append("notificationSubscriptionMask", notificationSubscriptionMask)
                .appendSuper(super.toString())
                .toString();
    }
}
