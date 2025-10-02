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
package net.fhirfactory.dricats.ui.serverside.topology;

import jakarta.annotation.PostConstruct;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UITopologyService {
    //
     // Housekeeping
    //
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UITopologyService.class);

    //
     // Attributes
    //
    private Map<String, ApplicationComponentSummary> components = new ConcurrentHashMap<>();
    private boolean initialized = false;

    //
     // Constructor(s)
    //

    public UITopologyService(){
        components = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        if (!initialized) {
            populateComponentMap();
            initialized = true;
        }
        LOG.debug(".initialise(): Exit");
    }

    //
     // Business Methods
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
        components.put(application.resolveKey(), application);

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
        components.put(applicationInstance.resolveKey(), applicationInstance);
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
        components.put(child1.resolveKey(), child1);
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
        components.put(child2.resolveKey(), child2);
        LOG.info(".populateComponentMap(): Adding Metrics Service to Component Map -> key {}", child2.resolveKey());

        LoggerFactory.getLogger(UitestOamService.class).info("Initialized in-memory component graph: {} entries", components.size());
    }

    //
     // Bean Methods
    //

    public Map<String, ApplicationComponentSummary> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ApplicationComponentSummary> components) {
        this.components = components;
    }

    //
     // Business Methods
    //

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        LOG.debug(".getSubComponents(): Entry, id={}", id);
        ApplicationComponentSummary c = components.get(id);
        if (c == null || c.getSubComponents() == null) {
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} (component missing or has none)", id);
            return Collections.emptyList();
        }
        List<ApplicationComponentSummary> result = new ArrayList<>();
        for (DistributableObjectId childId : c.getSubComponents()) {
            String key = childId.getQualifiedName().getCommonName().getValue();
            ApplicationComponentSummary child = components.get(key);
            if (child != null) {
                result.add(child);
            } else {
                LOG.warn(".getSubComponents(): Exit, No child component found for id={}", key);
            }
        }
        LOG.info(".getSubComponents(): Exit, Returning {} subcomponents for id={}", result.size(), id);
        return result;
    }
}
