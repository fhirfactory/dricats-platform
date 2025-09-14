package net.fhirfactory.dricats.model.topology.implementation.layers.application;

import net.fhirfactory.dricats.model.common.DistributableObjectIdentifier;
import net.fhirfactory.dricats.model.topology.implementation.layers.technology.valuesets.NetworkSecurityZoneEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubsystemTest {

    static class MySubsystem extends Subsystem {
        @Override
        DistributableObjectIdentifier specifySubsystemIdentifier() {
            return new DistributableObjectIdentifier();
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
