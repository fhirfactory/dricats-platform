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
package net.fhirfactory.dricats.internals.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.SubscriptionSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageSubscription extends SubscriptionSupport implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678200051L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageSubscription.class);

    //
    // Attributes
    //
    private DistributableObjectId messageSourceMask;
    private DistributableObjectId messageTargetMask;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime messagesAfterDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime messagesBeforeDate;
    private String messageIdGreaterThan;
    private String messageIdLessThan;
    private Integer messageSequenceNumberGreaterThan;
    private Integer messageSequenceNumberLessThan;

    //
    // Constructors
    //
    public MessageSubscription() {
        super();
    }

    public MessageSubscription(DistributableObjectId subscriber) {
        super(subscriber);
    }

    //
    // Getters and Setters
    //
    public DistributableObjectId getMessageSourceMask() {
        return messageSourceMask;
    }

    public void setMessageSourceMask(DistributableObjectId messageSourceMask) {
        this.messageSourceMask = messageSourceMask;
    }

    public DistributableObjectId getMessageTargetMask() {
        return messageTargetMask;
    }

    public void setMessageTargetMask(DistributableObjectId messageTargetMask) {
        this.messageTargetMask = messageTargetMask;
    }

    public LocalDateTime getMessagesAfterDate() {
        return messagesAfterDate;
    }

    public void setMessagesAfterDate(LocalDateTime messagesAfterDate) {
        this.messagesAfterDate = messagesAfterDate;
    }

    public LocalDateTime getMessagesBeforeDate() {
        return messagesBeforeDate;
    }

    public void setMessagesBeforeDate(LocalDateTime messagesBeforeDate) {
        this.messagesBeforeDate = messagesBeforeDate;
    }

    public String getMessageIdGreaterThan() {
        return messageIdGreaterThan;
    }

    public void setMessageIdGreaterThan(String messageIdGreaterThan) {
        this.messageIdGreaterThan = messageIdGreaterThan;
    }

    public String getMessageIdLessThan() {
        return messageIdLessThan;
    }

    public void setMessageIdLessThan(String messageIdLessThan) {
        this.messageIdLessThan = messageIdLessThan;
    }

    public Integer getMessageSequenceNumberGreaterThan() {
        return messageSequenceNumberGreaterThan;
    }

    public void setMessageSequenceNumberGreaterThan(Integer messageSequenceNumberGreaterThan) {
        this.messageSequenceNumberGreaterThan = messageSequenceNumberGreaterThan;
    }

    public Integer getMessageSequenceNumberLessThan() {
        return messageSequenceNumberLessThan;
    }

    public void setMessageSequenceNumberLessThan(Integer messageSequenceNumberLessThan) {
        this.messageSequenceNumberLessThan = messageSequenceNumberLessThan;
    }

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    //
    // Business Methods
    //
    /**
     * Filter a MessageObject against this subscription's criteria.
     * Supports wildcard '*' in source/target QualifiedName values, and in messageId bounds.
     * Null criteria are treated as "no restriction" for that field.
     */
    public boolean filterMessage(MessageObject message){
        getLogger().debug(".filterMessage(): Entry, message -> {}", message);
        if(message == null){
            return false;
        }
        // Source mask
        if(getMessageSourceMask() != null){
            if(message.getMessageSource() == null || !matchDistributableId(getMessageSourceMask(), message.getMessageSource())){
                getLogger().debug(".filterMessage(): Exit, message source does not match mask, returning false");
                return false;
            }
        }
        // Target mask
        if(getMessageTargetMask() != null){
            if(message.getMessageTarget() == null || !matchDistributableId(getMessageTargetMask(), message.getMessageTarget())){
                getLogger().debug(".filterMessage(): Exit, message target does not match mask, returning false");
                return false;
            }
        }
        // Date window (use send date if available else receive date)
        LocalDateTime msgTime = message.getMessageSendDate() != null ? message.getMessageSendDate() : message.getMessageReceiveDate();
        if(getMessagesAfterDate() != null){
            if(msgTime == null || !msgTime.isAfter(getMessagesAfterDate())){
                getLogger().debug(".filterMessage(): Exit, message time not after lower bound, returning false");
                return false;
            }
        }
        if(getMessagesBeforeDate() != null){
            if(msgTime == null || !msgTime.isBefore(getMessagesBeforeDate())){
                getLogger().debug(".filterMessage(): Exit, message time not before upper bound, returning false");
                return false;
            }
        }
        // Message ID bounds
        if(getMessageIdGreaterThan() != null){
            String pattern = getMessageIdGreaterThan();
            String value = message.getMessageId();
            if(pattern.contains("*")){
                if(!wildcardMatch(pattern, value)){
                    getLogger().debug(".filterMessage(): Exit, messageId does not match greaterThan wildcard pattern, returning false");
                    return false;
                }
            } else {
                if(value == null || !(value.compareTo(pattern) > 0)){
                    getLogger().debug(".filterMessage(): Exit, messageId not greater than lower bound, returning false");
                    return false;
                }
            }
        }
        if(getMessageIdLessThan() != null){
            String pattern = getMessageIdLessThan();
            String value = message.getMessageId();
            if(pattern.contains("*")){
                if(!wildcardMatch(pattern, value)){
                    getLogger().debug(".filterMessage(): Exit, messageId does not match lessThan wildcard pattern, returning false");
                    return false;
                }
            } else {
                if(value == null || !(value.compareTo(pattern) < 0)){
                    getLogger().debug(".filterMessage(): Exit, messageId not less than upper bound, returning false");
                    return false;
                }
            }
        }
        // Sequence number bounds (strictly greater/less than)
        if(getMessageSequenceNumberGreaterThan() != null){
            Integer minSeq = getMessageSequenceNumberGreaterThan();
            Integer seq = message.getMessageSequenceNumber();
            if(seq == null || !(seq > minSeq)){
                getLogger().debug(".filterMessage(): Exit, sequence number not greater than lower bound, returning false");
                return false;
            }
        }
        if(getMessageSequenceNumberLessThan() != null){
            Integer maxSeq = getMessageSequenceNumberLessThan();
            Integer seq = message.getMessageSequenceNumber();
            if(seq == null || !(seq < maxSeq)){
                getLogger().debug(".filterMessage(): Exit, sequence number not less than upper bound, returning false");
                return false;
            }
        }
        getLogger().debug(".filterMessage(): Exit, returning true");
        return true;
    }


}
