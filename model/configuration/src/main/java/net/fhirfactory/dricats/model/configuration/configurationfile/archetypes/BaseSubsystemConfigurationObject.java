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

import net.fhirfactory.dricats.internals.configuration.ApplicationInstanceConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.ports.base.ServerInterfaceConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.DeploymentModeConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.DeploymentSiteConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.DeploymentZoneConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.SecurityCredentialConfigurationObject;
import net.fhirfactory.dricats.internals.configuration.segments.SubsystemImageConfigurationObject;


public class BaseSubsystemConfigurationObject {

    private ApplicationInstanceConfigurationObject subsystemInstant;
    private DeploymentModeConfigurationObject deploymentMode;
    private DeploymentSiteConfigurationObject deploymentSites;
    private DeploymentZoneConfigurationObject deploymentZone;
    private ServerInterfaceConfigurationObject kubeReadinessProbe;
    private ServerInterfaceConfigurationObject kubeLivelinessProbe;
    private ServerInterfaceConfigurationObject prometheusPort;
    private ServerInterfaceConfigurationObject jolokiaPort;

    private SubsystemImageConfigurationObject subsystemImageProperties;
    private SecurityCredentialConfigurationObject trustStorePassword;
    private SecurityCredentialConfigurationObject keyPassword;

    public BaseSubsystemConfigurationObject() {
        subsystemInstant = new ApplicationInstanceConfigurationObject();
        deploymentMode = new DeploymentModeConfigurationObject();
        deploymentSites = new DeploymentSiteConfigurationObject();
        kubeLivelinessProbe = new ServerInterfaceConfigurationObject();
        kubeReadinessProbe = new ServerInterfaceConfigurationObject();
        subsystemImageProperties = new SubsystemImageConfigurationObject();
        trustStorePassword = new SecurityCredentialConfigurationObject();
        keyPassword = new SecurityCredentialConfigurationObject();
        jolokiaPort = new ServerInterfaceConfigurationObject();
        prometheusPort = new ServerInterfaceConfigurationObject();
        deploymentZone = new DeploymentZoneConfigurationObject();
    }

    public ServerInterfaceConfigurationObject getKubeReadinessProbe() {
        return kubeReadinessProbe;
    }

    public void setKubeReadinessProbe(ServerInterfaceConfigurationObject kubeReadinessProbe) {
        this.kubeReadinessProbe = kubeReadinessProbe;
    }

    public ServerInterfaceConfigurationObject getKubeLivelinessProbe() {
        return kubeLivelinessProbe;
    }

    public void setKubeLivelinessProbe(ServerInterfaceConfigurationObject kubeLivelinessProbe) {
        this.kubeLivelinessProbe = kubeLivelinessProbe;
    }

    public ServerInterfaceConfigurationObject getPrometheusPort() {
        return prometheusPort;
    }

    public void setPrometheusPort(ServerInterfaceConfigurationObject prometheusPort) {
        this.prometheusPort = prometheusPort;
    }

    public ServerInterfaceConfigurationObject getJolokiaPort() {
        return jolokiaPort;
    }

    public void setJolokiaPort(ServerInterfaceConfigurationObject jolokiaPort) {
        this.jolokiaPort = jolokiaPort;
    }

    public ApplicationInstanceConfigurationObject getSubsystemInstant() {
        return subsystemInstant;
    }

    public void setSubsystemInstant(ApplicationInstanceConfigurationObject subsystemInstant) {
        this.subsystemInstant = subsystemInstant;
    }

    public DeploymentModeConfigurationObject getDeploymentMode() {
        return deploymentMode;
    }

    public void setDeploymentMode(DeploymentModeConfigurationObject deploymentMode) {
        this.deploymentMode = deploymentMode;
    }

    public DeploymentSiteConfigurationObject getDeploymentSites() {
        return deploymentSites;
    }

    public void setDeploymentSites(DeploymentSiteConfigurationObject deploymentSites) {
        this.deploymentSites = deploymentSites;
    }


    public SubsystemImageConfigurationObject getSubsystemImageProperties() {
        return subsystemImageProperties;
    }

    public void setSubsystemImageProperties(SubsystemImageConfigurationObject subsystemImageProperties) {
        this.subsystemImageProperties = subsystemImageProperties;
    }

    public SecurityCredentialConfigurationObject getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(SecurityCredentialConfigurationObject trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public SecurityCredentialConfigurationObject getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(SecurityCredentialConfigurationObject keyPassword) {
        this.keyPassword = keyPassword;
    }

    public DeploymentZoneConfigurationObject getDeploymentZone() {
        return deploymentZone;
    }

    public void setDeploymentZone(DeploymentZoneConfigurationObject deploymentZone) {
        this.deploymentZone = deploymentZone;
    }

    @Override
    public String toString() {
        return "BaseSubsystemPropertyFile{" +
                "subsystemInstant=" + subsystemInstant +
                ", deploymentMode=" + deploymentMode +
                ", deploymentSites=" + deploymentSites +
                ", kubeReadinessProbe=" + kubeReadinessProbe +
                ", kubeLivelinessProbe=" + kubeLivelinessProbe +
                ", prometheusPort=" + prometheusPort +
                ", jolokiaPort=" + jolokiaPort +
                ", subsystemImageProperties=" + subsystemImageProperties +
                ", trustStorePassword=" + trustStorePassword +
                ", keyPassword=" + keyPassword +
                ", deploymentZone=" + deploymentZone +
                "," + super.toString() +
                '}';
    }
}
