package net.fhirfactory.dricats.model.topology.implementation.layers.application.interfaces.base;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class InterfaceImplementationBaseTest {

    static class TestInterface extends InterfaceImplementationBase { }

    @Test
    void constructorsAndURI() {
        TestInterface a = new TestInterface();
        assertNotNull(a.getMetricsData());

        TestInterface b = new TestInterface();
        b.setName("n");
        b.setDocumentation("d");
        b.setSpecialization("spec");
        assertEquals("n", b.getName());
        assertEquals("d", b.getDocumentation());
        assertEquals("spec", b.getSpecialization());

        DistributableObjectId parent = new DistributableObjectId();
        TestInterface c = new TestInterface();
        c.setOwner(parent);
        c.setName("x");
        c.setDocumentation("y");
        c.setSpecialization("z");
        URI uri = URI.create("http://example.com");
        c.setURI(uri);
        assertEquals(uri, c.getURI());
    }
}
