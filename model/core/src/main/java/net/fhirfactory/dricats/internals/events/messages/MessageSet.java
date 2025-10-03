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
package net.fhirfactory.dricats.internals.events.messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MessageSet implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345768900051L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageSet.class);

    //
    // Attributes
    //
    Map<Integer, MessageObject> messageSequence;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime messageSetCreationDate;

    //
    // Constructor(s)
    //
    public MessageSet(){
        this.messageSequence = new HashMap<>();
        this.messageSetCreationDate = LocalDateTime.now();
    }

    //
    // Getters and Setters
    //

    public Map<Integer, MessageObject> getMessageSequence() {
        return messageSequence;
    }

    public void setMessageSequence(Map<Integer, MessageObject> messageSequence) {
        this.messageSequence = messageSequence;
    }

    public LocalDateTime getMessageSetCreationDate() {
        return messageSetCreationDate;
    }

    public void setMessageSetCreationDate(LocalDateTime messageSetCreationDate) {
        this.messageSetCreationDate = messageSetCreationDate;
    }

    public void addMessage(MessageObject message){
        getMessageSequence().put(getMessageSequence().size(), message);
    }

    //
    // Utility
    //

    protected Logger getLogger() {
    	return(LOG);
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder();
        toStringBuilder.append("NotificationSet{");
        toStringBuilder.append("notificationSequence=[");
        for(int counter = 0; counter < getMessageSequence().size(); counter += 1){
            toStringBuilder.append(counter);
            toStringBuilder.append("=");
            toStringBuilder.append(getMessageSequence().get(counter).toString());
            if(counter < getMessageSequence().size() -1){
                toStringBuilder.append(",");
            }
        }
        toStringBuilder.append("]");
        toStringBuilder.append(", notificationSetCreationDate=");
        toStringBuilder.append(getMessageSetCreationDate());
        toStringBuilder.append("}");
        return toStringBuilder.toString();
    }
}
