package net.fhirfactory.dricats.model.topology.reference.layers.application;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.topology.reference.common.valuesets.ElementTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationInterfaceTest {

    @Test
    void constructorsAndBusinessMethods() {
        ApplicationInterface a = new ApplicationInterface();
        assertEquals(ElementTypeEnum.APPLICATION_INTERFACE, a.getElementType());
        assertNotNull(a.getServices());
        assertTrue(a.getServices().isEmpty());

        ApplicationInterface b = new ApplicationInterface("n","d","s");
        assertEquals("n", b.getName());
        assertEquals("d", b.getDocumentation());
        assertEquals("s", b.getSpecialization());

        DistributableObjectId owner = new DistributableObjectId();
        ApplicationInterface c = new ApplicationInterface();
        c.setOwner(owner);
        assertEquals(owner, c.getOwner());

        DistributableObjectId svc = new DistributableObjectId();
        c.addService(svc);
        assertEquals(1, c.getServices().size());
    }
}
