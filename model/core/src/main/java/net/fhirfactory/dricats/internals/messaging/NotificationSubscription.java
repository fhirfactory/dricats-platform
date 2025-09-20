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

public class NotificationSubscription extends SubscriptionSupport implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678200051L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSubscription.class);

    //
    // Attributes
    //
    private DistributableObjectId notificationSourceMask;
    private DistributableObjectId notificationTaskMask;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime notificationsAfterDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime notificationsBeforeDate;
    private String notificationIdGreaterThan;
    private String notificationIdLessThan;
    private Integer notificationSequenceNumberGreaterThan;
    private Integer notificationSequenceNumberLessThan;

    //
    // Constructors
    //
    public NotificationSubscription() {
        super();
    }

    public NotificationSubscription(DistributableObjectId subscriber) {
        super(subscriber);
    }

    //
    // Getters and Setters
    //
    public DistributableObjectId getNotificationSourceMask() {
        return notificationSourceMask;
    }

    public void setNotificationSourceMask(DistributableObjectId notificationSourceMask) {
        this.notificationSourceMask = notificationSourceMask;
    }

    public DistributableObjectId getNotificationTaskMask() {
        return notificationTaskMask;
    }

    public void setNotificationTaskMask(DistributableObjectId notificationTaskMask) {
        this.notificationTaskMask = notificationTaskMask;
    }

    public LocalDateTime getNotificationsAfterDate() {
        return notificationsAfterDate;
    }

    public void setNotificationsAfterDate(LocalDateTime notificationsAfterDate) {
        this.notificationsAfterDate = notificationsAfterDate;
    }

    public LocalDateTime getNotificationsBeforeDate() {
        return notificationsBeforeDate;
    }

    public void setNotificationsBeforeDate(LocalDateTime notificationsBeforeDate) {
        this.notificationsBeforeDate = notificationsBeforeDate;
    }

    public String getNotificationIdGreaterThan() {
        return notificationIdGreaterThan;
    }

    public void setNotificationIdGreaterThan(String notificationIdGreaterThan) {
        this.notificationIdGreaterThan = notificationIdGreaterThan;
    }

    public String getNotificationIdLessThan() {
        return notificationIdLessThan;
    }

    public void setNotificationIdLessThan(String notificationIdLessThan) {
        this.notificationIdLessThan = notificationIdLessThan;
    }

    public Integer getNotificationSequenceNumberGreaterThan() {
        return notificationSequenceNumberGreaterThan;
    }

    public void setNotificationSequenceNumberGreaterThan(Integer notificationSequenceNumberGreaterThan) {
        this.notificationSequenceNumberGreaterThan = notificationSequenceNumberGreaterThan;
    }

    public Integer getNotificationSequenceNumberLessThan() {
        return notificationSequenceNumberLessThan;
    }

    public void setNotificationSequenceNumberLessThan(Integer notificationSequenceNumberLessThan) {
        this.notificationSequenceNumberLessThan = notificationSequenceNumberLessThan;
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
    public boolean filterNotification(MessageObject message){
        getLogger().debug(".filterNotification(): Entry, message -> {}", message);
        if(message == null){
            return false;
        }
        // Source mask
        if(getNotificationSourceMask() != null){
            if(message.getMessageSource() == null || !matchDistributableId(getNotificationSourceMask(), message.getMessageSource())){
                getLogger().debug(".filterNotification(): Exit, message source does not match mask, returning false");
                return false;
            }
        }
        // Target mask
        if(getNotificationTaskMask() != null){
            if(message.getMessageTarget() == null || !matchDistributableId(getNotificationTaskMask(), message.getMessageTarget())){
                getLogger().debug(".filterNotification(): Exit, message target does not match mask, returning false");
                return false;
            }
        }
        // Date window (use send date if available else receive date)
        LocalDateTime msgTime = message.getMessageSendDate() != null ? message.getMessageSendDate() : message.getMessageReceiveDate();
        if(getNotificationsAfterDate() != null){
            if(msgTime == null || !msgTime.isAfter(getNotificationsAfterDate())){
                getLogger().debug(".filterNotification(): Exit, message time not after lower bound, returning false");
                return false;
            }
        }
        if(getNotificationsBeforeDate() != null){
            if(msgTime == null || !msgTime.isBefore(getNotificationsBeforeDate())){
                getLogger().debug(".filterNotification(): Exit, message time not before upper bound, returning false");
                return false;
            }
        }
        // Message ID bounds
        if(getNotificationIdGreaterThan() != null){
            String pattern = getNotificationIdGreaterThan();
            String value = message.getMessageId();
            if(pattern.contains("*")){
                if(!wildcardMatch(pattern, value)){
                    getLogger().debug(".filterNotification(): Exit, messageId does not match greaterThan wildcard pattern, returning false");
                    return false;
                }
            } else {
                if(value == null || !(value.compareTo(pattern) > 0)){
                    getLogger().debug(".filterNotification(): Exit, messageId not greater than lower bound, returning false");
                    return false;
                }
            }
        }
        if(getNotificationIdLessThan() != null){
            String pattern = getNotificationIdLessThan();
            String value = message.getMessageId();
            if(pattern.contains("*")){
                if(!wildcardMatch(pattern, value)){
                    getLogger().debug(".filterNotification(): Exit, messageId does not match lessThan wildcard pattern, returning false");
                    return false;
                }
            } else {
                if(value == null || !(value.compareTo(pattern) < 0)){
                    getLogger().debug(".filterNotification(): Exit, messageId not less than upper bound, returning false");
                    return false;
                }
            }
        }
        // Sequence number bounds (strictly greater/less than)
        if(getNotificationSequenceNumberGreaterThan() != null){
            Integer minSeq = getNotificationSequenceNumberGreaterThan();
            Integer seq = message.getMessageSequenceNumber();
            if(seq == null || !(seq > minSeq)){
                getLogger().debug(".filterNotification(): Exit, sequence number not greater than lower bound, returning false");
                return false;
            }
        }
        if(getNotificationSequenceNumberLessThan() != null){
            Integer maxSeq = getNotificationSequenceNumberLessThan();
            Integer seq = message.getMessageSequenceNumber();
            if(seq == null || !(seq < maxSeq)){
                getLogger().debug(".filterNotification(): Exit, sequence number not less than upper bound, returning false");
                return false;
            }
        }
        getLogger().debug(".filterNotification(): Exit, returning true");
        return true;
    }


}
