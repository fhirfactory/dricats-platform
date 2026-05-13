package net.fhirfactory.dricats.internals.topology.implementation.factories;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.*;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationComponentFactoryTest {

    private ApplicationComponentFactory applicationComponentFactory;
    private ApplicationInterfaceFactory applicationInterfaceFactory;

    private ElementReference parent;

    @BeforeEach
    void setUp() {
        applicationComponentFactory = new ApplicationComponentFactory();
        parent = new ElementReference();
        parent.setElementIdentifier(new ElementIdentifier());
    }

    @Test
    void testCreateApplicationComponentSolution() {
        String name = "SolutionTest";
        String doc = "SolutionDoc";
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SOLUTION);

        assertTrue(component instanceof Solution, "Should be an instance of Solution");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SOLUTION.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationComponentSubsystem() {
        String name = "SubsystemTest";
        String doc = "SubsystemDoc";
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SUBSYSTEM);

        assertTrue(component instanceof DefaultSubsystem, "Should be an instance of DefaultSubsystem");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationComponentCluster() {
        String name = "ClusterTest";
        String doc = "ClusterDoc";
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_CLUSTER);

        assertTrue(component instanceof DefaultSubsystemCluster, "Should be an instance of DefaultSubsystemCluster");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_CLUSTER.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationComponentInstance() {
        String name = "InstanceTest";
        String doc = "InstanceDoc";
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE);

        assertTrue(component instanceof DefaultSubsystemInstance, "Should be an instance of DefaultSubsystemInstance");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationComponentWUP() {
        String name = "WUPTest";
        String doc = "WUPDoc";
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR);

        assertTrue(component instanceof WorkUnitProcessor, "Should be an instance of WorkUnitProcessor");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationComponentDefault() {
        String name = "DefaultTest";
        String doc = "DefaultDoc";
        // Using an enum value that is not explicitly handled in the switch
        ApplicationComponent component = applicationComponentFactory.createApplicationComponent(parent, name, doc, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP);

        // Since it's not handled, it should be a base ApplicationComponent
        assertEquals(ApplicationComponent.class, component.getClass(), "Should be an instance of ApplicationComponent");
        assertEquals(parent, component.getParent());
        assertEquals(name, component.getShortName());
        assertEquals(doc, component.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP.getType(), component.getSpecialization());
    }

    @Test
    void testCreateApplicationInterfaceIngres() {
        String name = "IngresTest";
        String doc = "IngresDoc";
        ApplicationInterface iface = applicationInterfaceFactory.createInterface(parent, name, doc, InterfaceComponentTypeEnum.GENERIC_RECEIVER_INTERFACE, new HashMap<>());

        assertTrue(iface instanceof IngresApplicationInterface, "Should be an instance of IngresApplicationInterface");
        assertEquals(parent, iface.getOwner());
        assertEquals(name, iface.getShortName());
        assertEquals(doc, iface.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES.getType(), iface.getSpecialization());
    }

    @Test
    void testCreateApplicationInterfaceEgress() {
        String name = "EgressTest";
        String doc = "EgressDoc";
        ApplicationInterface iface = applicationInterfaceFactory.createInterface(parent, name, doc, InterfaceComponentTypeEnum.GENERIC_RECEIVER_INTERFACE, new HashMap<>());

        assertTrue(iface instanceof EgressApplicationInterface, "Should be an instance of EgressApplicationInterface");
        assertEquals(parent, iface.getOwner());
        assertEquals(name, iface.getShortName());
        assertEquals(doc, iface.getDocumentation());
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS.getType(), iface.getSpecialization());
    }

}
