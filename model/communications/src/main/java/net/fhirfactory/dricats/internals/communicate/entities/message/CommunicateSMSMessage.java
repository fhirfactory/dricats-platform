/*
 * Copyright (c) 2022 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.communicate.entities.message;

import net.fhirfactory.dricats.internals.communicate.entities.message.valuesets.CommunicateSMSStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;

//TODO work out where the split for general SMS in Pegacorn and Modica specific
//     stuff is
public class CommunicateSMSMessage extends CommunicateMessage {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900163L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateSMSMessage.class);

    //
    // Attributes
    //

    private String messageId;
    private String phoneNumber;
    private String message;
    private Instant sendDate;
    private CommunicateSMSStatusEnum status;

    //
    // Constructor(s)
    //

    public CommunicateSMSMessage() {
        super();
        status = CommunicateSMSStatusEnum.CREATED;
        this.message = null;
        this.messageId = null;
        this.phoneNumber = null;
        this.sendDate = null;
    }

    //
    // Getters and Setters
    //

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getSendDate() {
        return sendDate;
    }

    public void setSendDate(Instant sendDate) {
        this.sendDate = sendDate;
    }

    public CommunicateSMSStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CommunicateSMSStatusEnum status) {
        this.status = status;
    }

    //
    // To String
    //

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommunicateSMSMessage{");
        sb.append("messageId='").append(getMessageId()).append('\'');
        sb.append(", phoneNumber='").append(getPhoneNumber()).append('\'');
        sb.append(", message='").append(getMessage()).append('\'');
        sb.append(", sendDate=").append(getSendDate());
        sb.append(", status=").append(getStatus());
        sb.append(", messageIdentifier=").append(getMessageIdentifier());
        sb.append(", sourceRoom=").append(getSourceRoom());
        sb.append(", sourceUser=").append(getSourceUser());
        sb.append(", messageType=").append(getMessageType());
        sb.append(", messageContent=").append(getMessageContent());
        sb.append(", inResponseTo=").append(getInResponseTo());
        sb.append(", messageSource=").append(getMessageSource());
        sb.append(", messageTarget=").append(getMessageTarget());
        sb.append(", messageSendDate=").append(getMessageSendDate());
        sb.append(", messageReceiveDate=").append(getMessageReceiveDate());
        sb.append(", messageSequenceNumber=").append(getMessageSequenceNumber());
        sb.append(", messagePayload=").append(getMessagePayload());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommunicateSMSMessage that = (CommunicateSMSMessage) o;
        return Objects.equals(getMessageId(), that.getMessageId()) && Objects.equals(getPhoneNumber(), that.getPhoneNumber()) && Objects.equals(getMessage(), that.getMessage()) && Objects.equals(getSendDate(), that.getSendDate()) && getStatus() == that.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMessageId(), getPhoneNumber(), getMessage(), getSendDate(), getStatus());
    }
}
