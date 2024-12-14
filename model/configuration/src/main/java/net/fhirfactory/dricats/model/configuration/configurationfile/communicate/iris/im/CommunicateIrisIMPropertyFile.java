package net.fhirfactory.dricats.model.configuration.configurationfile.communicate.iris.im;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.SecurityCredentialSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact.ClusteredInteractHTTPServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact.StandardInteractHTTPClientPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.communicate.common.CommunicateSubsystemPropertyFile;

public class CommunicateIrisIMPropertyFile extends CommunicateSubsystemPropertyFile {

    private StandardInteractHTTPClientPortSegment interactMatrixClientServicesClient;
    private ClusteredInteractHTTPServerPortSegment interactMatrixApplicationServicesServer;
    private ClusteredInteractHTTPServerPortSegment interactPegacornVoIPServicesServer;
    private SecurityCredentialSegment wildflyUser;

    //
    // Constructor(s)
    //

    public CommunicateIrisIMPropertyFile(){
        super();
        this.wildflyUser = new SecurityCredentialSegment();
        this.interactMatrixClientServicesClient = new StandardInteractHTTPClientPortSegment();
        this.interactMatrixApplicationServicesServer = new ClusteredInteractHTTPServerPortSegment();
        this.interactPegacornVoIPServicesServer = new ClusteredInteractHTTPServerPortSegment();
    }

    //
    // Getters and Setters
    //

    public StandardInteractHTTPClientPortSegment getInteractMatrixClientServicesClient() {
        return interactMatrixClientServicesClient;
    }

    public void setInteractMatrixClientServicesClient(StandardInteractHTTPClientPortSegment interactMatrixClientServicesClient) {
        this.interactMatrixClientServicesClient = interactMatrixClientServicesClient;
    }

    public ClusteredInteractHTTPServerPortSegment getInteractMatrixApplicationServicesServer() {
        return interactMatrixApplicationServicesServer;
    }

    public void setInteractMatrixApplicationServicesServer(ClusteredInteractHTTPServerPortSegment interactMatrixApplicationServicesServer) {
        this.interactMatrixApplicationServicesServer = interactMatrixApplicationServicesServer;
    }

    public ClusteredInteractHTTPServerPortSegment getInteractPegacornVoIPServicesServer() {
        return interactPegacornVoIPServicesServer;
    }

    public void setInteractPegacornVoIPServicesServer(ClusteredInteractHTTPServerPortSegment interactPegacornVoIPServicesServer) {
        this.interactPegacornVoIPServicesServer = interactPegacornVoIPServicesServer;
    }

    public SecurityCredentialSegment getWildflyUser() {
        return wildflyUser;
    }

    public void setWildflyUser(SecurityCredentialSegment wildflyUser) {
        this.wildflyUser = wildflyUser;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "CommunicateIrisIMPropertyFile{" +
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
                ", interZoneIPC=" + getInterZoneIPC() +
                ", edgeAnswer=" + getEdgeAnswer() +
                ", intraZoneIPC=" + getIntraZoneIPC() +
                ", interZoneOAM=" + getInterZoneOAM() +
                ", intraZoneOAM=" + getIntraZoneOAM() +
                ", interactMatrixClientServicesClient=" + interactMatrixClientServicesClient +
                ", interactMatrixApplicationServicesServer=" + interactMatrixApplicationServicesServer +
                ", interactPegacornVoIPServicesServer=" + interactPegacornVoIPServicesServer +
                ", wildflyUser=" + wildflyUser +
                '}';
    }
}
