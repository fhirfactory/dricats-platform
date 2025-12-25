/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.oam.metrics.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.common.object.SerialisableObject;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentNotificationStatistics;
import net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentTaskProcessingStatistics;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.Instant;
import java.util.Objects;

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
	
	private ObjectId componentID;
    private String participantName;
    private ApplicationComponentSpecialisationEnum componentType;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant lastActivityInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant componentStartupInstant;
    private String componentStatus;

    private ComponentMessagingStatistics messagingStatistics;
    private ComponentNotificationStatistics notificationStatistics;
    private ComponentTaskProcessingStatistics taskProcessingStatistics;
    
    private Object contentLock;

    //
    // Constructor(s)
    //

    public CommonComponentMetricsData(){

        this.componentID = null;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.contentLock = new Object();
        this.messagingStatistics = new ComponentMessagingStatistics();
    }

    public CommonComponentMetricsData(ObjectId componentId){
        this.componentID = componentId;
        this.componentType = null;
        this.lastActivityInstant = null;
        this.componentStartupInstant = null;
        this.componentStatus = null;
        this.participantName = null;
        this.messagingStatistics = new ComponentMessagingStatistics();
        this.contentLock = new Object();
    }

    //
    // Getters and Setters
    //

    protected Object getContentLock() {
    	return(this.contentLock);
    }



    public ObjectId getComponentID() {
        return componentID;
    }

    public void setComponentID(ObjectId componentID) {
        this.componentID = componentID;
    }

    public ApplicationComponentSpecialisationEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(ApplicationComponentSpecialisationEnum componentType) {
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

    public ComponentMessagingStatistics getMessagingStatistics() {
        return messagingStatistics;
    }

    public void setMessagingStatistics(ComponentMessagingStatistics messagingStatistics) {
        this.messagingStatistics = messagingStatistics;
    }

    public ComponentNotificationStatistics getNotificationStatistics() {
        return notificationStatistics;
    }

    public void setNotificationStatistics(ComponentNotificationStatistics notificationStatistics) {
        this.notificationStatistics = notificationStatistics;
    }

    public ComponentTaskProcessingStatistics getTaskProcessingStatistics() {
        return taskProcessingStatistics;
    }

    public void setTaskProcessingStatistics(ComponentTaskProcessingStatistics taskProcessingStatistics) {
        this.taskProcessingStatistics = taskProcessingStatistics;
    }

//
    // Business Methods
    //
    
    @JsonIgnore
    public void touchLastActivityInstant(){
        setLastActivityInstant(Instant.now());
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
                ", messagingStatistics =" + messagingStatistics +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommonComponentMetricsData that = (CommonComponentMetricsData) o;
        return Objects.equals(getComponentID(), that.getComponentID()) && Objects.equals(getParticipantName(), that.getParticipantName()) && getComponentType() == that.getComponentType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentID(), getParticipantName(), getComponentType());
    }

    protected Logger getLogger() {
    	return(LOG);
    }
}
