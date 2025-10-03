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
package net.fhirfactory.dricats.ui.server;

import net.fhirfactory.dricats.datagrid.topology.IApplicationComponentCacheClient;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Backing service for Camel REST routes. Contains simple methods that
 * return POJOs which Camel-Jackson can marshal to JSON.
 */
@ApplicationScoped
public class OAMBackEndForFrontEndHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OAMBackEndForFrontEndHandler.class);

    @Inject
    IApplicationComponentCacheClient componentCacheClient;

    @Inject
    ILocalMetricsServerInterface metricsServer;

    // -------------------- Application Components ----------------------------

    public ApplicationComponentSummary getComponent(String id) {
        if (id == null || id.isEmpty()) { return null; }
        return safeGet(id);
    }

    public List<ApplicationComponentSummary> listComponents() {
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
