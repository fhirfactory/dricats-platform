package net.fhirfactory.dricats.middleware.oam.central.rest;

import net.fhirfactory.dricats.datagrid.satellite.topologygrid.ApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Backing service for Camel REST routes. Contains simple methods that
 * return POJOs which Camel-Jackson can marshal to JSON.
 */
@ApplicationScoped
public class CentralOamService {
    private static final Logger LOG = LoggerFactory.getLogger(CentralOamService.class);

    @Inject
    ApplicationComponentCacheClient componentCacheClient;

    @Inject
    ILocalMetricsServerInterface metricsServer;

    // -------------------- Application Components ----------------------------

    public ApplicationComponentSummary getComponent(String id) {
        if (id == null || id.isEmpty()) { return null; }
        return safeGet(id);
    }

    public List<ApplicationComponentSummary> listComponents() {
        try {
            Optional<Cache<String, ApplicationComponentSummary>> oc = componentCacheClient.getCache();
            if (oc.isPresent()) {
                return oc.get().values().stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOG.warn("listComponents: error reading cache: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        ApplicationComponentSummary root = getComponent(id);
        if (root == null || root.getSubComponents() == null) { return Collections.emptyList(); }
        List<ApplicationComponentSummary> result = new ArrayList<>();
        for (DistributableObjectId childId : root.getSubComponents()) {
            String key = extractKey(childId);
            ApplicationComponentSummary child = safeGet(key);
            if (child != null) { result.add(child); }
        }
        return result;
    }

    // -------------------- Metrics -------------------------------------------

    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        ApplicationComponentSummary component = getComponent(id);
        if (component == null) { return null; }
        return metricsServer.getMetrics(component);
    }


    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        LocalDateTime s = parseDateTime(start);
        LocalDateTime e = parseDateTime(end);
        return metricsServer.getMetrics(s, e);
    }

    // -------------------- Helpers -------------------------------------------

    private ApplicationComponentSummary safeGet(String key) {
        try {
            return componentCacheClient.get(key);
        } catch (Exception e) {
            LOG.debug("safeGet: unable to fetch component with key={}: {}", key, e.getMessage());
            return null;
        }
    }

    private String extractKey(DistributableObjectId id) {
        if (id == null) { return null; }
        try {
            if (id.getId() != null && id.getId().getValue() != null) {
                return id.getId().getValue();
            }
        } catch (Exception ignore) { }
        try {
            QualifiedName qn = id.getQualifiedName();
            if (qn != null && qn.getCommonName() != null && qn.getCommonName().getValue() != null) {
                return qn.getCommonName().getValue();
            }
        } catch (Exception ignore) { }
        return null;
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isEmpty()) { return null; }
        try {
            return LocalDateTime.parse(s);
        } catch (DateTimeParseException e) {
            LOG.warn("parseDateTime: invalid date-time '{}', expected ISO-8601 LocalDateTime. Returning null.", s);
            return null;
        }
    }
}
