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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ComponentTaskProcessingStatistics implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private int tasksAssigned;
    private int tasksInitiated;
    private int tasksCompleted;
    private int tasksFailed;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant lastTaskCompletionInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant lastTaskStartInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private Instant lastTaskFailureInstant;

    private Object contentLock;

    //
    // Constructors
    //
    public ComponentTaskProcessingStatistics(){
        this.contentLock = new Object();
        this.tasksFailed = 0;
        this.tasksCompleted = 0;
        this.tasksAssigned = 0;
        this.tasksInitiated = 0;
        this.lastTaskCompletionInstant = Instant.MIN;
        this.lastTaskFailureInstant = Instant.MIN;
        this.lastTaskStartInstant = Instant.MIN;
    }

    //
     // Getters and Setters
    //

    protected Object getContentLock() {
        return(this.contentLock);
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

    public int getTasksFailed() {
        return tasksFailed;
    }

    public void setTasksFailed(int tasksFailed) {
        this.tasksFailed = tasksFailed;
    }

    public int getTasksAssigned() {
        return tasksAssigned;
    }

    public void setTasksAssigned(int tasksAssigned) {
        this.tasksAssigned = tasksAssigned;
    }

    public int getTasksInitiated() {
        return tasksInitiated;
    }

    public void setTasksInitiated(int tasksInitiated) {
        this.tasksInitiated = tasksInitiated;
    }

    public Instant getLastTaskCompletionInstant() {
        return lastTaskCompletionInstant;
    }

    public void setLastTaskCompletionInstant(Instant lastTaslCompletionInstant) {
        this.lastTaskCompletionInstant = lastTaslCompletionInstant;
    }

    public Instant getLastTaskStartInstant() {
        return lastTaskStartInstant;
    }

    public void setLastTaskStartInstant(Instant lastTaskStartInstant) {
        this.lastTaskStartInstant = lastTaskStartInstant;
    }

    public Instant getLastTaskFailureInstant() {
        return lastTaskFailureInstant;
    }

    public void setLastTaskFailureInstant(Instant lastTaskFailureInstant) {
        this.lastTaskFailureInstant = lastTaskFailureInstant;
    }

    //
     // Business Methods
    //
    @JsonIgnore
    public void incrementTaskFailureCount(){
        synchronized (getContentLock()){
            int count = getTasksFailed();
            count += 1;
            setTasksFailed(count);
        }
    }

    @JsonIgnore
    public void incrementTaskAssignedCount(){
        synchronized (getContentLock()) {
            int count = getTasksAssigned();
            count += 1;
            setTasksAssigned(count);
        }
    }

    @JsonIgnore
    public void incrementTaskInitiatedCount(){
        synchronized (getContentLock()) {
            int count = getTasksInitiated();
            count += 1;
            setTasksInitiated(count);
        }
    }

    @JsonIgnore
    public void incrementTaskCompletedCount(){
        synchronized (getContentLock()) {
            int count = getTasksCompleted();
            count += 1;
            setTasksCompleted(count);
        }
    }

    @JsonIgnore
    public void touchLastTaskStartInstant(){
        synchronized (getContentLock()) {
            setLastTaskStartInstant(Instant.now());
        }
    }

    @JsonIgnore
    public void touchLastTaskFailureInstant(){
        synchronized (getContentLock()){
            setLastTaskFailureInstant(Instant.now());
        }
    }

    @JsonIgnore
    public void touchLastTaskCompletionInstance(){
        synchronized (getContentLock()){
            setLastTaskCompletionInstant(Instant.now());
        }
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return "ComponentMessagingStatistics{" +
                "ingresMessageCount=" + getTasksAssigned() +
                ", egressMessageAttemptCount=" + getTasksInitiated() +
                ", egressMessageSuccessCount=" + getTasksCompleted() +
                ", egressMessageFailureCount=" + getTasksFailed() +
                ", lastTaskCompletionInstant=" + getLastTaskCompletionInstant() +
                ", lastTaskStartInstant=" + getLastTaskStartInstant() +
                ", lastTaskFailureInstant=" + getLastTaskFailureInstant() +
                ", contentLock=" + getContentLock() +
                '}';
    }
}
