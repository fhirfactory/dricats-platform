package net.fhirfactory.dricats.platform.configuration.factories.archetypes.interfaces;


import net.fhirfactory.dricats.model.configuration.configurationfile.SolutionConfigurationObject;

public interface SolutionNodeFactoryInterface {
    public void initialise();
    public SolutionConfigurationObject getSolutionTopologyNode();
}
