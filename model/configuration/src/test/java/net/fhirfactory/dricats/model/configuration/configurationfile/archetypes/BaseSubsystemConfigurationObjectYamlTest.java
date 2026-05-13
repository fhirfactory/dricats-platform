package net.fhirfactory.dricats.model.configuration.configurationfile.archetypes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.fhirfactory.dricats.internals.configuration.segments.*;
import net.fhirfactory.dricats.internals.configuration.valuesets.ApplicationConcurrencyModeEnum;
import net.fhirfactory.dricats.internals.configuration.valuesets.ApplicationDeploymentModeEnum;
import net.fhirfactory.dricats.model.configuration.configurationfile.base.BaseSubsystemConfigurationObject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BaseSubsystemConfigurationObjectYamlTest {

    @Test
    public void testWriteAndReadYamlRoundTrip() throws Exception {
        // Build fully populated reference instance
        BaseSubsystemConfigurationObject reference = buildFullyPopulatedReference();

        // Configure YAML mapper
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        yamlMapper.findAndRegisterModules();
        yamlMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);

        // Write YAML file to project root with timestamp in filename
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String ts = now.format(fmt);
        java.nio.file.Path projectRoot = java.nio.file.Paths.get("").toAbsolutePath();
        String filename = "BaseSubsystemConfigurationObject-" + ts + ".yaml";
        java.nio.file.Path outputFile = projectRoot.resolve(filename);
        yamlMapper.writeValue(outputFile.toFile(), reference);

        // Read back from YAML
        BaseSubsystemConfigurationObject readBack = yamlMapper.readValue(outputFile.toFile(), BaseSubsystemConfigurationObject.class);

        // Validate fields (field-by-field to avoid relying on missing equals on container classes)
        assertNotNull(readBack);
        assertSolutionEquals(reference.getSolution(), readBack.getSolution());
        assertApplicationEquals(reference.getApplication(), readBack.getApplication());
        assertApplicationInstanceEquals(reference.getApplicationInstance(), readBack.getApplicationInstance());
        assertSiteEquals(reference.getDeploymentMode(), readBack.getDeploymentMode());
        assertMultiSiteEquals(reference.getDeploymentSites(), readBack.getDeploymentSites());
        assertDeploymentZoneEquals(reference.getDeploymentZone(), readBack.getDeploymentZone());
        assertImageEquals(reference.getSubsystemImageProperties(), readBack.getSubsystemImageProperties());
    }

    private BaseSubsystemConfigurationObject buildFullyPopulatedReference() throws Exception {
        BaseSubsystemConfigurationObject config = new BaseSubsystemConfigurationObject();

        // Solution
        SolutionConfigurationObject solution = new SolutionConfigurationObject();
        solution.setSolutionName("CareFlow");
        solution.setSolutionGroup("ACTHealth");
        solution.setSolutionDescription("CareFlow Integrated Care Platform");
        solution.setEncryptedAtRestRequired(true);
        solution.setEncryptedAtTransitRequired(true);
        config.setSolution(solution);

        // Application
        ApplicationConfigurationObject app = new ApplicationConfigurationObject();
        app.setVendorName("DRICaTS");
        app.setApplicationName("OAM");
        app.setApplicationVersion("1.2.3");
        config.setApplication(app);

        // Application Instance
        ApplicationInstanceConfigurationObject appInstance = new ApplicationInstanceConfigurationObject();
        appInstance.setDeploymentConfigFilename("/etc/dricats/oam.yaml");
        appInstance.setOtherDeploymentFlags("debug=true;trace=false");
        appInstance.setApplicationInstanceId("oam-instance-001");
        appInstance.setDeploymentMode(ApplicationDeploymentModeEnum.CONTAINER_KUBERNETES);
        config.setApplicationInstance(appInstance);

        // Site (deployment mode)
        SiteConfigurationObject site = new SiteConfigurationObject();
        site.setSiteName("Canberra-Primary");
        site.setSiteInstanceReplicationCount(3);
        site.setSiteConcurrencyMode(ApplicationConcurrencyModeEnum.ROUND_ROBIN);
        Map<String, String> flags = new LinkedHashMap<>();
        flags.put("maintenanceWindow", "Sun-02:00");
        flags.put("preferredAZ", "az-east-1");
        site.setOtherSiteDeploymentFlags(flags);
        config.setDeploymentMode(site);

        // Multi-site
        MultiSiteConfigurationObject multi = new MultiSiteConfigurationObject();
        multi.setSiteCount(2);
        multi.setSiteNames(Arrays.asList("Canberra-Primary", "Canberra-Secondary"));
        config.setDeploymentSites(multi);

        // Deployment Zone
        DeploymentZoneConfigurationObject zone = new DeploymentZoneConfigurationObject();
        zone.setSecurityZoneName("prod-secure");
        zone.setNameSpace("oam-prod");
        config.setDeploymentZone(zone);

        // Image properties
        ApplicationInstanceImageConfigurationObject image = new ApplicationInstanceImageConfigurationObject();
        image.setRepository("registry.example.com/dricats");
        image.setImageName("oam-service");
        image.setImageVersion("2025.09.28");
        image.setPullPolicy("IfNotPresent");
        config.setSubsystemImageProperties(image);

        return config;
    }

    private void assertSolutionEquals(SolutionConfigurationObject expected, SolutionConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getSolutionName(), actual.getSolutionName());
        assertEquals(expected.getSolutionGroup(), actual.getSolutionGroup());
        assertEquals(expected.getSolutionDescription(), actual.getSolutionDescription());
        // DistributableObjectIdentifierType has equals
        assertEquals(expected.isEncryptedAtRestRequired(), actual.isEncryptedAtRestRequired());
        assertEquals(expected.isEncryptedAtTransitRequired(), actual.isEncryptedAtTransitRequired());
    }

    private void assertApplicationEquals(ApplicationConfigurationObject expected, ApplicationConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getVendorName(), actual.getVendorName());
        assertEquals(expected.getApplicationName(), actual.getApplicationName());
        assertEquals(expected.getApplicationVersion(), actual.getApplicationVersion());
    }

    private void assertApplicationInstanceEquals(ApplicationInstanceConfigurationObject expected, ApplicationInstanceConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getDeploymentConfigFilename(), actual.getDeploymentConfigFilename());
        assertEquals(expected.getOtherDeploymentFlags(), actual.getOtherDeploymentFlags());
        assertEquals(expected.getApplicationInstanceId(), actual.getApplicationInstanceId());
        assertEquals(expected.getDeploymentMode(), actual.getDeploymentMode());
    }

    private void assertSiteEquals(SiteConfigurationObject expected, SiteConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getSiteName(), actual.getSiteName());
        assertEquals(expected.getSiteInstanceReplicationCount(), actual.getSiteInstanceReplicationCount());
        assertEquals(expected.getSiteConcurrencyMode(), actual.getSiteConcurrencyMode());
        assertEquals(expected.getOtherSiteDeploymentFlags(), actual.getOtherSiteDeploymentFlags());
    }

    private void assertMultiSiteEquals(MultiSiteConfigurationObject expected, MultiSiteConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getSiteCount(), actual.getSiteCount());
        assertEquals(expected.getSiteNames(), actual.getSiteNames());
    }

    private void assertDeploymentZoneEquals(DeploymentZoneConfigurationObject expected, DeploymentZoneConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getSecurityZoneName(), actual.getSecurityZoneName());
        assertEquals(expected.getNameSpace(), actual.getNameSpace());
    }

    private void assertImageEquals(ApplicationInstanceImageConfigurationObject expected, ApplicationInstanceImageConfigurationObject actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getRepository(), actual.getRepository());
        assertEquals(expected.getImageName(), actual.getImageName());
        assertEquals(expected.getImageVersion(), actual.getImageVersion());
        assertEquals(expected.getPullPolicy(), actual.getPullPolicy());
    }
}
