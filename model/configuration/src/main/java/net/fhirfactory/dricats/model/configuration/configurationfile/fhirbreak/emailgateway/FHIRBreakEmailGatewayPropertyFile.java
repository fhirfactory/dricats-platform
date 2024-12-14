package net.fhirfactory.dricats.model.configuration.configurationfile.fhirbreak.emailgateway;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact.ClusteredInteractServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.base.StandardServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.fhirbreak.common.FHIRBreakSubsystemPropertyFile;

public class FHIRBreakEmailGatewayPropertyFile extends FHIRBreakSubsystemPropertyFile {

    private ClusteredInteractServerPortSegment smtpGateway;
    private ClusteredInteractServerPortSegment imapGateway;

    private StandardServerPortSegment edgeReceiveCommunication;

    public ClusteredInteractServerPortSegment getSmtpGateway() {
        return smtpGateway;
    }

    public void setSmtpGateway(ClusteredInteractServerPortSegment smtpGateway) {
        this.smtpGateway = smtpGateway;
    }

    public ClusteredInteractServerPortSegment getImapGateway() {
        return imapGateway;
    }

    public void setImapGateway(ClusteredInteractServerPortSegment imapGateway) {
        this.imapGateway = imapGateway;
    }

    public StandardServerPortSegment getEdgeReceiveCommunication() {
        return edgeReceiveCommunication;
    }

    public void setEdgeReceiveCommunication(StandardServerPortSegment edgeReceiveCommunication) {
        this.edgeReceiveCommunication = edgeReceiveCommunication;
    }
}
