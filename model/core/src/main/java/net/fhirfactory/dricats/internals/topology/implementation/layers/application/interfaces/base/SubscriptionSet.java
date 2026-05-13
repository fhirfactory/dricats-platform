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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base;


import net.fhirfactory.dricats.internals.pubsub.content.ContentSubscription;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageSubscription;
import net.fhirfactory.dricats.internals.pubsub.notifications.NotificationSubscription;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationFunction;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionSet extends ApplicationFunction {

    //
    // Attributes
    //

    private List<MessageSubscription> messageSubscriptions;
    private List<NotificationSubscription> notificationSubscriptions;
    private List<ContentSubscription> contentSubscriptions;

    //
    // Constructor(s)
    //

    public SubscriptionSet(){
        super();
        this.messageSubscriptions = new ArrayList<>();
        this.notificationSubscriptions = new ArrayList<>();
        this.contentSubscriptions = new ArrayList<>();
    }

    //
    // Bean Methods
    //

    public List<MessageSubscription> getMessageSubscriptions() {
        if(this.messageSubscriptions == null){
            this.messageSubscriptions = new ArrayList<>();
        }
        return messageSubscriptions;
    }
    public void setMessageSubscriptions(List<MessageSubscription> messageSubscriptions) {
        this.messageSubscriptions = messageSubscriptions;
    }
    public List<NotificationSubscription> getNotificationSubscriptions() {
        if(this.notificationSubscriptions == null){
            this.notificationSubscriptions = new ArrayList<>();
        }
        return notificationSubscriptions;
    }
    public void setNotificationSubscriptions(List<NotificationSubscription> notificationSubscriptions) {
        this.notificationSubscriptions = notificationSubscriptions;
    }
    public List<ContentSubscription> getContentSubscriptions() {
        if(this.contentSubscriptions == null){
            this.contentSubscriptions = new ArrayList<>();
        }
        return contentSubscriptions;
    }
    public void setContentSubscriptions(List<ContentSubscription> contentSubscriptions) {
        this.contentSubscriptions = contentSubscriptions;
    }
}
