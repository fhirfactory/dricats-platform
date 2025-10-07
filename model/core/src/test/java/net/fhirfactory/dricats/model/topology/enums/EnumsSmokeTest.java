package net.fhirfactory.dricats.model.topology.enums;

import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyComponentIdentifierTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.technology.valuesets.InfrastructureComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.technology.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.reference.archimate.relationships.valuesets.RelationshipType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumsSmokeTest {

    @Test
    void enumsHaveValues() {
        assertTrue(TopologyComponentIdentifierTypeEnum.values().length > 0);
        assertTrue(SoftwareComponentTypeEnum.values().length > 0);
        assertTrue(InfrastructureComponentTypeEnum.values().length > 0);
        assertTrue(NetworkSecurityZoneEnum.values().length > 0);
        assertTrue(PathwayRouteSelectionCriteriaEnum.values().length > 0);
        assertTrue(RelationshipType.values().length > 0);
    }

    @Test
    void networkSecurityZoneHelpers() {
        assertEquals(NetworkSecurityZoneEnum.INTERNET, NetworkSecurityZoneEnum.fromDisplayName("Internet"));
        assertEquals(NetworkSecurityZoneEnum.INTERNET, NetworkSecurityZoneEnum.fromSecurityZoneCamelCaseString("Internet"));
        assertNull(NetworkSecurityZoneEnum.fromDisplayName("NotAZone"));
    }
}
