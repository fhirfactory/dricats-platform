/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.model.configuration.configurationfile.common.archetypes;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.ipc.HTTPIPCClientPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.ipc.HTTPIPCServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.ipc.JGroupsIPCServerPortSegment;

public class PetasosEnabledSubsystemPropertyFile extends ClusterServiceDeliverySubsystemPropertyFile{
    private JGroupsIPCServerPortSegment interZoneIPC;
    private JGroupsIPCServerPortSegment intraZoneIPC;
    private JGroupsIPCServerPortSegment interZoneOAM;
    private JGroupsIPCServerPortSegment intraZoneOAM;
    private JGroupsIPCServerPortSegment interZoneAudit;
    private JGroupsIPCServerPortSegment intraZoneAudit;
    private JGroupsIPCServerPortSegment interZoneInterception;
    private JGroupsIPCServerPortSegment intraZoneInterception;
    private JGroupsIPCServerPortSegment interZoneTasks;
    private JGroupsIPCServerPortSegment intraZoneTasks;
    private JGroupsIPCServerPortSegment multiZoneInfinispan;
    private JGroupsIPCServerPortSegment interZoneMetrics;
    private JGroupsIPCServerPortSegment intraZoneMetrics;
    private HTTPIPCServerPortSegment edgeAnswer;
    private HTTPIPCClientPortSegment edgeAsk;

    //
    // Constructor(s)
    //

    public PetasosEnabledSubsystemPropertyFile(){
        super();
        edgeAnswer = new HTTPIPCServerPortSegment();
        edgeAsk = new HTTPIPCClientPortSegment();
        interZoneIPC = new JGroupsIPCServerPortSegment();
        intraZoneIPC = new JGroupsIPCServerPortSegment();
        interZoneOAM = new JGroupsIPCServerPortSegment();
        intraZoneOAM = new JGroupsIPCServerPortSegment();
        interZoneAudit = new JGroupsIPCServerPortSegment();
        intraZoneAudit= new JGroupsIPCServerPortSegment();
        interZoneInterception = new JGroupsIPCServerPortSegment();
        intraZoneInterception = new JGroupsIPCServerPortSegment();
        interZoneTasks = new JGroupsIPCServerPortSegment();
        intraZoneTasks = new JGroupsIPCServerPortSegment();
        multiZoneInfinispan = new JGroupsIPCServerPortSegment();
        interZoneMetrics = new JGroupsIPCServerPortSegment();
        intraZoneMetrics = new JGroupsIPCServerPortSegment();
    }

    //
    // Getters and Setters
    //


    public HTTPIPCClientPortSegment getEdgeAsk() {
        return edgeAsk;
    }

    public void setEdgeAsk(HTTPIPCClientPortSegment edgeAsk) {
        this.edgeAsk = edgeAsk;
    }

    public JGroupsIPCServerPortSegment getInterZoneAudit() {
        return interZoneAudit;
    }

    public void setInterZoneAudit(JGroupsIPCServerPortSegment interZoneAudit) {
        this.interZoneAudit = interZoneAudit;
    }

    public JGroupsIPCServerPortSegment getIntraZoneAudit() {
        return intraZoneAudit;
    }

    public void setIntraZoneAudit(JGroupsIPCServerPortSegment intraZoneAudit) {
        this.intraZoneAudit = intraZoneAudit;
    }

    public JGroupsIPCServerPortSegment getInterZoneInterception() {
        return interZoneInterception;
    }

    public void setInterZoneInterception(JGroupsIPCServerPortSegment interZoneInterception) {
        this.interZoneInterception = interZoneInterception;
    }

    public JGroupsIPCServerPortSegment getIntraZoneInterception() {
        return intraZoneInterception;
    }

    public void setIntraZoneInterception(JGroupsIPCServerPortSegment intraZoneInterception) {
        this.intraZoneInterception = intraZoneInterception;
    }

    public JGroupsIPCServerPortSegment getInterZoneTasks() {
        return interZoneTasks;
    }

    public void setInterZoneTasks(JGroupsIPCServerPortSegment interZoneTasks) {
        this.interZoneTasks = interZoneTasks;
    }

    public JGroupsIPCServerPortSegment getIntraZoneTasks() {
        return intraZoneTasks;
    }

    public void setIntraZoneTasks(JGroupsIPCServerPortSegment intraZoneTasks) {
        this.intraZoneTasks = intraZoneTasks;
    }

    public JGroupsIPCServerPortSegment getMultiZoneInfinispan() {
        return multiZoneInfinispan;
    }

    public void setMultiZoneInfinispan(JGroupsIPCServerPortSegment multiZoneInfinispan) {
        this.multiZoneInfinispan = multiZoneInfinispan;
    }

    public JGroupsIPCServerPortSegment getInterZoneMetrics() {
        return interZoneMetrics;
    }

    public void setInterZoneMetrics(JGroupsIPCServerPortSegment interZoneMetrics) {
        this.interZoneMetrics = interZoneMetrics;
    }

    public JGroupsIPCServerPortSegment getIntraZoneMetrics() {
        return intraZoneMetrics;
    }

    public void setIntraZoneMetrics(JGroupsIPCServerPortSegment intraZoneMetrics) {
        this.intraZoneMetrics = intraZoneMetrics;
    }

    public JGroupsIPCServerPortSegment getInterZoneIPC() {
        return interZoneIPC;
    }

    public void setInterZoneIPC(JGroupsIPCServerPortSegment interZoneIPC) {
        this.interZoneIPC = interZoneIPC;
    }

    public HTTPIPCServerPortSegment getEdgeAnswer() {
        return edgeAnswer;
    }

    public void setEdgeAnswer(HTTPIPCServerPortSegment edgeAnswer) {
        this.edgeAnswer = edgeAnswer;
    }

    public JGroupsIPCServerPortSegment getIntraZoneIPC() {
        return intraZoneIPC;
    }

    public void setIntraZoneIPC(JGroupsIPCServerPortSegment intraZoneIPC) {
        this.intraZoneIPC = intraZoneIPC;
    }

    public JGroupsIPCServerPortSegment getInterZoneOAM() {
        return interZoneOAM;
    }

    public void setInterZoneOAM(JGroupsIPCServerPortSegment interZoneOAM) {
        this.interZoneOAM = interZoneOAM;
    }

    public JGroupsIPCServerPortSegment getIntraZoneOAM() {
        return intraZoneOAM;
    }

    public void setIntraZoneOAM(JGroupsIPCServerPortSegment intraZoneOAM) {
        this.intraZoneOAM = intraZoneOAM;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "PetasosEnabledSubsystemPropertyFile{" +
                "kubeReadinessProbe=" + getKubeReadinessProbe() +
                ", kubeLivelinessProbe=" + getKubeLivelinessProbe() +
                ", prometheusPort=" + getPrometheusPort() +
                ", jolokiaPort=" + getJolokiaPort() +
                ", subsystemInstant=" + getSubsystemInstant() +
                ", deploymentMode=" + getDeploymentMode() +
                ", deploymentSites=" + getDeploymentSites() +
                ", subsystemImageProperties=" + getSubsystemImageProperties() +
                ", trustStorePassword=" + getTrustStorePassword() +
                ", keyPassword=" + getKeyPassword() +
                ", deploymentZone=" + getDeploymentZone() +
                ", defaultServicePortLowerBound=" + getDefaultServicePortLowerBound() +
                ", loadBalancer=" + getLoadBalancer() +
                ", volumeMounts=" + getVolumeMounts() +
                ", debugProperties=" + getDebugProperties() +
                ", hapiAPIKey=" + getHapiAPIKey() +
                ", javaDeploymentParameters=" + getJavaDeploymentParameters() +
                ", interZoneIPC=" + interZoneIPC +
                ", intraZoneIPC=" + intraZoneIPC +
                ", interZoneOAM=" + interZoneOAM +
                ", intraZoneOAM=" + intraZoneOAM +
                ", interZoneAudit=" + interZoneAudit +
                ", intraZoneAudit=" + intraZoneAudit +
                ", interZoneInterception=" + interZoneInterception +
                ", intraZoneInterception=" + intraZoneInterception +
                ", interZoneTasks=" + interZoneTasks +
                ", intraZoneTasks=" + intraZoneTasks +
                ", multiZoneInfinispan=" + multiZoneInfinispan +
                ", interZoneMetrics=" + interZoneMetrics +
                ", intraZoneMetrics=" + intraZoneMetrics +
                ", edgeAnswer=" + edgeAnswer +
                ", edgeAsk=" + edgeAsk +
                '}';
    }
}
