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
package net.fhirfactory.dricats.internals.oam.topology;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ApplicationComponentStatus implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 123412341234L;

    //
    // Attributes
    //
    private String componentStatus;
    private String componentStatusDescription;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime startupInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime heartbeatInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime lastActivityInstant;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime lastHeartbeatInstant;

    //
    // Constructors
    //
    public ApplicationComponentStatus() {
        super();
        this.startupInstant = LocalDateTime.now();
        this.heartbeatInstant = LocalDateTime.now();
        this.lastActivityInstant = LocalDateTime.now();
        this.lastHeartbeatInstant = LocalDateTime.now();
    }

    //
    // Bean Methods
    //

    public String getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(String componentStatus) {
        this.componentStatus = componentStatus;
    }

    public String getComponentStatusDescription() {
        return componentStatusDescription;
    }

    public void setComponentStatusDescription(String componentStatusDescription) {
        this.componentStatusDescription = componentStatusDescription;
    }

    public LocalDateTime getStartupInstant() {
        return startupInstant;
    }

    public void setStartupInstant(LocalDateTime startupInstant) {
        this.startupInstant = startupInstant;
    }

    public LocalDateTime getHeartbeatInstant() {
        return heartbeatInstant;
    }

    public void setHeartbeatInstant(LocalDateTime heartbeatInstant) {
        this.heartbeatInstant = heartbeatInstant;
    }

    public LocalDateTime getLastActivityInstant() {
        return lastActivityInstant;
    }

    public void setLastActivityInstant(LocalDateTime lastActivityInstant) {
        this.lastActivityInstant = lastActivityInstant;
    }

    public LocalDateTime getLastHeartbeatInstant() {
        return lastHeartbeatInstant;
    }

    public void setLastHeartbeatInstant(LocalDateTime lastHeartbeatInstant) {
        this.lastHeartbeatInstant = lastHeartbeatInstant;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return "ApplicationComponentStatusSummary{" +
                "componentStatus='" + getComponentStatus() + '\'' +
                ", componentStatusDescription='" + getComponentStatusDescription() + '\'' +
                ", startupInstant=" + getStartupInstant() +
                ", heartbeatInstant=" + getHeartbeatInstant() +
                ", lastActivityInstant=" + getLastActivityInstant() +
                ", lastHeartbeatInstant=" + getLastHeartbeatInstant() +
                '}';
    }
}
