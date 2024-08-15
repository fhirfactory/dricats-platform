/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.model.oam.common;

import java.io.Serial;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectId;
import net.fhirfactory.dricats.internals.model.base.dataytypes.SerialisableObject;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;

public class CommonComponentMetricsData extends SerialisableObject{
	//
	// Housekeeping
	//
	
	@Serial
	private static final long serialVersionUID = 1137576853554630827L;
	private static final Logger LOG = LoggerFactory.getLogger(CommonComponentMetricsData.class);
	
	//
	// Attributes
	//
	
	private DistributableObjectId componentID;
    private String participantName;
    private SoftwareComponentType componentType;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant lastActivityInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant componentStartupInstant;
    private String componentStatus;

    private int ingresMessageCount;
    private int egressMessageAttemptCount;
    private int egressMessageSuccessCount;
    private int egressMessageFailureCount;
    private int internalDistributedMessageCount;
    private int internalReceivedMessageCount;
    private Map<String, Integer> internalDistributionCountMap;
    
    private Object contentLock;

    //
    // Constructor(s)
    //

    public CommonComponentMetricsData(){
        this.ingresMessageCount = 0;
        this.egressMessageAttemptCount = 0;
        this.internalDistributedMessageCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
        this.componentID = null;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.internalReceivedMessageCount = 0;
        this.egressMessageFailureCount = 0;
        this.egressMessageSuccessCount = 0;
        this.contentLock = new Object();
    }

    public CommonComponentMetricsData(DistributableObjectId componentId){
        this.ingresMessageCount = 0;
        this.egressMessageAttemptCount = 0;
        this.internalDistributedMessageCount = 0;
        this.internalDistributionCountMap = new HashMap<>();
        this.componentID = componentId;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.internalReceivedMessageCount = 0;
        this.egressMessageFailureCount = 0;
        this.egressMessageSuccessCount = 0;
        this.contentLock = new Object();
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

    public DistributableObjectId getComponentID() {
        return componentID;
    }

    public void setComponentID(DistributableObjectId componentID) {
        this.componentID = componentID;
    }

    public SoftwareComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(SoftwareComponentType componentType) {
        this.componentType = componentType;
    }

    public Instant getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(Instant lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    public Instant getComponentStartupInstant() {
        return componentStartupInstant;
    }

    public void setComponentStartupInstant(Instant componentStartupInstant) {
        this.componentStartupInstant = componentStartupInstant;
    }

    public String getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(String componentStatus) {
        this.componentStatus = componentStatus;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
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
    public void touchLastActivityInstant(){
        setLastActivityInstant(Instant.now());
    }
    
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
    // Utility Methods
    //

    @Override
    public String toString() {
        return "CommonComponentMetricsData{" +
                "componentID=" + componentID +
                ", participantName='" + participantName + '\'' +
                ", componentType=" + componentType +
                ", lastActivityInstant=" + lastActivityInstant +
                ", componentStartupInstant=" + componentStartupInstant +
                ", componentStatus='" + componentStatus + '\'' +
                ", ingresMessageCount=" + ingresMessageCount +
                ", egressMessageAttemptCount=" + egressMessageAttemptCount +
                ", egressMessageSuccessCount=" + egressMessageSuccessCount +
                ", egressMessageFailureCount=" + egressMessageFailureCount +
                ", internalDistributedMessageCount=" + internalDistributedMessageCount +
                ", internalReceivedMessageCount=" + internalReceivedMessageCount +
                ", internalDistributionCountMap=" + internalDistributionCountMap +
                '}';
    }
    
    protected Logger getLogger() {
    	return(LOG);
    }
}
