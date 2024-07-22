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

import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationObject implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900057L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationObject.class);

    //
    // Attributes
    //

    private IntegrationEndpoint notificationSource;
    private IntegrationEndpoint notificationTarget;
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


    //
    // Bean Methods
    //

    public IntegrationEndpoint getNotificationSource() {
        return notificationSource;
    }

    public void setNotificationSource(IntegrationEndpoint notificationSource) {
        this.notificationSource = notificationSource;
    }

    public IntegrationEndpoint getNotificationTarget() {
        return notificationTarget;
    }

    public void setNotificationTarget(IntegrationEndpoint notificationTarget) {
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
}
