/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.oam.metrics.datatypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ComponentNotificationStatistics implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private int ingresNotificationCount;
    private int egressNotificationAttemptCount;
    private int egressNotificationSuccessCount;
    private int egressNotificationFailureCount;
    private int internalDistributedNotificationCount;
    private int internalReceivedNotificationCount;
    private Map<String, Integer> internalDistributionCountMap;

    private Object contentLock;

    //
    // Constructors
    //
    public ComponentNotificationStatistics(){
        this.contentLock = new Object();
        this.internalReceivedNotificationCount = 0;
        this.egressNotificationFailureCount = 0;
        this.egressNotificationSuccessCount = 0;
        this.ingresNotificationCount = 0;
        this.egressNotificationAttemptCount = 0;
        this.internalDistributedNotificationCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
    }

    //
     // Getters and Setters
    //

    protected Object getContentLock() {
        return(this.contentLock);
    }

    public int getEgressNotificationSuccessCount() {
        return egressNotificationSuccessCount;
    }

    public void setEgressNotificationSuccessCount(int egressNotificationSuccessCount) {
        this.egressNotificationSuccessCount = egressNotificationSuccessCount;
    }

    public int getEgressNotificationFailureCount() {
        return egressNotificationFailureCount;
    }

    public void setEgressNotificationFailureCount(int egressNotificationFailureCount) {
        this.egressNotificationFailureCount = egressNotificationFailureCount;
    }

    public int getIngresNotificationCount() {
        return ingresNotificationCount;
    }

    public void setIngresNotificationCount(int ingresNotificationCount) {
        this.ingresNotificationCount = ingresNotificationCount;
    }

    public int getEgressNotificationAttemptCount() {
        return egressNotificationAttemptCount;
    }

    public void setEgressNotificationAttemptCount(int egressNotificationAttemptCount) {
        this.egressNotificationAttemptCount = egressNotificationAttemptCount;
    }

    public int getInternalDistributedNotificationCount() {
        return internalDistributedNotificationCount;
    }

    public void setInternalDistributedNotificationCount(int internalDistributedNotificationCount) {
        this.internalDistributedNotificationCount = internalDistributedNotificationCount;
    }

    public Map<String, Integer> getInternalDistributionCountMap() {
        return internalDistributionCountMap;
    }

    public void setInternalDistributionCountMap(Map<String, Integer> internalDistributionCountMap) {
        this.internalDistributionCountMap = internalDistributionCountMap;
    }

    public int getInternalReceivedNotificationCount() {
        return internalReceivedNotificationCount;
    }

    public void setInternalReceivedNotificationCount(int internalReceivedNotificationCount) {
        this.internalReceivedNotificationCount = internalReceivedNotificationCount;
    }

    //
     // Business Methods
    //
    @JsonIgnore
    public void incrementEgressNotificationFailureCount(){
        synchronized (getContentLock()){
            int count = getEgressNotificationFailureCount();
            count += 1;
            setEgressNotificationFailureCount(count);
        }
    }

    @JsonIgnore
    public void incrementIngresNotificationCount(){
        synchronized (getContentLock()) {
            int count = getIngresNotificationCount();
            count += 1;
            setIngresNotificationCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressNotificationAttemptCount(){
        synchronized (getContentLock()) {
            int count = getEgressNotificationAttemptCount();
            count += 1;
            setEgressNotificationAttemptCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressNotificationSuccessCount(){
        synchronized (getContentLock()) {
            int count = getEgressNotificationSuccessCount();
            count += 1;
            setEgressNotificationSuccessCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalNotificationDistributionCount(){
        synchronized (getContentLock()) {
            int count = getInternalDistributedNotificationCount();
            count += 1;
            setInternalDistributedNotificationCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalReceivedNotificationCount(){
        synchronized (getContentLock()){
            int count = getInternalReceivedNotificationCount();
            count += 1;
            setInternalReceivedNotificationCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalNotificationDistributionCount(String targetParticipantName){
        if(StringUtils.isNotEmpty(targetParticipantName)){
            synchronized (getContentLock()){
                if(!getInternalDistributionCountMap().containsKey(targetParticipantName)){
                    getInternalDistributionCountMap().put(targetParticipantName, 0);
                }
                Integer count = getInternalDistributionCountMap().get(targetParticipantName);
                Integer newValue = count + 1;
                getInternalDistributionCountMap().replace(targetParticipantName, newValue);
            }
        }
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return "ComponentMessagingStatistics{" +
                "ingresNotificationCount=" + getIngresNotificationCount() +
                ", egressNotificationAttemptCount=" + getEgressNotificationAttemptCount() +
                ", egressNotificationSuccessCount=" + getEgressNotificationSuccessCount() +
                ", egressNotificationFailureCount=" + getEgressNotificationFailureCount() +
                ", internalDistributedNotificationCount=" + getInternalDistributedNotificationCount() +
                ", internalReceivedNotificationCount=" + getInternalReceivedNotificationCount() +
                ", internalDistributionCountMap=" + getInternalDistributionCountMap() +
                ", contentLock=" + getContentLock() +
                '}';
    }
}
