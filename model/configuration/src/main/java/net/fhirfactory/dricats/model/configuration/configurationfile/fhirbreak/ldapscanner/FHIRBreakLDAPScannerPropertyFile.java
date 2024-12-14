package net.fhirfactory.dricats.model.configuration.configurationfile.fhirbreak.ldapscanner;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact.ClusteredInteractServerPortSegment;
import net.fhirfactory.dricats.model.configuration.configurationfile.fhirbreak.common.FHIRBreakSubsystemPropertyFile;

public class FHIRBreakLDAPScannerPropertyFile extends FHIRBreakSubsystemPropertyFile {

    private ClusteredInteractServerPortSegment ldapProxy;
    private ClusteredInteractServerPortSegment ldapServer;

    public ClusteredInteractServerPortSegment getLdapProxy() {
        return ldapProxy;
    }

    public void setLdapProxy(ClusteredInteractServerPortSegment ldapProxy) {
        this.ldapProxy = ldapProxy;
    }

    public ClusteredInteractServerPortSegment getLdapServer() {
        return ldapServer;
    }

    public void setLdapServer(ClusteredInteractServerPortSegment ldapServer) {
        this.ldapServer = ldapServer;
    }

}
