package net.fhirfactory.dricats.model.configuration.ports.external;

import java.io.Serial;

import org.slf4j.Logger;

import net.fhirfactory.dricats.model.configuration.connectedsystems.ConnectedSystemProperties;
import net.fhirfactory.dricats.model.configuration.ports.base.ServerInterfaceConfigurationObject;

public abstract class StandardExternalFacingInterface extends ServerInterfaceConfigurationObject {

    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900911L;
    
    protected abstract Logger specifyLogger();

    protected Logger getLogger(){
        return(specifyLogger());
    }

    private ConnectedSystemProperties connectedSystem;

    public StandardExternalFacingInterface(){
        super();
        this.connectedSystem = new ConnectedSystemProperties();
    }

    public ConnectedSystemProperties getConnectedSystem() {
        return connectedSystem;
    }

    public void setConnectedSystem(ConnectedSystemProperties connectedSystem) {
        this.connectedSystem = connectedSystem;
    }
}
