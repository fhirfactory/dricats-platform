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
package net.fhirfactory.dricats.internals.tasking.datatypes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;

public class InternalTaskProvenance implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900094L;
    private static final Logger LOG = LoggerFactory.getLogger(InternalTaskProvenance.class);

    //
    // Attributes
    //

    private Map<Integer, ElementReference> taskHistory;
    private Map<Integer, ElementReference> softwareComponentHistory;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime taskOriginDate;

    //
    // Constructor(s)
    //

    public InternalTaskProvenance() {
        this.taskHistory = new HashMap<>();
        this.softwareComponentHistory = new HashMap<>();
    }

    //
    // Bean Methods
    //

    public Map<Integer, ElementReference> getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(Map<Integer, ElementReference> taskHistory) {
        this.taskHistory = taskHistory;
    }

    public Map<Integer, ElementReference> getSoftwareComponentHistory() {
        return softwareComponentHistory;
    }

    public void setSoftwareComponentHistory(Map<Integer, ElementReference> softwareComponentHistory) {
        this.softwareComponentHistory = softwareComponentHistory;
    }

    public LocalDateTime getTaskOriginDate() {
        return taskOriginDate;
    }

    public void setTaskOriginDate(LocalDateTime taskOriginDate) {
        this.taskOriginDate = taskOriginDate;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
