/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.ui.uitest.handlers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummaryList;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.ui.serverside.metrics.UIMetricsCacheService;
import net.fhirfactory.dricats.ui.serverside.topology.UITopologyCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.common.BaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory stubbed data provider for OAM UI tests. Provides a small
 * topology of components and simplistic metrics values.
 */
@ApplicationScoped
public class TestTopologyServices extends BaseHandler {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TestTopologyServices.class);

    //
    // Attributes
    //

    @Inject
    private UITopologyCacheService testComponentServices;

    @Inject
    private UIMetricsCacheService testMetricsService;

    //
    // Constructor(s)
    //

    public TestTopologyServices() {
        LOG.debug(".constructor(): Entry");
        getLogger().debug(".constructor(): Exit");
    }

    //
    // Post Construct (for dependency injection)
    //
    @PostConstruct
    public void initialise() {
        LOG.debug("initialise() invoked");
        getTestMetricsService().initialise();
        getTestComponentServices().initialise();
        populateComponentMap();
        LOG.debug("initialise() exit");
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return (LOG);
    }

    protected UITopologyCacheService getTestComponentServices() {
        return testComponentServices;
    }

    protected UIMetricsCacheService getTestMetricsService() {
        return testMetricsService;
    }

    //
     // Business Methods
    //

    public String listComponentsAsJSON() {
        LOG.debug("listComponents() invoked");
        List<ApplicationComponentSummary> list = new ArrayList<>(testComponentServices.getComponents().values());
        LOG.info("Returning {} components", list.size());
        ApplicationComponentSummaryList resultList = new ApplicationComponentSummaryList();
        for (ApplicationComponentSummary currentListItem : list) {
            if(currentListItem.getParent() == null) {
                resultList.getElementList().add(currentListItem);
            }
        }
        return(convertToJson(resultList));
    }

    public ApplicationComponentSummary getComponent(String id) {
        LOG.debug(".getComponent(): Entry, id={}", id);
        if (id == null || id.isEmpty()) {
            LOG.warn(".getComponent(), Exit, called with empty id");
            return null;
        }
        ApplicationComponentSummary c = testComponentServices.getComponents().get(id);
        if (c == null) {
            LOG.warn(".getComponent(): Component not found for id={}", id);
        }
        LOG.debug(".getComponent(): Exit, Returning component id={}", c);
        return c;
    }

    public String getComponentAsJSON(String id){
        ApplicationComponentSummary component = getComponent(id);
        return(convertToJson(component));
    }

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        LOG.debug("getSubComponents(id={}) invoked", id);
        List<ApplicationComponentSummary> subComponents = getTestComponentServices().getSubComponents(id);
        return subComponents;
    }

    public String getSubComponentsAsJSON(String id){
        List<ApplicationComponentSummary> subs = getSubComponents(id);
        return(convertToJson(subs));
    }

    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        LOG.debug(".getLatestMetricsForComponent(): Entry, id={}", id);
        ApplicationComponentSummary component = getComponent(id);
        ApplicationComponentMetricsData latestMetricsForComponent = testMetricsService.getLatestMetricsForComponent(component);
        LOG.debug(".getLatestMetricsForComponent(): Exit, Returning latest metrics for component id={}", id);
        return(latestMetricsForComponent);
    }

        public String getLatestMetricsForComponentAsJSON(String id){
        ApplicationComponentMetricsData m = getLatestMetricsForComponent(id);
        return(convertToJson(m));
    }

    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        LOG.debug("getMetricsInRange(start={}, end={}) invoked", start, end);
        // For UI tests, just return the latest metrics for all components
        List<ApplicationComponentMetricsData> list = testComponentServices.getComponents().values().stream()
                .map(c -> getLatestMetricsForComponent(keyOf(c)))
                .collect(Collectors.toList());
        LOG.info("Returning {} metrics entries for range [start={}, end={}]", list.size(), start, end);
        return list;
    }

    public String getMetricsInRangeAsJSON(String start, String end){
        List<ApplicationComponentMetricsData> list = getMetricsInRange(start, end);
        return(convertToJson(list));
    }

    private static String keyOf(ApplicationComponentSummary c) {
        String key = c.resolveKey();
        return(key);
    }

    private static String normalizeKey(String id) {
        // IDs may be URL-decoded; just return as-is here
        return id;
    }



    //
     // Test Data Loader
    //

    private void populateComponentMap (){
        // Build a tiny component tree: application -> child1, child2
        ApplicationComponentSummary application = new ApplicationComponentSummary();
        application.setName("Test Subsystem");
        application.setDocumentation("Top-level DRICaTS platform stub for UI testing");
        UnqualifiedName unqName = new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM.getType(), "TestSubsystem");
        QualifiedName applicationName = new QualifiedName();
        applicationName.appendUnqualifiedName(unqName);
        application.setObjectID(new DistributableObjectId(applicationName));
        application.setComponentStatus(new ApplicationComponentStatusSummary());
        application.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        application.setSpecialization(SoftwareComponentTypeEnum.SUBSYSTEM.getType());
        application.getMetadata().setCreationDate(LocalDateTime.now());
        application.getMetadata().setLastUpdateDate(LocalDateTime.now());
        application.getComponentStatus().setComponentStatus("Operational");
        application.getComponentStatus().setComponentStatus("Operational and Processing");
        application.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        application.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        LOG.info(".populateComponentMap(): Adding Subsystem to Component Map -> key {}", application.resolveKey());
        getTestComponentServices().getComponents().put(application.resolveKey(), application);

        ApplicationComponentSummary applicationInstance = new ApplicationComponentSummary();
        applicationInstance.setName("Test Application Instance");
        applicationInstance.setDocumentation("A Single Instance of the Test Subsystem Application");
        String uniqueId = String.valueOf(LocalDateTime.now().getSecond()) + String.valueOf(LocalDateTime.now().getNano());
        UnqualifiedName applicationInstanceUnqualifiedName = new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType(), uniqueId);
        QualifiedName applicationInstanceQualifiedName = new QualifiedName(applicationName);
        applicationInstanceQualifiedName.appendUnqualifiedName(applicationInstanceUnqualifiedName);
        applicationInstance.setObjectID(new DistributableObjectId(applicationInstanceQualifiedName));
        applicationInstance.setComponentStatus(new ApplicationComponentStatusSummary());
        applicationInstance.setParent(application.getObjectID());
        applicationInstance.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        applicationInstance.setSpecialization(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType());
        applicationInstance.getMetadata().setCreationDate(LocalDateTime.now());
        applicationInstance.getMetadata().setLastUpdateDate(LocalDateTime.now());
        applicationInstance.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        applicationInstance.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        application.getSubComponents().add(applicationInstance.getObjectID());
        getTestComponentServices().getComponents().put(applicationInstance.resolveKey(), applicationInstance);
        LOG.info(".populateComponentMap(): Adding Application Instance to Component Map -> key {}",applicationInstance.resolveKey());

        ApplicationComponentSummary child1 = new ApplicationComponentSummary();
        child1.setName("Messaging Services");
        child1.setDocumentation("Handles inter-component messaging");
        UnqualifiedName unqName1 = new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK.getType(), "MessagingServices");
        QualifiedName qualifiedName1 = new QualifiedName(applicationInstanceQualifiedName);
        qualifiedName1.appendUnqualifiedName(unqName1);
        child1.setObjectID(new DistributableObjectId(qualifiedName1));
        child1.setParent(applicationInstance.getObjectID());
        child1.setComponentStatus(new ApplicationComponentStatusSummary());
        child1.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        child1.setSpecialization(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK.getType());;
        child1.getMetadata().setCreationDate(LocalDateTime.now());
        child1.getMetadata().setLastUpdateDate(LocalDateTime.now());
        child1.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        child1.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        applicationInstance.getSubComponents().add(child1.getObjectID());
        getTestComponentServices().getComponents().put(child1.resolveKey(), child1);
        LOG.info(".populateComponentMap(): Adding Messaging Services to Component Map -> key {}", child1.resolveKey());

        ApplicationComponentSummary child2 = new ApplicationComponentSummary();
        child2.setName("Metrics Service");
        child2.setDocumentation("Collects and serves metrics");
        UnqualifiedName unqName2 = new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK.getType(), "MetricsService");
        QualifiedName qualifiedName2 = new QualifiedName(applicationInstanceQualifiedName);
        qualifiedName2.appendUnqualifiedName(unqName2);
        child2.setObjectID(new DistributableObjectId(qualifiedName2));
        child2.setParent(applicationInstance.getObjectID());
        child2.setComponentStatus(new ApplicationComponentStatusSummary());
        child2.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        child2.setSpecialization(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK.getType());;
        child2.getMetadata().setCreationDate(LocalDateTime.now());
        child2.getMetadata().setLastUpdateDate(LocalDateTime.now());
        child2.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        child2.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        applicationInstance.getSubComponents().add(child2.getObjectID());
        getTestComponentServices().getComponents().put(child2.resolveKey(), child2);
        LOG.info(".populateComponentMap(): Adding Metrics Service to Component Map -> key {}", child2.resolveKey());

        LOG.info("Initialized in-memory component graph: {} entries", getTestComponentServices().getComponents().size());
    }
}
