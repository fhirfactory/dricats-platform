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
package net.fhirfactory.dricats.model.configuration.configurationfile.base;

import net.fhirfactory.dricats.internals.configuration.segments.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;


public class BaseSubsystemConfigurationObject implements Serializable {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
     // Attributes
    //
    private SolutionConfigurationObject solution;
    private ApplicationConfigurationObject application;
    private ApplicationInstanceConfigurationObject applicationInstance;
    private SiteConfigurationObject deploymentMode;
    private MultiSiteConfigurationObject deploymentSites;
    private DeploymentZoneConfigurationObject deploymentZone;
    private ApplicationInstanceImageConfigurationObject subsystemImageProperties;

    //
     // Constructors
    //
    public BaseSubsystemConfigurationObject() {
        applicationInstance = new ApplicationInstanceConfigurationObject();
        deploymentMode = new SiteConfigurationObject();
        deploymentSites = new MultiSiteConfigurationObject();
        subsystemImageProperties = new ApplicationInstanceImageConfigurationObject();
        deploymentZone = new DeploymentZoneConfigurationObject();
    }

    //
     // Bean Methods
    //

    public SolutionConfigurationObject getSolution() {
        return solution;
    }

    public void setSolution(SolutionConfigurationObject solution) {
        this.solution = solution;
    }

    public ApplicationConfigurationObject getApplication() {
        return application;
    }

    public void setApplication(ApplicationConfigurationObject application) {
        this.application = application;
    }

    public ApplicationInstanceConfigurationObject getApplicationInstance() {
        return applicationInstance;
    }

    public void setApplicationInstance(ApplicationInstanceConfigurationObject applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    public SiteConfigurationObject getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(SiteConfigurationObject deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public MultiSiteConfigurationObject getDeploymentSites() {
        return deploymentSites;
    }

    public void setDeploymentSites(MultiSiteConfigurationObject deploymentSites) {
        this.deploymentSites = deploymentSites;
    }

    public DeploymentZoneConfigurationObject getDeploymentZone() {
        return deploymentZone;
    }

    public void setDeploymentZone(DeploymentZoneConfigurationObject deploymentZone) {
        this.deploymentZone = deploymentZone;
    }

    public ApplicationInstanceImageConfigurationObject getSubsystemImageProperties() {
        return subsystemImageProperties;
    }

    public void setSubsystemImageProperties(ApplicationInstanceImageConfigurationObject subsystemImageProperties) {
        this.subsystemImageProperties = subsystemImageProperties;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("solution", solution)
                .append("application", application)
                .append("applicationInstance", applicationInstance)
                .append("deploymentMode", deploymentMode)
                .append("deploymentSites", deploymentSites)
                .append("deploymentZone", deploymentZone)
                .append("subsystemImageProperties", subsystemImageProperties)
                .toString();
    }
}
