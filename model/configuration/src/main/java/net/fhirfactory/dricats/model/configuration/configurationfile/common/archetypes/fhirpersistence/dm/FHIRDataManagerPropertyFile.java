package net.fhirfactory.dricats.model.configuration.configurationfile.common.archetypes.fhirpersistence.dm;


import net.fhirfactory.dricats.model.configuration.configurationfile.common.archetypes.BaseSubsystemPropertyFile;
import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.standard.HTTPClusteredServiceServerPortSegment;

public abstract class FHIRDataManagerPropertyFile extends BaseSubsystemPropertyFile {
    HTTPClusteredServiceServerPortSegment fhirJPAServerPort;

    public HTTPClusteredServiceServerPortSegment getFHIRJPAServerPort() {
        return fhirJPAServerPort;
    }

    public void getFHIRJPAServerPort(HTTPClusteredServiceServerPortSegment edgeAnswer) {
        this.fhirJPAServerPort = edgeAnswer;
    }
}
