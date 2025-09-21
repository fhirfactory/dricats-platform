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
package net.fhirfactory.dricats.internals.events.notifications;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.events.common.EventBase;
import net.fhirfactory.dricats.internals.events.messages.MessagePayload;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

public class NotificationObject extends EventBase {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900057L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationObject.class);

    //
    // Attributes
    //

    private NotificationPayload notificationPayload;

    //
    // Constructor(s)
    //

    public NotificationObject(){
        super();
        UnqualifiedName unqualifiedName = new UnqualifiedName("Notification", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
        setNotificationPayload(new NotificationPayload());
    }

    public NotificationObject(DistributableObjectId source, DistributableObjectId target, LocalDateTime sendDate, NotificationPayload payload){
        super();
        setEventReceiveDate(LocalDateTime.now());
        setEventSendDate(sendDate);
        setSource(source);
        setTarget(target);
        setNotificationPayload(payload);
        UnqualifiedName unqualifiedName = new UnqualifiedName("Notification", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
    }

    //
    // Bean Methods
    //

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
                .append("notificationPayload", notificationPayload)
                .appendSuper(super.toString())
                .toString();
    }
}
