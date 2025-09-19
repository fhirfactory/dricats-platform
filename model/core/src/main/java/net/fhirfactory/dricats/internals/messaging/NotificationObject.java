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

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.internals.common.SerialisableObject;

public class NotificationObject extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900057L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationObject.class);

    //
    // Attributes
    //

    private DistributableObjectId notificationSource;
    private DistributableObjectId notificationTarget;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime notificationSendDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime notificationReceiveDate;
    private String notificationId;
    private Integer notificationSequenceNumber;
    private NotificationPayload notificationPayload;

    //
    // Constructor(s)
    //

    public NotificationObject(){
        super();
        setNotificationReceiveDate(LocalDateTime.now());
        setNotificationSource(new DistributableObjectId());
        setNotificationTarget(new DistributableObjectId());
        setNotificationPayload(new NotificationPayload());
        setNotificationSendDate(LocalDateTime.MIN);
        setNotificationSequenceNumber(-1);
        setNotificationId(UUID.randomUUID().toString());
        setId(new CommonName("Notification("+getNotificationId()+")"));
    }

    public NotificationObject(DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, NotificationPayload payload){
        super();
        setNotificationReceiveDate(LocalDateTime.now());
        setNotificationSendDate(sendDate);
        setNotificationSource(source);
        setNotificationTarget(target);
        setNotificationPayload(payload);
        setNotificationSequenceNumber(0);
        setNotificationId(UUID.randomUUID().toString());
        setId(new CommonName("Notification("+getNotificationId()+")"));
    }

    public  NotificationObject(String notificationId, DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, NotificationPayload payload, int sequenceNumber ){
        this(source, target, sendDate, payload);
        setNotificationId(notificationId);
        setNotificationSequenceNumber(sequenceNumber);
        setId(new CommonName("Notification("+getNotificationId()+")"));
    }

    //
    // Bean Methods
    //

    public DistributableObjectId getNotificationSource() {
        return notificationSource;
    }

    public void setNotificationSource(DistributableObjectId notificationSource) {
        this.notificationSource = notificationSource;
    }

    public DistributableObjectId getNotificationTarget() {
        return notificationTarget;
    }

    public void setNotificationTarget(DistributableObjectId notificationTarget) {
        this.notificationTarget = notificationTarget;
    }

    public LocalDateTime getNotificationSendDate() {
        return notificationSendDate;
    }

    public void setNotificationSendDate(LocalDateTime notificationSendDate) {
        this.notificationSendDate = notificationSendDate;
    }

    public LocalDateTime getNotificationReceiveDate() {
        return notificationReceiveDate;
    }

    public void setNotificationReceiveDate(LocalDateTime notificationReceiveDate) {
        this.notificationReceiveDate = notificationReceiveDate;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getNotificationSequenceNumber() {
        return notificationSequenceNumber;
    }

    public void setNotificationSequenceNumber(Integer notificationSequenceNumber) {
        this.notificationSequenceNumber = notificationSequenceNumber;
    }

    public NotificationPayload getNotificationPayload() {
        return notificationPayload;
    }

    public void setNotificationPayload(NotificationPayload notificationPayload) {
        this.notificationPayload = notificationPayload;
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
                .append("notificationSource", notificationSource)
                .append("notificationTarget", notificationTarget)
                .append("notificationSendDate", notificationSendDate)
                .append("notificationReceiveDate", notificationReceiveDate)
                .append("notificationId", notificationId)
                .append("notificationSequenceNumber", notificationSequenceNumber)
                .append("notificationPayload", notificationPayload)
                .toString();
    }
}
