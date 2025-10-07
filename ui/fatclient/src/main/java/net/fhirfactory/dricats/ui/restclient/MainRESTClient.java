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
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummaryList;
import net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.common.naming.IdToken;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MainRESTClient {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(MainRESTClient.class);

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

    public ApplicationComponentSummaryList listComponents() {
        String url = normalize(baseUrl) + "/oam/applicationcomponent";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}, body={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length(), resp.body());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<ApplicationComponentSummaryList>(){});
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return new ApplicationComponentSummaryList();
    }

    public List<ApplicationComponentSummary> listSubcomponents(String id) {
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
                return mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponentSummary>>(){});
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
        String id = resolveRestKey(pathway==null? null : pathway.getObjectID());
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
        String id = resolveRestKey(route==null? null : route.getObjectID());
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
        String id = resolveRestKey(segment==null? null : segment.getObjectID());
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

    public static String resolveKey(ApplicationComponentSummary s) {
        String key = s.resolveKey();
        return(key);
    }

    private static String normalize(String base) {
        if (base == null || base.isEmpty()) return "";
        return base.endsWith("/") ? base.substring(0, base.length()-1) : base;
    }

    // Resolve REST key from DistributableObjectId using QualifiedName.CommonName.value
    private static String resolveRestKey(DistributableObjectId id) {
        if (id == null) { return null; }
        try {
            return id.getQualifiedName().getCommonName().getValue();
        } catch (Exception e) {
            LOG.warn("[UI] resolveRestKey(): Failed to resolve key from {}: {}", id, e.toString());
            return null;
        }
    }

    public List<ApplicationComponentSummary> listSubcomponents(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return Collections.emptyList(); }
        return listSubcomponents(id.getContent());
    }

    public ApplicationComponentMetricsData getLatestMetrics(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return null; }
        return getLatestMetrics(id.getContent());
    }

    public Pathway getPathway(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return null; }
        return getPathway(id.getContent());
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return java.util.Collections.emptyMap(); }
        return getPathwaySegments(id.getContent());
    }

    public PathwayRoute getSegmentById(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return null; }
        return getSegmentById(id.getContent());
    }

    public PathwayRouteSegment getPathById(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return null; }
        return getPathById(id.getContent());
    }

    public PathwayElement getFlowById(IdToken id) {
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return null; }
        return getFlowById(id.getContent());
    }

    // Overloads that accept DistributableObjectId and use CommonName as REST key
    public List<ApplicationComponentSummary> listSubcomponents(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return Collections.emptyList(); }
        return listSubcomponents(key);
    }

    public ApplicationComponentMetricsData getLatestMetrics(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getLatestMetrics(key);
    }

    public Pathway getPathway(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getPathway(key);
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return java.util.Collections.emptyMap(); }
        return getPathwaySegments(key);
    }

    public PathwayRoute getSegmentById(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getSegmentById(key);
    }

    public PathwayRouteSegment getPathById(DistributableObjectId id) {
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return null; }
        return getPathById(key);
    }

    public PathwayElement getFlowById(DistributableObjectId id) {
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
        String id = resolveRestKey(flow==null? null : flow.getObjectID());
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
    public java.util.List<InterfaceComponentSummary> listInterfaces(String id) {
        List<InterfaceComponentSummary> interfaceList = new ArrayList<>();
        interfaceList.addAll(listIngressInterfaces(id));
        interfaceList.addAll(listEgressInterfaces(id));
        return interfaceList;
    }

    public List<IngressInterfaceComponentSummary> listIngressInterfaces(String id) {
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
                List<IngressInterfaceComponentSummary> ingressInterfaces = mapper.readValue(resp.body(), new TypeReference<java.util.List<IngressInterfaceComponentSummary>>(){});
                return ingressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public List<EgressInterfaceComponentSummary> listEgressInterfaces(String id) {
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
                List<EgressInterfaceComponentSummary> egressInterfaces = mapper.readValue(resp.body(), new TypeReference<java.util.List<EgressInterfaceComponentSummary>>(){});
                return egressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        return java.util.Collections.emptyList();
    }

    public java.util.List<InterfaceComponentSummary> listInterfaces(IdToken id){
        if (id == null || id.getContent() == null || id.getContent().isBlank()) { return java.util.Collections.emptyList(); }
        return listInterfaces(id.getContent());
    }

    public java.util.List<InterfaceComponentSummary> listInterfaces(DistributableObjectId id){
        String key = resolveRestKey(id);
        if (key == null || key.isBlank()) { return java.util.Collections.emptyList(); }
        return listInterfaces(key);
    }
}
