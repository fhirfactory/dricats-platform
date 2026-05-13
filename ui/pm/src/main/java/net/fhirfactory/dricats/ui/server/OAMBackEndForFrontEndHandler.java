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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Backing service for Camel REST routes. Contains simple methods that
 * return POJOs which Camel-Jackson can marshal to JSON.
 */
@ApplicationScoped
public class OAMBackEndForFrontEndHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OAMBackEndForFrontEndHandler.class);

    @Inject
    UITopologyCacheService topologyCacheService;

    @Inject
    ILocalMetricsServerInterface metricsServer;

    // -------------------- Application Components ----------------------------

    public ApplicationComponent getComponent(String id) {
        if (id == null || id.isEmpty()) { return null; }
        ElementBase element = topologyCacheService.getComponent(id);
        if (element instanceof ApplicationComponent) {
            return (ApplicationComponent) element;
        }
        return null;
    }

    public List<ApplicationComponent> listComponents() {
        return topologyCacheService.getAllApplicationComponents().stream().toList();
    }

    public List<ApplicationComponent> getSubComponents(String id) {
        return topologyCacheService.getSubComponents(id);
    }

    // -------------------- Metrics -------------------------------------------

    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        ApplicationComponent component = getComponent(id);
        if (component == null) { return null; }
        return metricsServer.getMetrics(component);
    }


    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        LocalDateTime s = parseDateTime(start);
        LocalDateTime e = parseDateTime(end);
        return metricsServer.getMetrics(s, e);
    }

    // -------------------- Helpers -------------------------------------------

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
