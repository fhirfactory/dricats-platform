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

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.events.common.EventBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class MessageObject extends EventBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900051L;

    //
    // Attributes
    //

    public static final String ELEMENT_SPECIALIZATION = "Message";

    private Integer messageSequenceNumber;
    private MessagePayload messagePayload;

    //
    // Constructor(s)
    //
    
    public MessageObject(){
        super();
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(ELEMENT_SPECIALIZATION, UUID.randomUUID().toString());
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        ElementIdentifier identifier = new ElementIdentifier(qualifiedName);
        setIdentifier(identifier);
        setShortName(unqualifiedName.getUnqualifiedValue());
        setSpecialization(ELEMENT_SPECIALIZATION);
        setElementType(ElementTypeEnum.APPLICATION_DATA_OBJECT);
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

    public MessageObject(ElementReference source, ElementReference target, LocalDateTime sendDate, MessagePayload payload){
        super();
        setEventReceiveDate(LocalDateTime.now());
        setEventSendDate(sendDate);
        setSource(source);
        setTarget(target);
        setMessagePayload(payload);
        setMessageSequenceNumber(0);
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(ELEMENT_SPECIALIZATION, UUID.randomUUID().toString());
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        setShortName(unqualifiedName.getUnqualifiedValue());
        setSpecialization(ELEMENT_SPECIALIZATION);
        setElementType(ElementTypeEnum.APPLICATION_DATA_OBJECT);
        ElementIdentifier identifier = new ElementIdentifier(qualifiedName);
        setIdentifier(identifier);
    }

    public  MessageObject(String messageId, ElementReference source, ElementReference target, LocalDateTime sendDate, MessagePayload payload, int sequenceNumber ){
        this(source, target, sendDate, payload);
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(ELEMENT_SPECIALIZATION, messageId);
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        ElementIdentifier identifier = new ElementIdentifier(qualifiedName);
        setIdentifier(identifier);
        setSpecialization(ELEMENT_SPECIALIZATION);
        setElementType(ElementTypeEnum.APPLICATION_DATA_OBJECT);
        setShortName(messageId);
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageSequenceNumber", messageSequenceNumber)
                .append("messagePayload", messagePayload)
                .appendSuper(super.toString())
                .toString();
    }
}
