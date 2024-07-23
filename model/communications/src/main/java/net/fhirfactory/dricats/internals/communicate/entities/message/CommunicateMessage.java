/*
 * Copyright (c) 2021 Mark A. Hunter
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

import net.fhirfactory.dricats.internals.communicate.entities.message.datatypes.CommunicateMessageContentBase;
import net.fhirfactory.dricats.internals.communicate.entities.message.datatypes.CommunicateMessageIdentifier;
import net.fhirfactory.dricats.internals.communicate.entities.message.valuesets.CommunicateMessageTypeEnum;
import net.fhirfactory.dricats.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.dricats.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.dricats.internals.model.base.MessageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.Objects;

public class CommunicateMessage extends MessageObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900147L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateMessage.class);

    //
    // Attributes
    //

    private CommunicateMessageIdentifier messageIdentifier;
    private CommunicateRoomReference sourceRoom;
    private CommunicateUserReference sourceUser;
    private CommunicateMessageTypeEnum messageType;
    private CommunicateMessageContentBase messageContent;
    private CommunicateMessageIdentifier inResponseTo;

    //
    // Constructor(s)
    //

    //
    // Bean Methods
    //

    public CommunicateMessageIdentifier getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(CommunicateMessageIdentifier messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public CommunicateRoomReference getSourceRoom() {
        return sourceRoom;
    }

    public void setSourceRoom(CommunicateRoomReference sourceRoom) {
        this.sourceRoom = sourceRoom;
    }

    public CommunicateUserReference getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(CommunicateUserReference sourceUser) {
        this.sourceUser = sourceUser;
    }

    public CommunicateMessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(CommunicateMessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public CommunicateMessageContentBase getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(CommunicateMessageContentBase messageContent) {
        this.messageContent = messageContent;
    }

    public CommunicateMessageIdentifier getInResponseTo() {
        return inResponseTo;
    }

    public void setInResponseTo(CommunicateMessageIdentifier inResponseTo) {
        this.inResponseTo = inResponseTo;
    }

    //
    // Utility Methods
    //

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommunicateMessage{");
        sb.append("messageIdentifier=").append(getMessageIdentifier());
        sb.append(", sourceRoom=").append(getSourceRoom());
        sb.append(", sourceUser=").append(getSourceUser());
        sb.append(", messageType=").append(getMessageType());
        sb.append(", messageContent=").append(getMessageContent());
        sb.append(", inResponseTo=").append(getInResponseTo());
        sb.append(", messageSource=").append(getMessageSource());
        sb.append(", messageTarget=").append(getMessageTarget());
        sb.append(", messageSendDate=").append(getMessageSendDate());
        sb.append(", messageReceiveDate=").append(getMessageReceiveDate());
        sb.append(", messageId='").append(getMessageId()).append('\'');
        sb.append(", messageSequenceNumber=").append(getMessageSequenceNumber());
        sb.append(", messagePayload=").append(getMessagePayload());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        CommunicateMessage that = (CommunicateMessage) o;
        return Objects.equals(getMessageIdentifier(), that.getMessageIdentifier()) && Objects.equals(getSourceRoom(), that.getSourceRoom()) && Objects.equals(getSourceUser(), that.getSourceUser()) && getMessageType() == that.getMessageType() && Objects.equals(getMessageContent(), that.getMessageContent()) && Objects.equals(getInResponseTo(), that.getInResponseTo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMessageIdentifier(), getSourceRoom(), getSourceUser(), getMessageType(), getMessageContent(), getInResponseTo());
    }
}
