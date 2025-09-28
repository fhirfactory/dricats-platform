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
package net.fhirfactory.dricats.model.configuration.configurationfile.archetypes;

import net.fhirfactory.dricats.internals.configuration.segments.DebuggingSupportConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.JavaDeploymentConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.LoadBalancerConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.SecurityCredentialConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.VolumeMountConfigurationObject;

public abstract class ContainerBasedSubsystemConfigurationObject extends BaseSubsystemConfigurationObject {

    private Integer defaultServicePortLowerBound;
    private LoadBalancerConfigurationObject loadBalancer;
    private VolumeMountConfigurationObject volumeMounts;
    private DebuggingSupportConfigurationObject debugProperties;
    private SecurityCredentialConfigurationObject hapiAPIKey;
    private JavaDeploymentConfigurationObject javaDeploymentParameters;

    public ContainerBasedSubsystemConfigurationObject(){
        super();
        loadBalancer = new LoadBalancerConfigurationObject();
        volumeMounts = new VolumeMountConfigurationObject();
        debugProperties = new DebuggingSupportConfigurationObject();
        hapiAPIKey = new SecurityCredentialConfigurationObject();
        javaDeploymentParameters = new JavaDeploymentConfigurationObject();
        defaultServicePortLowerBound = null;
    }

    boolean hasDefaultServicePortLowerBound(){
        boolean has = defaultServicePortLowerBound != null;
        return(has);
    }

    public Integer getDefaultServicePortLowerBound() {
        return defaultServicePortLowerBound;
    }

    public void setDefaultServicePortLowerBound(Integer defaultServicePortLowerBound) {
        this.defaultServicePortLowerBound = defaultServicePortLowerBound;
    }

    public LoadBalancerConfigurationObject getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerConfigurationObject loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public VolumeMountConfigurationObject getVolumeMounts() {
        return volumeMounts;
    }

    public void setVolumeMounts(VolumeMountConfigurationObject volumeMounts) {
        this.volumeMounts = volumeMounts;
    }

    public DebuggingSupportConfigurationObject getDebugProperties() {
        return debugProperties;
    }

    public void setDebugProperties(DebuggingSupportConfigurationObject debugProperties) {
        this.debugProperties = debugProperties;
    }

    public SecurityCredentialConfigurationObject getHapiAPIKey() {
        return hapiAPIKey;
    }

    public void setHapiAPIKey(SecurityCredentialConfigurationObject hapiAPIKey) {
        this.hapiAPIKey = hapiAPIKey;
    }

    public JavaDeploymentConfigurationObject getJavaDeploymentParameters() {
        return javaDeploymentParameters;
    }

    public void setJavaDeploymentParameters(JavaDeploymentConfigurationObject javaDeploymentParameters) {
        this.javaDeploymentParameters = javaDeploymentParameters;
    }

    @Override
    public String toString() {
        return "ContainerBasedSubsystemConfigurationObject{" +
                "defaultServicePortLowerBound=" + defaultServicePortLowerBound +
                ", loadBalancer=" + loadBalancer +
                ", volumeMounts=" + volumeMounts +
                ", debugProperties=" + debugProperties +
                ", hapiAPIKey=" + hapiAPIKey +
                ", javaDeploymentParameters=" + javaDeploymentParameters +
                ", deploymentMode=" + getDeploymentMode() +
                ", deploymentSites=" + getDeploymentSites() +
                '}';
    }
}
