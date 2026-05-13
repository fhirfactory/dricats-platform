package net.fhirfactory.dricats.subsystem;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class ApplicationInstance extends Subsystem {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInstance.class);

    //
    // Attributes
    //

    //
    // Constructor
    //

    public ApplicationInstance(){

    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }

    //
    // Postconstruct (Initialisation)
    //
    @PostConstruct
    protected void initialize(){

    }

    //
    // Core Subsystem Methods
    //

    @Override
    protected ElementIdentifier specifySubsystemIdentifier() {
        return null;
    }
}
