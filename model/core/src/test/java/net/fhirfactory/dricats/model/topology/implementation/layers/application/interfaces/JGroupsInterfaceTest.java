package net.fhirfactory.dricats.model.topology.implementation.layers.application.interfaces;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.configuration.ports.internal.JGroupsInterfaceConfigurationObject;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.JGroupsInterface;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JGroupsInterfaceTest {
    private static final Logger LOG = LoggerFactory.getLogger(JGroupsInterfaceTest.class);

    @Test
    void constructorsAndBeanMethods() {
        JGroupsInterface iface = new JGroupsInterface();
        assertNotNull(iface);

        JGroupsInterface iface2 = new JGroupsInterface("name","doc", JGroupsInterface.INTERFACE_JGROUPS_MESSAGE);
        assertEquals("name", iface2.getName());
        assertEquals("doc", iface2.getDocumentation());
        assertEquals(JGroupsInterface.INTERFACE_JGROUPS_MESSAGE, iface2.getSpecialization());

        DistributableObjectId parent = new DistributableObjectId();
        JGroupsInterface iface3 = new JGroupsInterface(parent, "n2","d2", JGroupsInterface.INTERFACE_JGROUPS_RMI);
        assertEquals(parent, iface3.getOwner());

        URI uri = URI.create("urn:test:uri");
        JGroupsInterface iface4 = new JGroupsInterface(parent, "n3","d3", JGroupsInterface.INTERFACE_JGROUPS_RMI, uri);
        assertEquals(uri, iface4.getURI());

        JGroupsInterfaceConfigurationObject cfg = new JGroupsInterfaceConfigurationObject();
        iface4.setConfigurationObject(cfg);
        assertSame(cfg, iface4.getConfigurationObject());
    }

    @Test
    void equalsHashCodeToString() {
        LOG.debug(".equalsHashCodeToString(): Testing equals and hashCode methods for JGroupsInterface");
        LOG.trace(".equalsHashCodeToString(): Creating Parent ID: Start");
        DistributableObjectId parent = new DistributableObjectId();
        LOG.trace(".equalsHashCodeToString(): Creating Parent ID: Finish");
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface <a> Start");
        JGroupsInterface a = new JGroupsInterface(parent, "n","d", JGroupsInterface.INTERFACE_JGROUPS_RMI);
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface, <a> -> {}", a);
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface <a> Finish");
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface <b> Start");
        JGroupsInterface b = new JGroupsInterface(parent, "n","d", JGroupsInterface.INTERFACE_JGROUPS_RMI);
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface, <b> -> {}", b);
        LOG.trace(".equalsHashCodeToString(): Creating JGroupsInterface <b> Finish");

        LOG.trace(".equalsHashCodeToString(): assertEquals(a, b): Start");
        assertEquals(a, b);
        LOG.trace(".equalsHashCodeToString(): assertEquals(a, b): Finish");
        LOG.trace(".equalsHashCodeToString(): assertEquals(a.hashCode(), b.hashCode()): Start");
        assertEquals(a.hashCode(), b.hashCode());
        LOG.trace(".equalsHashCodeToString(): assertEquals(a.hashCode(), b.hashCode()): Finish");

        // change configuration object should change equality
        LOG.trace(".equalsHashCodeToString(): Changing configuration object");
        LOG.trace(".equalsHashCodeToString(): Create New JGroupsInterface Configuration Object: Start");
        JGroupsInterfaceConfigurationObject cfg = new JGroupsInterfaceConfigurationObject(Instant.now().toString());
        LOG.trace(".equalsHashCodeToString(): Create New JGroupsInterface Configuration Object: Finish");
        LOG.trace(".equalsHashCodeToString(): Updating <a> object with new configuration object: Start");
        a.setConfigurationObject(cfg);
        LOG.trace(".equalsHashCodeToString(): Updating <a> object with new configuration object: Finish");
        LOG.trace(".equalsHashCodeToString(): assertNotEquals(a, b): Start");
        assertNotEquals(a, b);
        LOG.trace(".equalsHashCodeToString(): assertNotEquals(a, b): Finish");

        LOG.trace(".equalsHashCodeToString(): a.toString(): Start");
        String s = a.toString();
        LOG.trace(".equalsHashCodeToString(): a.toString(): Finish");
        assertNotNull(s);
        assertTrue(s.contains("JGroupsInterface"));
        LOG.debug(".equalsHashCodeToString(): Testing equals and hashCode methods for JGroupsInterface: Finish");
    }
}
