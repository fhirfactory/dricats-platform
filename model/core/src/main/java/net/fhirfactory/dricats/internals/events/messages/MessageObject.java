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
package net.fhirfactory.dricats.internals.events.messages;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.events.common.EventBase;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.internals.common.SerialisableObject;


public class MessageObject extends EventBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900051L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageObject.class);

    //
    // Attributes
    //

    private Integer messageSequenceNumber;
    private MessagePayload messagePayload;

    //
    // Constructor(s)
    //
    
    public MessageObject(){
        super();
        UnqualifiedName unqualifiedName = new UnqualifiedName("Message", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
        setMessagePayload(new MessagePayload());
        setMessageSequenceNumber(-1);
    }

    public MessageObject(MessageObject messageObject){
        super(messageObject);
        if(messageObject == null){
            return;
        }
        setMessageSequenceNumber(messageObject.getMessageSequenceNumber());
        setMessagePayload(SerializationUtils.clone(messageObject.getMessagePayload()));
    }

    public MessageObject(DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, MessagePayload payload){
        super();
        setEventReceiveDate(LocalDateTime.now());
        setEventSendDate(sendDate);
        setSource(source);
        setTarget(target);
        setMessagePayload(payload);
        setMessageSequenceNumber(0);
        UnqualifiedName unqualifiedName = new UnqualifiedName("Message", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
    }

    public  MessageObject(String MessageId, DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, MessagePayload payload, int sequenceNumber ){
        this(source, target, sendDate, payload);
        UnqualifiedName unqualifiedName = new UnqualifiedName("Message", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
        setMessageSequenceNumber(sequenceNumber);
    }

    //
    // Bean Methods
    //

    public Integer getMessageSequenceNumber() {
        return messageSequenceNumber;
    }

    public void setMessageSequenceNumber(Integer messageSequenceNumber) {
        this.messageSequenceNumber = messageSequenceNumber;
    }

    public MessagePayload getMessagePayload() {
        return messagePayload;
    }

    public void setMessagePayload(MessagePayload messagePayload) {
        this.messagePayload = messagePayload;
    }

    //
     // Business Methods
    //

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageSequenceNumber", messageSequenceNumber)
                .append("messagePayload", messagePayload)
                .appendSuper(super.toString())
                .toString();
    }
}
