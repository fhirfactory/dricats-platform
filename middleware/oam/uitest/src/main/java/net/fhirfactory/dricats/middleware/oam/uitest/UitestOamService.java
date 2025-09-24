package net.fhirfactory.dricats.middleware.oam.uitest;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory stubbed data provider for OAM UI tests. Provides a small
 * topology of components and simplistic metrics values.
 */
public class UitestOamService {
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
    }

    public List<ApplicationComponentSummary> listComponents() {
        return new ArrayList<>(COMPONENTS.values());
    }

    public ApplicationComponentSummary getComponent(String id) {
        if (id == null || id.isEmpty()) { return null; }
        return COMPONENTS.get(normalizeKey(id));
    }

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        ApplicationComponentSummary c = getComponent(id);
        if (c == null || c.getSubComponents() == null) { return Collections.emptyList(); }
        return c.getSubComponents().stream()
                .map(this::extractKey)
                .map(COMPONENTS::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        ApplicationComponentSummary c = getComponent(id);
        if (c == null) { return null; }
        ApplicationComponentMetricsData m = new ApplicationComponentMetricsData();
        m.setComponentID(c.getObjectID());
        m.setParticipantName(Optional.ofNullable(c.getName()).orElse(""));
        m.setComponentStatus("OK");
        m.setComponentStartupInstant(Instant.now().minusSeconds(3600));
        m.setLastActivityInstant(Instant.now());
        // leave nested stats mostly default for simplicity
        return m;
    }

    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        // For UI tests, just return the latest metrics for all components
        return COMPONENTS.values().stream()
                .map(c -> getLatestMetricsForComponent(keyOf(c)))
                .collect(Collectors.toList());
    }

    private String extractKey(DistributableObjectId id) {
        if (id == null) { return null; }
        try {
            if (id.getId() != null && id.getId().getValue() != null) {
                return id.getId().getValue();
            }
        } catch (Exception ignore) {}
        try {
            QualifiedName qn = id.getQualifiedName();
            if (qn != null && qn.getCommonName() != null && qn.getCommonName().getValue() != null) {
                return qn.getCommonName().getValue();
            }
        } catch (Exception ignore) {}
        return null;
    }

    private static String keyOf(ApplicationComponentSummary c) {
        if (c == null) { return null; }
        try {
            if (c.getId() != null && c.getId().getValue() != null) {
                return c.getId().getValue();
            }
        } catch (Exception ignore) {}
        try {
            if (c.getObjectID() != null && c.getObjectID().getId() != null && c.getObjectID().getId().getValue() != null) {
                return c.getObjectID().getId().getValue();
            }
        } catch (Exception ignore) {}
        return Optional.ofNullable(c.getName()).orElse("");
    }

    private static String normalizeKey(String id) {
        // IDs may be URL-decoded; just return as-is here
        return id;
    }
}
