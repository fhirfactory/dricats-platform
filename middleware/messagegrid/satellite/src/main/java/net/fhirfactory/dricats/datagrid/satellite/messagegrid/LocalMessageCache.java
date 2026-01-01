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
package net.fhirfactory.dricats.datagrid.satellite.messagegrid;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.id.ObjectKey;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.events.interfaces.ILocalMessageService;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.events.messages.MessageSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class LocalMessageCache implements ILocalMessageService {
    //
    // Housekeeping
    //

    private final Logger LOG = LoggerFactory.getLogger(LocalMessageCache.class);

    //
    // Attributes
    //

    private Map<ObjectKey, Queue<MessageObject>> incomingQueueCache ;
    private Queue<MessageObject> outgoingQueueCache ;

    @Inject
    ISubsystem subsystem;

    //
    // Constructor(s)
    //

    public LocalMessageCache() {
        incomingQueueCache = new HashMap<>();
        outgoingQueueCache = new ConcurrentLinkedQueue<>();
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return LOG;
    }

    protected Map<ObjectKey, Queue<MessageObject>> getIncomingQueueCache() {
        return incomingQueueCache;
    }

    protected Queue<MessageObject> getOutgoingQueueCache() {
        return outgoingQueueCache;
    }

    protected ISubsystem getSubsystem(){
        return(subsystem);
    }

    //
    // Business Methods
    //

    public void queueIncomingMessage(MessageObject messageObject) {
        getLogger().debug(".queueMessage(): Entry, messageObject -> {}", messageObject);
        if(messageObject == null){
            getLogger().warn(".queueMessage(): Exit, Message object is null");
            return;
        }
        if(messageObject.getTarget() == null){
            getLogger().warn(".queueMessage(): Exit, Message target is null");
            return;
        }
        ObjectKey objectToken = messageObject.getTarget().getLocalObjectId();
        if(!getIncomingQueueCache().containsKey(objectToken)){
            Queue<MessageObject> incomingMessageQueue = new ConcurrentLinkedQueue<>();
            incomingMessageQueue.add(messageObject);
            getIncomingQueueCache().put(objectToken, incomingMessageQueue);
        } else {
            getIncomingQueueCache().get(objectToken).add(messageObject);
        }
        getLogger().debug(".queueMessage(): Exit");
    }

    @Override
    public MessageObject peekNextMessage( ObjectKey consumerObjectToken){
        if(consumerObjectToken == null){
            getLogger().debug(".peekIncomingMessage(): Exit, consumerIdToken is null");
            return(null);
        }
        if(getIncomingQueueCache().containsKey(consumerObjectToken)){
            MessageObject peek = getIncomingQueueCache().get(consumerObjectToken).peek();
            getLogger().debug(".peekIncomingMessage(): Exit, returning ->{}", peek);
            return(peek);
        }
        getLogger().debug(".peekIncomingMessage(): Exit, nothing in queue");
        return(null);
    }

    @Override
    public MessageObject pollNextMessage( ObjectKey consumerObjectToken){
        getLogger().debug(".pollNextMessage(): Entry, consumerIdToken -> {}", consumerObjectToken);
        if(consumerObjectToken == null){
            getLogger().debug(".pollNextMessage(): Exit, consumerIdToken is null");
            return(null);
        }
        if(getIncomingQueueCache().containsKey(consumerObjectToken)){
            MessageObject poll = getIncomingQueueCache().get(consumerObjectToken).poll();
            getLogger().debug(".pollNextMessage(): Exit, returning ->{}", poll);
            return(poll);
        }
        getLogger().debug(".pollNextMessage(): Exit, nothing in queue");
        return(null);
    }

    public void queueOutgoingMessage(MessageObject messageObject) {
        getLogger().debug(".queueOutgoingMessage(): Entry, messageObject -> {}", messageObject);
        if(messageObject == null){
            getLogger().warn(".queueOutgoingMessage(): Exit, Message object is null");
            return;
        }
        getOutgoingQueueCache().add(messageObject);
        getLogger().debug(".queueOutgoingMessage(): Exit");
    }

    public MessageObject peekOutgoingQueue( ){
        getLogger().debug(".peekOutgoingQueue(): Entry");
        MessageObject peek = getOutgoingQueueCache().peek();
        getLogger().debug(".peekOutgoingQueue(): Exit, peek -> {}", peek);
        return(peek);
    }

    public MessageObject pollOutgoingMessage(){
        getLogger().debug(".pollOutgoingMessage(): Entry");
        MessageObject poll = getOutgoingQueueCache().poll();
        getLogger().debug(".pollIncomingMessage(): Exit, poll -> {}", poll);
        return(poll);
    }

    @Override
    public LocalDateTime postMessage(MessageObject message) {
        getLogger().debug(".postMessage(): Entry, message -> {}", message);
        LocalDateTime messagePostedInstant = LocalDateTime.now();
        if(message == null){
            getLogger().debug(".postMessage(): Exit, nothing to post, return -> {}", messagePostedInstant);
            return(messagePostedInstant);
        }
        ObjectId messageTarget = message.getTarget().getLocalObjectId();
        if(messageTarget == null){
            getLogger().debug(".postMessage(): Exit, no target, return -> {}", messagePostedInstant);
            return(messagePostedInstant);
        }
        FullyDistinguishedName targetSubsystemQualifiedName = messageTarget.getFullyDistinguishedName().extractQualifiedNameForQualifier(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType());
        FullyDistinguishedName localSubsystemQualifiedName = getSubsystem().getSubsystem().getObjectId().getFullyDistinguishedName();
        String targetSubsystemName = targetSubsystemQualifiedName.getUnqualifiedName().getValue();
        String localSubsystemName = localSubsystemQualifiedName.getUnqualifiedName().getValue();
        if(targetSubsystemName.contentEquals(localSubsystemName)){
            getLogger().trace(".postMessage(): queueing for local processing!");
            queueIncomingMessage(message);
        } else {
            getLogger().trace(".postMessage(): queueing for outgoing processing!");
            queueOutgoingMessage(message);
        }
        getLogger().debug(".postMessage(): Exit");
        return(messagePostedInstant);
    }

    @Override
    public MessageSet pollNextMessage(ObjectKey consumerId, Integer size) {
        getLogger().debug(".pollNextMessage(): Entry, consumerIdToken -> {}, size -> {}", consumerId, size);
        MessageSet messageSet = new MessageSet();
        if(consumerId == null){
            getLogger().debug(".pollNextMessage(): Exit, consumer is null");
            return(messageSet);
        }
        if(!getIncomingQueueCache().containsKey(consumerId)){
            getLogger().debug(".pollNextMessage(): Exit, no messages for consumer, returning empty list");
            return(messageSet);
        }
        Queue<MessageObject> consumerIncomingQueue = getIncomingQueueCache().get(consumerId);
        int listSize = size;
        if(consumerIncomingQueue.size() < listSize){
            listSize = consumerIncomingQueue.size();
        }
        for(int counter = 0; counter < listSize ; counter++ ){
            MessageObject currentMessage = consumerIncomingQueue.poll();
            messageSet.addMessage(currentMessage);
        }
        getLogger().debug(".pollNextMessage(): Exit, messageSet -> {}", messageSet);
        return(messageSet);
    }
}
