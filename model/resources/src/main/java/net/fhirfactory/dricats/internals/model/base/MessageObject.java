/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.internals.model.base;

import java.io.Serial;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.internals.model.base.dataytypes.SerialisableObject;

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

    private IntegrationEndpoint messageSource;
    private IntegrationEndpoint messageTarget;
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

    //
    // Bean Methods
    //

    public IntegrationEndpoint getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(IntegrationEndpoint messageSource) {
        this.messageSource = messageSource;
    }

    public IntegrationEndpoint getMessageTarget() {
        return messageTarget;
    }

    public void setMessageTarget(IntegrationEndpoint messageTarget) {
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
}
