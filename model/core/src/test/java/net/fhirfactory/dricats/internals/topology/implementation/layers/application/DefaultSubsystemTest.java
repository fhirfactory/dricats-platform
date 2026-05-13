package net.fhirfactory.dricats.internals.topology.implementation.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSubsystemTest {

    @Test
    void defaultConstructorTest() {
        DefaultSubsystem subsystem = new DefaultSubsystem();
        assertNotNull(subsystem, "Subsystem instance should be created");
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getType(), subsystem.getSpecialization(), "Specialization should be SUBSYSTEM");
        
        // specifySubsystemIdentifier should return the same as getIdentifier
        ElementIdentifier identifier = subsystem.getIdentifier();
        assertEquals(identifier, subsystem.specifySubsystemIdentifier(), "specifySubsystemIdentifier should return the correct identifier");
    }

    @Test
    void parameterizedConstructorTest() {
        ElementIdentifier parentIdentifier = new ElementIdentifier();
        parentIdentifier.setIdentifierValue(new DistinguishedName());
        ElementReference parent = new ElementReference();
        parent.setElementIdentifier(parentIdentifier);

        String name = "TestSubsystem";
        String documentation = "Documentation for TestSubsystem";
        Map<String, String> extensions = new HashMap<>();
        extensions.put("key1", "value1");

        DefaultSubsystem subsystem = new DefaultSubsystem(parent, name, documentation, extensions);

        assertNotNull(subsystem, "Subsystem instance should be created");
        assertEquals(parent, subsystem.getParent(), "Parent should be correctly set");
        assertEquals(name, subsystem.getShortName(), "Short name should be correctly set");
        assertEquals(documentation, subsystem.getDocumentation(), "Documentation should be correctly set");
        assertEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getCode(), subsystem.getSpecialization(), "Specialization should be SUBSYSTEM code");
        assertEquals(extensions, subsystem.getExtensions(), "Extensions should be correctly set");
        
        // specifySubsystemIdentifier should return the same as getIdentifier
        ElementIdentifier identifier = subsystem.getIdentifier();
        assertEquals(identifier, subsystem.specifySubsystemIdentifier(), "specifySubsystemIdentifier should return the correct identifier");
    }
}
