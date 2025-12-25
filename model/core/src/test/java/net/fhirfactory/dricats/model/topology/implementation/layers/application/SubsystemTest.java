package net.fhirfactory.dricats.model.topology.implementation.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.technology.valuesets.NetworkSecurityZoneEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubsystemTest {

    static class MySubsystem extends Subsystem {
        @Override
        public ElementIdentifier specifySubsystemIdentifier() {
            return new ElementIdentifier();
        }
    }

    @Test
    void gettersAndClusterMap() {
        MySubsystem ss = new MySubsystem();
        ss.setNetworkSecurityZone(NetworkSecurityZoneEnum.INTERNET);
        assertEquals(NetworkSecurityZoneEnum.INTERNET, ss.getNetworkSecurityZone());
        assertNotNull(ss.getApplicationCluster());
        assertTrue(ss.getApplicationCluster().isEmpty());
        assertSame(ss, ss.getSubsystem());
    }
}
