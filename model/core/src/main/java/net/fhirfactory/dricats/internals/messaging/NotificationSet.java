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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class NotificationSet implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12354768900051L;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSet.class);

    //
    // Attributes
    //
    Map<Integer, NotificationObject> notificationSequence;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime notificationSetCreationDate;

    //
    // Constructor(s)
    //
    public NotificationSet(){
        this.notificationSequence = new HashMap<>();
        this.notificationSetCreationDate = LocalDateTime.now();
    }

    //
    // Getters and Setters
    //

    public Map<Integer, NotificationObject> getNotificationSequence() {
        return notificationSequence;
    }

    public void setNotificationSequence(Map<Integer, NotificationObject> notificationSequence) {
        this.notificationSequence = notificationSequence;
    }

    public LocalDateTime getNotificationSetCreationDate() {
        return notificationSetCreationDate;
    }

    public void setNotificationSetCreationDate(LocalDateTime messageSetCreationDate) {
        this.notificationSetCreationDate = messageSetCreationDate;
    }

    public void addNotification(NotificationObject notification){
        getNotificationSequence().put(getNotificationSequence().size(), notification);
    }
    
    //
    // Utilities
    //
    
    protected Logger getLogger() {
    	return(LOG);
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append("NotificationSet{");
        toStringBuilder.append("notificationSequence=[");
        for(int counter = 0; counter < notificationSequence.size(); counter += 1){
            toStringBuilder.append(counter+"="+getNotificationSequence().get(counter).toString());
            if(counter < notificationSequence.size() -1){
                toStringBuilder.append(",");
            }
        }
        toStringBuilder.append("]");
        toStringBuilder.append(", notificationSetCreationDate=" + notificationSetCreationDate + "}");
        return toStringBuilder.toString();
    }
}
