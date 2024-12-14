package net.fhirfactory.dricats.model.configuration.configurationfile.mitaf.smsgateway;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.base.StandardServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact.StandardInteractServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.mitaf.MITaFSubsystemPropertyFile;

public class MITaFSMSGatewayPropertyFile extends MITaFSubsystemPropertyFile {

    private StandardInteractServerPortSegment smsGateway;

    private StandardServerPortSegment edgeReceiveCommunication;

    public StandardInteractServerPortSegment getSmsGateway() {
        return smsGateway;
    }

    public void setSmsGateway(StandardInteractServerPortSegment smsGateway) {
        this.smsGateway = smsGateway;
    }

    public StandardServerPortSegment getEdgeReceiveCommunication() {
        return edgeReceiveCommunication;
    }

    public void setEdgeReceiveCommunication(StandardServerPortSegment edgeReceiveCommunication) {
        this.edgeReceiveCommunication = edgeReceiveCommunication;
    }
}
