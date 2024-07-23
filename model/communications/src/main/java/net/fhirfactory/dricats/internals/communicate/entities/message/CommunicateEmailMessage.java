/*
 * Copyright (c) 2021 ACT Health
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

import net.fhirfactory.dricats.internals.communicate.entities.message.datatypes.CommunicateEmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// A basic class for the components of email that are supported
public class CommunicateEmailMessage extends CommunicateMessage{
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900161L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateEmailMessage.class);

    //
    // Attributes
    //

    private String from;
    private List<String> to;
    private List<String> cc;
    private String subject;
    private String content;               //TODO this may be long and could possibly be a stream instead
    private String contentType;
    private List<CommunicateEmailAttachment> attachments;

    //
    // Constructor(s)
    //

    public CommunicateEmailMessage() {
        super();
        this.attachments = new ArrayList<>();
        this.content = null;
        this.contentType = null;
        this.from = null;
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.subject = null;
    }

    //
    // Getters and Setters
    //

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<CommunicateEmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<CommunicateEmailAttachment> attachments) {
        this.attachments = attachments;
    }

    //
    // To String
    //

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommunicateEmailMessage{");
        sb.append("from='").append(getFrom()).append('\'');
        sb.append(", to=").append(getTo());
        sb.append(", cc=").append(getCc());
        sb.append(", subject='").append(getSubject()).append('\'');
        sb.append(", content='").append(getContent()).append('\'');
        sb.append(", contentType='").append(getContentType()).append('\'');
        sb.append(", attachments=").append(getAttachments());
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
        CommunicateEmailMessage that = (CommunicateEmailMessage) o;
        return Objects.equals(getFrom(), that.getFrom()) && Objects.equals(getTo(), that.getTo()) && Objects.equals(getCc(), that.getCc()) && Objects.equals(getSubject(), that.getSubject()) && Objects.equals(getContent(), that.getContent()) && Objects.equals(getContentType(), that.getContentType()) && Objects.equals(getAttachments(), that.getAttachments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFrom(), getTo(), getCc(), getSubject(), getContent(), getContentType(), getAttachments());
    }
}
