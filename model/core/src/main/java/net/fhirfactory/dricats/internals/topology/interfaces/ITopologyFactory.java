package net.fhirfactory.dricats.internals.topology.interfaces;


import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessorGroup;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;


public interface ITopologyFactory {
    public ObjectId createNodeRDN(String nodeName, String nodeVersion, ApplicationComponentSpecialisationEnum nodeType);

    public WorkUnitProcessor createWorkUnitProcessor(String name, String version, Subsystem processingPlant, String specialisation);

    public WorkUnitProcessor createWorkUnitProcessor(String name, String version, WorkUnitProcessorGroup workshop, String specialisation);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public void initialise();
}
