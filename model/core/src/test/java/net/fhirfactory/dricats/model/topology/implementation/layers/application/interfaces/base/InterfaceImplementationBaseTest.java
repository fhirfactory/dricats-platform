package net.fhirfactory.dricats.model.topology.implementation.layers.application.interfaces.base;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InterfaceImplementationBaseTest {

    static class TestInterface extends WUPInterfaceBase { }

    @Test
    void constructorsAndURI() {
        TestInterface a = new TestInterface();
        assertNotNull(a.getMetricsData());

        TestInterface b = new TestInterface();
        b.setShortName("n");
        b.setDocumentation("d");
        b.setSpecialization("spec");
        assertEquals("n", b.getShortName());
        assertEquals("d", b.getDocumentation());
        assertEquals("spec", b.getSpecialization());

        ElementReference parent = new ElementReference();
        TestInterface c = new TestInterface();
        c.setOwner(parent);
        c.setShortName("x");
        c.setDocumentation("y");
        c.setSpecialization("z");
        URI uri = URI.create("http://example.com");
        c.setURI(uri);
        assertEquals(uri, c.getURI());
    }
}
