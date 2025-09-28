/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments;

import net.fhirfactory.dricats.internals.configuration.valuesets.ApplicationDeploymentModeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ApplicationInstanceConfigurationObject implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678903391L;

    //
    // Attributes
    //
    private String deploymentConfigFilename;
    private String otherDeploymentFlags;
    private String applicationInstanceId;
    private ApplicationDeploymentModeEnum deploymentMode;

    //
     // Constructor(s)
    //

    public ApplicationInstanceConfigurationObject(){
        this.deploymentMode = ApplicationDeploymentModeEnum.STANDALONE;
    }

    /* an update */

    public String getDeploymentConfigFilename() {
        return deploymentConfigFilename;
    }

    public void setDeploymentConfigFilename(String deploymentConfigFilename) {
        this.deploymentConfigFilename = deploymentConfigFilename;
    }

    public String getOtherDeploymentFlags() {
        return otherDeploymentFlags;
    }

    public void setOtherDeploymentFlags(String otherDeploymentFlags) {
        this.otherDeploymentFlags = otherDeploymentFlags;
    }

    public String getApplicationInstanceId() {
        return applicationInstanceId;
    }

    public void setApplicationInstanceId(String applicationInstanceId) {
        this.applicationInstanceId = applicationInstanceId;
    }

    public ApplicationDeploymentModeEnum getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(ApplicationDeploymentModeEnum deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    //
     // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("deploymentConfigFilename", deploymentConfigFilename)
                .append("otherDeploymentFlags", otherDeploymentFlags)
                .append("applicationInstanceId", applicationInstanceId)
                .append("deploymentMode", deploymentMode)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationInstanceConfigurationObject that = (ApplicationInstanceConfigurationObject) o;
        return Objects.equals(deploymentConfigFilename, that.deploymentConfigFilename) && Objects.equals(applicationInstanceId, that.applicationInstanceId) && deploymentMode == that.deploymentMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deploymentConfigFilename, applicationInstanceId, deploymentMode);
    }
}
