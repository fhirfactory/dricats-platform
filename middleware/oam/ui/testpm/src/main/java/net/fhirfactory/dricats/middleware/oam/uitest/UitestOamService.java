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
package net.fhirfactory.dricats.middleware.oam.uitest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummaryList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory stubbed data provider for OAM UI tests. Provides a small
 * topology of components and simplistic metrics values.
 */
@ApplicationScoped
public class UitestOamService {
    private static final Logger LOG = LoggerFactory.getLogger(UitestOamService.class);
    private static final Map<String, ApplicationComponentSummary> COMPONENTS = new ConcurrentHashMap<>();

    static {
        // Build a tiny component tree: root -> child1, child2
        ApplicationComponentSummary root = new ApplicationComponentSummary();
        root.setName("Core Platform");
        root.setDocumentation("Top-level DRICaTS platform stub for UI testing");
        root.setObjectID(new DistributableObjectId("platform.core"));
        // Also set id common name for ease of UI resolution
        CommonName rootCN = new CommonName();
        rootCN.setValue("platform.core");
        root.setId(rootCN);
        root.setComponentStatus(new ApplicationComponentStatusSummary());

        ApplicationComponentSummary child1 = new ApplicationComponentSummary();
        child1.setName("Messaging Service");
        child1.setDocumentation("Handles inter-component messaging");
        child1.setObjectID(new DistributableObjectId("platform.core.messaging"));
        CommonName c1CN = new CommonName();
        c1CN.setValue("platform.core.messaging");
        child1.setId(c1CN);
        child1.setParent(new DistributableObjectId("platform.core"));
        child1.setComponentStatus(new ApplicationComponentStatusSummary());

        ApplicationComponentSummary child2 = new ApplicationComponentSummary();
        child2.setName("Metrics Service");
        child2.setDocumentation("Collects and serves metrics");
        child2.setObjectID(new DistributableObjectId("platform.core.metrics"));
        CommonName c2CN = new CommonName();
        c2CN.setValue("platform.core.metrics");
        child2.setId(c2CN);
        child2.setParent(new DistributableObjectId("platform.core"));
        child2.setComponentStatus(new ApplicationComponentStatusSummary());

        // Wire subcomponents on root
        root.setSubComponents(Arrays.asList(
                new DistributableObjectId("platform.core.messaging"),
                new DistributableObjectId("platform.core.metrics")
        ));

        COMPONENTS.put(keyOf(root), root);
        COMPONENTS.put(keyOf(child1), child1);
        COMPONENTS.put(keyOf(child2), child2);

        LoggerFactory.getLogger(UitestOamService.class).info("Initialized in-memory component graph: {} entries", COMPONENTS.size());
    }

    public String listComponents() {
        LOG.debug("listComponents() invoked");
        List<ApplicationComponentSummary> list = new ArrayList<>(COMPONENTS.values());
        LOG.info("Returning {} components", list.size());
        ApplicationComponentSummaryList resultList = new ApplicationComponentSummaryList();
        for(ApplicationComponentSummary currentListItem : list){
            resultList.getElementList().add(currentListItem);
        }
        JsonMapper jsonMapper = new JsonMapper();
        String resultAsString = "";
        try {
            resultAsString = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultList);
        } catch (JsonProcessingException e) {
            LOG.info("Error serializing component list: {}", e.getMessage());
        }
        return(resultAsString);
    }

    public ApplicationComponentSummary getComponent(String id) {
        LOG.debug("getComponent(id={}) invoked", id);
        if (id == null || id.isEmpty()) {
            LOG.warn("getComponent called with empty id");
            return null;
        }
        ApplicationComponentSummary c = COMPONENTS.get(normalizeKey(id));
        if (c == null) {
            LOG.warn("Component not found for id={}", id);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("Found component name={} idKey={}", c.getName(), keyOf(c));
        }
        return c;
    }

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        LOG.debug("getSubComponents(id={}) invoked", id);
        ApplicationComponentSummary c = getComponent(id);
        if (c == null || c.getSubComponents() == null) {
            LOG.info("No subcomponents for id={} (component missing or has none)", id);
            return Collections.emptyList();
        }
        List<ApplicationComponentSummary> subs = c.getSubComponents().stream()
                .map(this::extractKey)
                .map(COMPONENTS::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LOG.info("Returning {} subcomponents for id={}", subs.size(), id);
        return subs;
    }

    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        LOG.debug("getLatestMetricsForComponent(id={}) invoked", id);
        ApplicationComponentSummary c = getComponent(id);
        if (c == null) {
            LOG.warn("Cannot build metrics: component not found for id={}", id);
            return null;
        }
        ApplicationComponentMetricsData m = new ApplicationComponentMetricsData();
        m.setComponentID(c.getObjectID());
        m.setParticipantName(Optional.ofNullable(c.getName()).orElse(""));
        m.setComponentStatus("OK");
        // Note: Avoid setting java.time.Instant fields to keep JSON marshalling simple without extra modules
        // m.setComponentStartupInstant(Instant.now().minusSeconds(3600));
        // m.setLastActivityInstant(Instant.now());
        // leave nested stats mostly default for simplicity
        LOG.info("Built latest metrics for id={} name={}", id, c.getName());
        return m;
    }

    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        LOG.debug("getMetricsInRange(start={}, end={}) invoked", start, end);
        // For UI tests, just return the latest metrics for all components
        List<ApplicationComponentMetricsData> list = COMPONENTS.values().stream()
                .map(c -> getLatestMetricsForComponent(keyOf(c)))
                .collect(Collectors.toList());
        LOG.info("Returning {} metrics entries for range [start={}, end={}]", list.size(), start, end);
        return list;
    }

    private String extractKey(DistributableObjectId id) {
        if (id == null) { return null; }
        try {
            if (id.getId() != null && id.getId().getValue() != null) {
                return id.getId().getValue();
            }
        } catch (Exception e) {
            LOG.debug("Failed to extract key via id.value: {}", e.getMessage());
        }
        try {
            QualifiedName qn = id.getQualifiedName();
            if (qn != null && qn.getCommonName() != null && qn.getCommonName().getValue() != null) {
                return qn.getCommonName().getValue();
            }
        } catch (Exception e) {
            LOG.debug("Failed to extract key via qualifiedName.commonName.value: {}", e.getMessage());
        }
        return null;
    }

    private static String keyOf(ApplicationComponentSummary c) {
        if (c == null) { return null; }
        try {
            if (c.getId() != null && c.getId().getValue() != null) {
                return c.getId().getValue();
            }
        } catch (Exception e) {
            LOG.debug("keyOf: id.value missing: {}", e.getMessage());
        }
        try {
            if (c.getObjectID() != null && c.getObjectID().getId() != null && c.getObjectID().getId().getValue() != null) {
                return c.getObjectID().getId().getValue();
            }
        } catch (Exception e) {
            LOG.debug("keyOf: objectID.id.value missing: {}", e.getMessage());
        }
        return Optional.ofNullable(c.getName()).orElse("");
    }

    private static String normalizeKey(String id) {
        // IDs may be URL-decoded; just return as-is here
        return id;
    }
}
