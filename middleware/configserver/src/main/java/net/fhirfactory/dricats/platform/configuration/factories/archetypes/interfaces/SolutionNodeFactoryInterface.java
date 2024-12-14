package net.fhirfactory.dricats.platform.configuration.factories.archetypes.interfaces;

import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;

public interface SolutionNodeFactoryInterface {
    public void initialise();
    public SolutionTopologyNode getSolutionTopologyNode();
}
