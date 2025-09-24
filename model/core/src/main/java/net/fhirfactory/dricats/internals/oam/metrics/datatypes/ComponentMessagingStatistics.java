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

public class ComponentMessagingStatistics implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private int ingresMessageCount;
    private int egressMessageAttemptCount;
    private int egressMessageSuccessCount;
    private int egressMessageFailureCount;
    private int internalDistributedMessageCount;
    private int internalReceivedMessageCount;
    private Map<String, Integer> internalDistributionCountMap;

    private Object contentLock;

    //
    // Constructors
    //
    public ComponentMessagingStatistics(){
        this.contentLock = new Object();
        this.internalReceivedMessageCount = 0;
        this.egressMessageFailureCount = 0;
        this.egressMessageSuccessCount = 0;
        this.ingresMessageCount = 0;
        this.egressMessageAttemptCount = 0;
        this.internalDistributedMessageCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
    }

    //
     // Getters and Setters
    //

    protected Object getContentLock() {
        return(this.contentLock);
    }

    public int getEgressMessageSuccessCount() {
        return egressMessageSuccessCount;
    }

    public void setEgressMessageSuccessCount(int egressMessageSuccessCount) {
        this.egressMessageSuccessCount = egressMessageSuccessCount;
    }

    public int getEgressMessageFailureCount() {
        return egressMessageFailureCount;
    }

    public void setEgressMessageFailureCount(int egressMessageFailureCount) {
        this.egressMessageFailureCount = egressMessageFailureCount;
    }

    public int getIngresMessageCount() {
        return ingresMessageCount;
    }

    public void setIngresMessageCount(int ingresMessageCount) {
        this.ingresMessageCount = ingresMessageCount;
    }

    public int getEgressMessageAttemptCount() {
        return egressMessageAttemptCount;
    }

    public void setEgressMessageAttemptCount(int egressMessageAttemptCount) {
        this.egressMessageAttemptCount = egressMessageAttemptCount;
    }

    public int getInternalDistributedMessageCount() {
        return internalDistributedMessageCount;
    }

    public void setInternalDistributedMessageCount(int internalDistributedMessageCount) {
        this.internalDistributedMessageCount = internalDistributedMessageCount;
    }

    public Map<String, Integer> getInternalDistributionCountMap() {
        return internalDistributionCountMap;
    }

    public void setInternalDistributionCountMap(Map<String, Integer> internalDistributionCountMap) {
        this.internalDistributionCountMap = internalDistributionCountMap;
    }

    public int getInternalReceivedMessageCount() {
        return internalReceivedMessageCount;
    }

    public void setInternalReceivedMessageCount(int internalReceivedMessageCount) {
        this.internalReceivedMessageCount = internalReceivedMessageCount;
    }

    //
     // Business Methods
    //
    @JsonIgnore
    public void incrementEgressMessageFailureCount(){
        synchronized (getContentLock()){
            int count = getEgressMessageFailureCount();
            count += 1;
            setEgressMessageFailureCount(count);
        }
    }

    @JsonIgnore
    public void incrementIngresMessageCount(){
        synchronized (getContentLock()) {
            int count = getIngresMessageCount();
            count += 1;
            setIngresMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressMessageAttemptCount(){
        synchronized (getContentLock()) {
            int count = getEgressMessageAttemptCount();
            count += 1;
            setEgressMessageAttemptCount(count);
        }
    }

    @JsonIgnore
    public void incrementEgressMessageSuccessCount(){
        synchronized (getContentLock()) {
            int count = getEgressMessageSuccessCount();
            count += 1;
            setEgressMessageSuccessCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalMessageDistributionCount(){
        synchronized (getContentLock()) {
            int count = getInternalDistributedMessageCount();
            count += 1;
            setInternalDistributedMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalReceivedMessageCount(){
        synchronized (getContentLock()){
            int count = getInternalReceivedMessageCount();
            count += 1;
            setInternalReceivedMessageCount(count);
        }
    }

    @JsonIgnore
    public void incrementInternalMessageDistributionCount(String targetParticipantName){
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
                "ingresMessageCount=" + getIngresMessageCount() +
                ", egressMessageAttemptCount=" + getEgressMessageAttemptCount() +
                ", egressMessageSuccessCount=" + getEgressMessageSuccessCount() +
                ", egressMessageFailureCount=" + getEgressMessageFailureCount() +
                ", internalDistributedMessageCount=" + getInternalDistributedMessageCount() +
                ", internalReceivedMessageCount=" + getInternalReceivedMessageCount() +
                ", internalDistributionCountMap=" + getInternalDistributionCountMap() +
                ", contentLock=" + getContentLock() +
                '}';
    }
}
