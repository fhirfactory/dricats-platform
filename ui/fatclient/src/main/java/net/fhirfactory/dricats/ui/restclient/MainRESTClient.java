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
package net.fhirfactory.dricats.ui.restclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.id.ObjectKey;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainRESTClient {
    //
    // Housekeeping
    //
    private static final JulLogger LOG = new JulLogger(MainRESTClient.class);

    // Simple adapter to support SLF4J-like `{}` formatting with java.util.logging
    private static final class JulLogger {
        private final Logger logger;
        JulLogger(Class<?> cls) { this.logger = Logger.getLogger(cls.getName()); }
        void info(String msg, Object... args) {
            if (logger.isLoggable(Level.INFO)) { logger.log(Level.INFO, format(msg, args)); }
        }
        void warn(String msg, Object... args) {
            if (logger.isLoggable(Level.WARNING)) { logger.log(Level.WARNING, format(msg, args)); }
        }
        void error(String msg, Object... args) {
            if (logger.isLoggable(Level.SEVERE)) { logger.log(Level.SEVERE, format(msg, args)); }
        }
        private String format(String msg, Object... args) {
            if (msg == null || args == null || args.length == 0) { return msg; }
            StringBuilder sb = new StringBuilder();
            int idx = 0;
            int ai = 0;
            while (idx < msg.length()) {
                int p = msg.indexOf("{}", idx);
                if (p < 0) { sb.append(msg.substring(idx)); break; }
                sb.append(msg, idx, p);
                if (ai < args.length) {
                    sb.append(String.valueOf(args[ai++]));
                } else {
                    sb.append("{}");
                }
                idx = p + 2;
            }
            if (ai < args.length) {
                sb.append(" ");
                for (int i = ai; i < args.length; i++) {
                    if (i > ai) { sb.append(", "); }
                    sb.append(String.valueOf(args[i]));
                }
            }
            return sb.toString();
        }
    }

    //
    // Attributes
    //
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private String baseUrl;

    //
    // Constructor(s)
    //
    public MainRESTClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.baseUrl = baseUrl;
    }

    //
     // Bean Methods
    //

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    //
     // Business Methods
    //

    public List<ApplicationComponent> listComponents() {
        String url = normalize(baseUrl) + "/oam/applicationcomponent";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}, body={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length(), resp.body());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponent>>(){});
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return Collections.emptyList();
    }

    public List<ApplicationComponent> listSubcomponents(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/subcomponents";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return Collections.emptyList();
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponent>>(){});
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return Collections.emptyList();
    }

    public ApplicationComponentMetricsData getLatestMetrics(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/oam/metrics/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null && !resp.body().isBlank()) {
                return mapper.readValue(resp.body(), ApplicationComponentMetricsData.class);
            } else if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                LOG.warn("[UI] GET {} returned empty body", url);
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return null;
    }

    public List<java.util.Map<String,Object>> listPathways() {
        String url = normalize(baseUrl) + "/api/pathway";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<List<java.util.Map<String,Object>>>(){});
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public Pathway getPathway(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), Pathway.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return null;
    }

    public Pathway createPathway(Pathway pathway) {
        String url = normalize(baseUrl) + "/api/pathway";
        try {
            String body = mapper.writeValueAsString(pathway);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), Pathway.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        return null;
    }

    public Pathway updatePathway(Pathway pathway) {
        String id = pathway.resolveKey();
        if (id == null) { return null; }
        String url;
        try { url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id); } catch (IOException e) { return null; }
        try {
            String body = mapper.writeValueAsString(pathway);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), Pathway.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        return null;
    }

    public boolean deletePathway(String id) {
        String url;
        try { url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id); } catch (IOException e) { return false; }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
            return false;
        }
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id) + "/routes";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return java.util.Collections.emptyMap();
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                try {
                    java.util.List<PathwayRoute> list = mapper.readValue(resp.body(), new TypeReference<java.util.List<PathwayRoute>>(){});
                    java.util.Map<Integer, PathwayRoute> map = new java.util.LinkedHashMap<>();
                    int i = 1;
                    for (PathwayRoute s : list) { map.put(i++, s); }
                    return map;
                } catch (Exception e) {
                    // Older format
                    return mapper.readValue(resp.body(), new TypeReference<java.util.Map<Integer, PathwayRoute>>(){});
                }
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyMap();
    }

    public PathwayRoute getSegmentById(String id){
        String url;
        try {
            url = normalize(baseUrl) + "/api/route/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayRoute.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return null;
    }

    public PathwayRoute createPathwayRoute(String pathwayId, PathwayRoute route){
        String url;
        try { url = normalize(baseUrl) + "/api/pathway/" + urlEncode(pathwayId) + "/routes"; } catch (IOException e) { return null; }
        try {
            String body = mapper.writeValueAsString(route);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayRoute.class);
            }
        } catch (Exception e){
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        return null;
    }

    public PathwayRoute updatePathwayRoute(PathwayRoute route){
        String id = route.getObjectId().getKeyValue();
        if(id==null){ return null; }
        String url;
        try { url = normalize(baseUrl) + "/api/route/" + urlEncode(id); } catch (IOException e) { return null; }
        try {
            String body = mapper.writeValueAsString(route);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayRoute.class);
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        return null;
    }

    public boolean deletePathwayRoute(String id){
        String url;
        try { url = normalize(baseUrl) + "/api/route/" + urlEncode(id); } catch (IOException e) { return false; }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
            return false;
        }
    }

    public PathwayRouteSegment getPathById(String id){
        String url;
        try {
            url = normalize(baseUrl) + "/api/segment/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayRouteSegment.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return null;
    }

    public PathwayRouteSegment updatePathwayRouteSegment(PathwayRouteSegment segment){
        String id = segment.getObjectId().getKeyValue();
        if(id==null){ return null; }
        String url;
        try { url = normalize(baseUrl) + "/api/segment/" + urlEncode(id); } catch (IOException e) { return null; }
        try {
            String body = mapper.writeValueAsString(segment);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayRouteSegment.class);
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        return null;
    }

    public boolean deletePathwayRouteSegment(String id){
        String url;
        try { url = normalize(baseUrl) + "/api/segment/" + urlEncode(id); } catch (IOException e) { return false; }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
            return false;
        }
    }

    public java.util.List<PathwayElement> listPathwayElements(){
        String url = normalize(baseUrl) + "/api/pathwayelement";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), new TypeReference<java.util.List<PathwayElement>>(){});
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public PathwayElement getFlowById(String id){
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayElement.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return null;
    }

    public static String resolveKey(ApplicationComponent s) {
        String key = s.resolveKey();
        return(key);
    }

    private static String normalize(String base) {
        if (base == null || base.isEmpty()) return "";
        return base.endsWith("/") ? base.substring(0, base.length()-1) : base;
    }

    // Resolve REST key from DistributableObjectId using QualifiedName.CommonName.value
    private static String resolveRestKey(ObjectId id) {
        if (id == null) { return null; }
        try {
            return id.getKeyValue();
        } catch (Exception e) {
            LOG.warn("[UI] resolveRestKey(): Failed to resolve key from {}: {}", id, e.toString());
            return null;
        }
    }

    public List<ApplicationComponent> listSubcomponents(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return Collections.emptyList(); }
        return listSubcomponents(id.getKeyValue());
    }

    public ApplicationComponentMetricsData getLatestMetrics(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return null; }
        return getLatestMetrics(id.getKeyValue());
    }

    public Pathway getPathway(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return null; }
        return getPathway(id.getKeyValue());
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return java.util.Collections.emptyMap(); }
        return getPathwaySegments(id.getKeyValue());
    }

    public PathwayRoute getSegmentById(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return null; }
        return getSegmentById(id.getKeyValue());
    }

    public PathwayRouteSegment getPathById(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return null; }
        return getPathById(id.getKeyValue());
    }

    public PathwayElement getFlowById(ObjectKey id) {
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return null; }
        return getFlowById(id.getKeyValue());
    }

    // Overloads that accept DistributableObjectId and use CommonName as REST key
    public List<ApplicationComponent> listSubcomponents(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return Collections.emptyList(); }
        return listSubcomponents(key);
    }

    public ApplicationComponentMetricsData getLatestMetrics(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getLatestMetrics(key);
    }

    public Pathway getPathway(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getPathway(key);
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return java.util.Collections.emptyMap(); }
        return getPathwaySegments(key);
    }

    public PathwayRoute getSegmentById(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getSegmentById(key);
    }

    public PathwayRouteSegment getPathById(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getPathById(key);
    }

    public PathwayElement getFlowById(ObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getFlowById(key);
    }

    public PathwayElement createFlow(PathwayElement flow){
        String url = normalize(baseUrl) + "/api/pathwayelement";
        try {
            String body = mapper.writeValueAsString(flow);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayElement.class);
            }
        } catch (Exception e) {
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        return null;
    }

    public PathwayElement updateFlow(PathwayElement flow){
        String id = resolveRestKey(flow==null? null : flow.getObjectId());
        if(id==null){ return null; }
        String url;
        try { url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id); } catch (IOException e) { return null; }
        try {
            String body = mapper.writeValueAsString(flow);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                return mapper.readValue(resp.body(), PathwayElement.class);
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        return null;
    }

    public boolean deleteFlow(String id){
        String url;
        try { url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id); } catch (IOException e) { return false; }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
            return false;
        }
    }

    private static String urlEncode(String s) throws IOException {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // --- Interfaces for a component ---
    public List<WUPInterfaceBase> listInterfaces(String id) {
        List<WUPInterfaceBase> interfaceList = new ArrayList<>();
        interfaceList.addAll(listIngressInterfaces(id));
        interfaceList.addAll(listEgressInterfaces(id));
        return interfaceList;
    }

    public List<IngresApplicationInterface> listIngressInterfaces(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/ingressinterfaces";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return java.util.Collections.emptyList();
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                List<IngresApplicationInterface> ingressInterfaces = mapper.readValue(resp.body(), new TypeReference<java.util.List<IngresApplicationInterface>>(){});
                return ingressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public List<EgressApplicationInterface> listEgressInterfaces(String id) {
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/egressinterfaces";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            return java.util.Collections.emptyList();
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                List<EgressApplicationInterface> egressInterfaces = mapper.readValue(resp.body(), new TypeReference<java.util.List<EgressApplicationInterface>>(){});
                return egressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public List<WUPInterfaceBase> listInterfaces(ObjectKey id){
        if (id == null || id.getKeyValue() == null || id.getKeyValue().isBlank()) { return java.util.Collections.emptyList(); }
        return listInterfaces(id.getKeyValue());
    }

    public List<WUPInterfaceBase> listInterfaces(ObjectId id){
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return java.util.Collections.emptyList(); }
        return listInterfaces(key);
    }
}
