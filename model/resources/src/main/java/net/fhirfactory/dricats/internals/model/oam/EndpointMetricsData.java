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
package net.fhirfactory.dricats.internals.model.oam;

import java.io.Serial;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectId;
import net.fhirfactory.dricats.internals.model.oam.common.CommonComponentMetricsData;

public class EndpointMetricsData extends CommonComponentMetricsData{
	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 6062599081116012689L;
	private static final Logger LOG = LoggerFactory.getLogger(EndpointMetricsData.class);

	//
	// Attributes
	//
	private Integer nodeIngresQueueSize;
    private Integer egressSendAttemptCount;
    private Integer remoteProcedureCallCount;
    private Integer remoteProcedureCallHandledCount;
    private Integer remoteProcedureCallFailureCount;
    private Map<String, Integer> remoteProcedureCallRequestsMap;
    private Map<String, Integer> remoteProcedureCallFailuresMap;
    private Map<String, Integer> remoteProcedureCallResponsesMap;

    //
    // Constructor(s)
    //

    public EndpointMetricsData(){
        super();
        this.nodeIngresQueueSize = 0;
        this.egressSendAttemptCount = 0;
        this.remoteProcedureCallCount = 0;
        this.remoteProcedureCallHandledCount = 0;
        this.remoteProcedureCallFailureCount = 0;
        this.remoteProcedureCallRequestsMap = new ConcurrentHashMap<>();
        this.remoteProcedureCallFailuresMap = new ConcurrentHashMap<>();
        this.remoteProcedureCallResponsesMap = new ConcurrentHashMap<>();
    }

    public EndpointMetricsData(DistributableObjectId componentId){
        super(componentId);
        this.nodeIngresQueueSize = 0;
        this.egressSendAttemptCount = 0;
        this.remoteProcedureCallCount = 0;
        this.remoteProcedureCallHandledCount = 0;
        this.remoteProcedureCallFailureCount = 0;
        this.remoteProcedureCallRequestsMap = new ConcurrentHashMap<>();
        this.remoteProcedureCallFailuresMap = new ConcurrentHashMap<>();
        this.remoteProcedureCallResponsesMap = new ConcurrentHashMap<>();
    }

    //
    // Getters and Setters
    //

    public Integer getRemoteProcedureCallFailureCount() {
        return remoteProcedureCallFailureCount;
    }

    public void setRemoteProcedureCallFailureCount(Integer remoteProcedureCallFailureCount) {
        this.remoteProcedureCallFailureCount = remoteProcedureCallFailureCount;
    }

    public Integer getEgressSendAttemptCount() {
        return egressSendAttemptCount;
    }

    public void setEgressSendAttemptCount(Integer egressSendAttemptCount) {
        this.egressSendAttemptCount = egressSendAttemptCount;
    }

    public Integer getRemoteProcedureCallCount() {
        return remoteProcedureCallCount;
    }

    public void setRemoteProcedureCallCount(Integer remoteProcedureCallCount) {
        this.remoteProcedureCallCount = remoteProcedureCallCount;
    }

    public Integer getRemoteProcedureCallHandledCount() {
        return remoteProcedureCallHandledCount;
    }

    public void setRemoteProcedureCallHandledCount(Integer remoteProcedureCallHandledCount) {
        this.remoteProcedureCallHandledCount = remoteProcedureCallHandledCount;
    }

    public Integer getNodeIngresQueueSize() {
        return nodeIngresQueueSize;
    }

    public void setNodeIngresQueueSize(Integer nodeIngresQueueSize) {
        this.nodeIngresQueueSize = nodeIngresQueueSize;
    }

    public Map<String, Integer> getRemoteProcedureCallRequestsMap() {
        return remoteProcedureCallRequestsMap;
    }

    public void setRemoteProcedureCallRequestsMap(Map<String, Integer> remoteProcedureCallRequestsMap) {
        this.remoteProcedureCallRequestsMap = remoteProcedureCallRequestsMap;
    }

    public Map<String, Integer> getRemoteProcedureCallFailuresMap() {
        return remoteProcedureCallFailuresMap;
    }

    public void setRemoteProcedureCallFailuresMap(Map<String, Integer> remoteProcedureCallFailuresMap) {
        this.remoteProcedureCallFailuresMap = remoteProcedureCallFailuresMap;
    }

    public Map<String, Integer> getRemoteProcedureCallResponsesMap() {
        return remoteProcedureCallResponsesMap;
    }

    public void setRemoteProcedureCallResponsesMap(Map<String, Integer> remoteProcedureCallResponsesMap) {
        this.remoteProcedureCallResponsesMap = remoteProcedureCallResponsesMap;
    }
    
    //
    // Business Methods
    //
    
    @JsonIgnore
    public void incrementRemoteProcedureCallCount(){
        getLogger().debug(".incrementRemoteProcedureCallCount(): Entry");
        synchronized (getContentLock()){
            int count = getRemoteProcedureCallCount();
            count += 1;
            setRemoteProcedureCallCount(count);
        }
        getLogger().debug(".incrementRemoteProcedureCallCount(): Exit");
    }

    @JsonIgnore
    public void incrementRemoteProcedureCallFailureCount(){
        getLogger().debug(".incrementRemoteProcedureCallFailureCount(): Entry");
        synchronized (getContentLock()){
            int count = getRemoteProcedureCallFailureCount();
            count += 1;
            setRemoteProcedureCallCount(count);
        }
        getLogger().debug(".incrementRemoteProcedureCallFailureCount(): Exit");
    }

    @JsonIgnore
    public void incrementRemoteProcedureCallHandledCount(){
        getLogger().debug(".incrementRemoteProcedureCallHandledCount(): Entry");
        synchronized (getContentLock()){
            int count = getRemoteProcedureCallCount();
            count += 1;
            setRemoteProcedureCallCount(count);
        }
        getLogger().debug(".incrementRemoteProcedureCallHandledCount(): Exit");
    }

    @JsonIgnore
    public void incrementEgressSendAttemptCount(){
        synchronized (getContentLock()){
            int count = getEgressSendAttemptCount();
            count += 1;
            setEgressSendAttemptCount(count);
        }
    }

    @JsonIgnore
    public void incrementRPCRequestCount(String targetParticipantName){
        if(StringUtils.isNotEmpty(targetParticipantName)){
            synchronized (getContentLock()){
                if(!getRemoteProcedureCallRequestsMap().containsKey(targetParticipantName)){
                    getRemoteProcedureCallRequestsMap().put(targetParticipantName, 0);
                }
                Integer count = getRemoteProcedureCallRequestsMap().get(targetParticipantName);
                Integer newValue = count + 1;
                getRemoteProcedureCallRequestsMap().replace(targetParticipantName, newValue);
            }
        }
    }

    @JsonIgnore
    public void incrementRPCResponseCount(String targetParticipantName){
        if(StringUtils.isNotEmpty(targetParticipantName)){
            synchronized (getContentLock()){
                if(!getRemoteProcedureCallResponsesMap().containsKey(targetParticipantName)){
                    getRemoteProcedureCallResponsesMap().put(targetParticipantName, 0);
                }
                Integer count = getRemoteProcedureCallResponsesMap().get(targetParticipantName);
                Integer newValue = count + 1;
                getRemoteProcedureCallResponsesMap().replace(targetParticipantName, newValue);
            }
        }
    }

    @JsonIgnore
    public void incrementRPCFailureCount(String targetParticipantName){
        if(StringUtils.isNotEmpty(targetParticipantName)){
            synchronized (getContentLock()){
                if(!getRemoteProcedureCallFailuresMap().containsKey(targetParticipantName)){
                    getRemoteProcedureCallFailuresMap().put(targetParticipantName, 0);
                }
                Integer count = getRemoteProcedureCallFailuresMap().get(targetParticipantName);
                Integer newValue = count + 1;
                getRemoteProcedureCallFailuresMap().replace(targetParticipantName, newValue);
            }
        }
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "EndpointMetricsData{" +
                "nodeIngresQueueSize=" + nodeIngresQueueSize +
                ", egressSendAttemptCount=" + egressSendAttemptCount +
                ", remoteProcedureCallCount=" + remoteProcedureCallCount +
                ", remoteProcedureCallHandledCount=" + remoteProcedureCallHandledCount +
                ", remoteProcedureCallFailureCount=" + remoteProcedureCallFailureCount +
                ", remoteProcedureCallRequestsMap=" + remoteProcedureCallRequestsMap +
                ", remoteProcedureCallFailuresMap=" + remoteProcedureCallFailuresMap +
                ", remoteProcedureCallResponsesMap=" + remoteProcedureCallResponsesMap +
                ", ingresMessageCount=" + getIngresMessageCount() +
                ", egressMessageCount=" + getEgressMessageAttemptCount() +
                ", internalDistributedMessageCount=" + getInternalDistributedMessageCount() +
                ", internalDistributionCountMap=" + getInternalDistributionCountMap() +
                ", componentID=" + getComponentID() +
                ", componentType=" + getComponentType() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", componentStartupInstant=" + getComponentStartupInstant() +
                ", componentStatus='" + getComponentStatus() + '\'' +
                ", participantName='" + getParticipantName() + '\'' +
                ", internalReceivedMessageCount=" + getInternalReceivedMessageCount() +
                '}';
    }
    
    @Override
    protected Logger getLogger() {
    	return(LOG);
    }
}
