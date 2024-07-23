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

import net.fhirfactory.dricats.internals.communicate.entities.message.datatypes.CommunicateMessageReadTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommunicateTrackedMessage extends CommunicateMessage {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900165L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateTrackedMessage.class);

    //
    // Attributes
    //

    private List<CommunicateMessageReadTag> messageReadTags;

    //
    // Constructor(s)
    //

    public CommunicateTrackedMessage() {
        messageReadTags = new ArrayList<>();
    }

    //
    // Bean Methods
    //

    public List<CommunicateMessageReadTag> getMessageReadTags() {
        return messageReadTags;
    }

    public void setMessageReadTags(List<CommunicateMessageReadTag> messageReadTags) {
        this.messageReadTags = messageReadTags;
    }

    //
    // Utility Methods
    //

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommunicateTrackedMessage{");
        sb.append("messageReadTags=").append(getMessageReadTags());
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
        if (!super.equals(o)) return false;
        CommunicateTrackedMessage that = (CommunicateTrackedMessage) o;
        return Objects.equals(getMessageReadTags(), that.getMessageReadTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMessageReadTags());
    }
}
