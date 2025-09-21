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
package net.fhirfactory.dricats.datagrid.notificationgrid;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.internals.events.notifications.NotificationObject;
import net.fhirfactory.dricats.internals.events.notifications.NotificationSet;
import net.fhirfactory.dricats.internals.events.interfaces.ILocalNotificationService;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class LocalNotificationCache implements ILocalNotificationService {
    //
    // Housekeeping
    //

    private final Logger LOG = LoggerFactory.getLogger(LocalNotificationCache.class);

    //
    // Attributes
    //

    private Map<QualifiedNameToken, Queue<NotificationObject>> incomingQueueCache ;
    private Queue<NotificationObject> outgoingQueueCache ;

    @Inject
    ISubsystem subsystem;

    //
    // Constructor(s)
    //

    public LocalNotificationCache() {
        incomingQueueCache = new HashMap<>();
        outgoingQueueCache = new ConcurrentLinkedQueue<>();
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return LOG;
    }

    protected Map<QualifiedNameToken, Queue<NotificationObject>> getIncomingQueueCache() {
        return incomingQueueCache;
    }

    protected Queue<NotificationObject> getOutgoingQueueCache() {
        return outgoingQueueCache;
    }

    protected ISubsystem getSubsystem(){
        return(subsystem);
    }

    //
    // Business Methods
    //

    public void queueIncomingNotification(NotificationObject NotificationObject) {
        getLogger().debug(".queueMessage(): Entry, NotificationObject -> {}", NotificationObject);
        if(NotificationObject == null){
            getLogger().warn(".queueMessage(): Exit, Message object is null");
            return;
        }
        if(NotificationObject.getTarget() == null){
            getLogger().warn(".queueMessage(): Exit, Message target is null");
            return;
        }
        QualifiedNameToken qualifiedNameToken = NotificationObject.getTarget().getQualifiedName().getQualifiedNameToken();
        if(!getIncomingQueueCache().containsKey(qualifiedNameToken)){
            Queue<NotificationObject> incomingMessageQueue = new ConcurrentLinkedQueue<>();
            incomingMessageQueue.add(NotificationObject);
            getIncomingQueueCache().put(qualifiedNameToken, incomingMessageQueue);
        } else {
            getIncomingQueueCache().get(qualifiedNameToken).add(NotificationObject);
        }
        getLogger().debug(".queueMessage(): Exit");
    }

    @Override
    public NotificationObject peekNextNotification( QualifiedNameToken consumerIdToken){
        if(consumerIdToken == null){
            getLogger().debug(".peekNextNotification(): Exit, consumerIdToken is null");
            return(null);
        }
        if(getIncomingQueueCache().containsKey(consumerIdToken)){
            NotificationObject peek = getIncomingQueueCache().get(consumerIdToken).peek();
            getLogger().debug(".peekNextNotification(): Exit, returning ->{}", peek);
            return(peek);
        }
        getLogger().debug(".peekNextNotification(): Exit, nothing in queue");
        return(null);
    }

    @Override
    public NotificationObject pollNextNotification( QualifiedNameToken consumerIdToken){
        getLogger().debug(".pollNextNotification(): Entry, consumerIdToken -> {}", consumerIdToken);
        if(consumerIdToken == null){
            getLogger().debug(".pollNextNotification(): Exit, consumerIdToken is null");
            return(null);
        }
        if(getIncomingQueueCache().containsKey(consumerIdToken)){
            NotificationObject poll = getIncomingQueueCache().get(consumerIdToken).poll();
            getLogger().debug(".pollNextNotification(): Exit, returning ->{}", poll);
            return(poll);
        }
        getLogger().debug(".pollNextNotification(): Exit, nothing in queue");
        return(null);
    }

    @Override
    public NotificationSet pollNextNotification(QualifiedNameToken consumer, Integer size) {
        getLogger().debug(".pollNextNotification(): Entry, consumerIdToken -> {}, size -> {}", consumer, size);
        NotificationSet notificationSet = new NotificationSet();
        if(consumer == null){
            getLogger().debug(".pollNextNotification(): Exit, consumer is null");
            return(notificationSet);
        }
        if(!getIncomingQueueCache().containsKey(consumer)){
            getLogger().debug(".pollNextNotification(): Exit, no messages for consumer, returning empty list");
            return(notificationSet);
        }
        Queue<NotificationObject> consumerIncomingQueue = getIncomingQueueCache().get(consumer);
        int listSize = size;
        if(consumerIncomingQueue.size() < listSize){
            listSize = consumerIncomingQueue.size();
        }
        for(int counter = 0; counter < listSize ; counter++ ){
            NotificationObject currentNotification = consumerIncomingQueue.poll();
            notificationSet.addNotification(currentNotification);
        }
        getLogger().debug(".pollNextNotification(): Exit, notificationSet -> {}", notificationSet);
        return(notificationSet);
    }

    public void queueOutgoingNotification(NotificationObject NotificationObject) {
        getLogger().debug(".queueOutgoingNotification(): Entry, NotificationObject -> {}", NotificationObject);
        if(NotificationObject == null){
            getLogger().warn(".queueOutgoingNotification(): Exit, Message object is null");
            return;
        }
        getOutgoingQueueCache().add(NotificationObject);
        getLogger().debug(".queueOutgoingNotification(): Exit");
    }

    public NotificationObject peekOutgoingQueue( ){
        getLogger().debug(".queueOutgoingNotification(): Entry");
        NotificationObject peek = getOutgoingQueueCache().peek();
        getLogger().debug(".queueOutgoingNotification(): Exit, peek -> {}", peek);
        return(peek);
    }

    public NotificationObject pollOutgoingQueue(){
        getLogger().debug(".pollOutgoingQueue(): Entry");
        NotificationObject poll = getOutgoingQueueCache().poll();
        getLogger().debug(".pollOutgoingQueue(): Exit, poll -> {}", poll);
        return(poll);
    }

    @Override
    public LocalDateTime postNotification(NotificationObject message) {
        getLogger().debug(".postNotification(): Entry, message -> {}", message);
        LocalDateTime messagePostedInstant = LocalDateTime.now();
        if(message == null){
            getLogger().debug(".postNotification(): Exit, nothing to post, return -> {}", messagePostedInstant);
            return(messagePostedInstant);
        }
        DistributableObjectId messageTarget = message.getTarget();
        if(messageTarget == null){
            getLogger().debug(".postNotification(): Exit, no target, return -> {}", messagePostedInstant);
            return(messagePostedInstant);
        }
        QualifiedName targetSubsystemQualifiedName = messageTarget.getQualifiedName().extractQualifiedNameForQualifier(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType());
        QualifiedName localSubsystemQualifiedName = getSubsystem().getSubsystem().getObjectID().getQualifiedName();
        String targetSubsystemName = targetSubsystemQualifiedName.getUnqualifiedName().getValue();
        String localSubsystemName = localSubsystemQualifiedName.getUnqualifiedName().getValue();
        if(targetSubsystemName.contentEquals(localSubsystemName)){
            getLogger().trace(".postNotification(): queueing for local processing!");
            queueIncomingNotification(message);
        } else {
            getLogger().trace(".postNotification(): queueing for outgoing processing!");
            queueOutgoingNotification(message);
        }
        getLogger().debug(".postNotification(): Exit");
        return(messagePostedInstant);
    }
}
