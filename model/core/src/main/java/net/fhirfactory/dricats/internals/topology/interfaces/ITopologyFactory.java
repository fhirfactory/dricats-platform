package net.fhirfactory.dricats.internals.topology.interfaces;


import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessorGroup;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;


public interface ITopologyFactory {
    public ElementReference createNodeRDN(ElementReference parent, String nodeName, String nodeVersion, ApplicationComponentSpecialisationEnum nodeType);

    public WorkUnitProcessor createWorkUnitProcessor(ElementReference parent, String name, String version, String specialisation);

    public WorkUnitProcessorGroup createWorkUnitProcessor(ElementReference parent, String name, String version, WorkUnitProcessorGroup workshop, String specialisation);


    public void initialise();
}
