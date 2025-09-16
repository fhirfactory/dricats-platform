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
package net.fhirfactory.dricats.model.messaging;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.common.naming.CommonName;
import net.fhirfactory.dricats.model.topology.implementation.layers.application.interfaces.MLLPInterface;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.model.common.SerialisableObject;


public class MessageObject extends SerialisableObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900051L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageObject.class);

    //
    // Attributes
    //

    private DistributableObjectId messageSource;
    private DistributableObjectId messageTarget;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime messageSendDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime messageReceiveDate;
    private String messageId;
    private Integer messageSequenceNumber;
    private MessagePayload messagePayload;

    //
    // Constructor(s)
    //
    
    public MessageObject(){
        super();
        setMessageReceiveDate(LocalDateTime.now());
        setMessageSource(new DistributableObjectId());
        setMessageTarget(new DistributableObjectId());
        setMessagePayload(new MessagePayload());
        setMessageSendDate(LocalDateTime.MIN);
        setMessageSequenceNumber(-1);
        setMessageId(UUID.randomUUID().toString());
        setId(new CommonName("Message("+getMessageId()+")"));
    }

    public MessageObject(DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, MessagePayload payload){
        super();
        setMessageReceiveDate(LocalDateTime.now());
        setMessageSendDate(sendDate);
        setMessageSource(source);
        setMessageTarget(target);
        setMessagePayload(payload);
        setMessageSequenceNumber(0);
        setMessageId(UUID.randomUUID().toString());
        setId(new CommonName("Message("+getMessageId()+")"));
    }

    public  MessageObject(String MessageId, DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, MessagePayload payload, int sequenceNumber ){
        this(source, target, sendDate, payload);
        setMessageId(MessageId);
        setMessageSequenceNumber(sequenceNumber);
        setId(new CommonName("Message("+getMessageId()+")"));
    }

    //
    // Bean Methods
    //

    public DistributableObjectId getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(DistributableObjectId messageSource) {
        this.messageSource = messageSource;
    }

    public DistributableObjectId getMessageTarget() {
        return messageTarget;
    }

    public void setMessageTarget(DistributableObjectId messageTarget) {
        this.messageTarget = messageTarget;
    }

    public LocalDateTime getMessageSendDate() {
        return messageSendDate;
    }

    public void setMessageSendDate(LocalDateTime messageSendDate) {
        this.messageSendDate = messageSendDate;
    }

    public LocalDateTime getMessageReceiveDate() {
        return messageReceiveDate;
    }

    public void setMessageReceiveDate(LocalDateTime messageReceiveDate) {
        this.messageReceiveDate = messageReceiveDate;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

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
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageSource", messageSource)
                .append("messageTarget", messageTarget)
                .append("messageSendDate", messageSendDate)
                .append("messageReceiveDate", messageReceiveDate)
                .append("messageId", messageId)
                .append("messageSequenceNumber", messageSequenceNumber)
                .append("messagePayload", messagePayload)
                .toString();
    }
}
