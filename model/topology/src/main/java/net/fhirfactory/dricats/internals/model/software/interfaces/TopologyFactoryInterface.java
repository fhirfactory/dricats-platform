package net.fhirfactory.dricats.internals.model.software.interfaces;

import net.fhirfactory.dricats.internals.model.software.ProcessingPlant;
import net.fhirfactory.dricats.internals.model.software.Workshop;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentId;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ConcurrencyModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.mode.ResilienceModeEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;

public interface TopologyFactoryInterface {
    public SoftwareComponentId createNodeRDN(String nodeName, String nodeVersion, SoftwareComponentType nodeType);

    public Workshop createWorkshop(String name, String version, ProcessingPlant processingPlant, SoftwareComponentType nodeType);

    public SoftwareComponent createWorkUnitProcessor(String name, String version, WorkshopSoftwareComponent workshop, ComponentTypeTypeEnum nodeType);

    public WorkUnitProcessorComponentTopologyNode createWorkUnitProcessorComponent(String name, ComponentTypeTypeEnum topologyType, WorkUnitProcessorSoftwareComponent wup);

    public WorkUnitProcessorInterchangeComponentTopologyNode createWorkUnitProcessingInterchangeComponent(String name, ComponentTypeTypeEnum topologyNodeType, WorkUnitProcessorSoftwareComponent wup);

    public Boolean getSubsystemInternalTrafficEncrypt();

    public ResilienceModeEnum getResilienceMode();
    public ConcurrencyModeEnum getConcurrenceMode();

    public void initialise();
}
