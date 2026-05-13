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
import net.fhirfactory.dricats.internals.common.id.ElementInstanceId;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
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

    public String getBaseUrl() {
        LOG.debug(".getBaseUrl(): [Entry]");
        LOG.debug(".getBaseUrl(): [Exit]");
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        LOG.debug(".setBaseUrl(): [Entry] baseUrl={}", baseUrl);
        this.baseUrl = baseUrl;
        LOG.debug(".setBaseUrl(): [Exit]");
    }

    //
     // Business Methods
    //

    public List<ApplicationComponent> listComponents() {
        LOG.debug(".listComponents(): [Entry]");
        String url = normalize(baseUrl) + "/oam/applicationcomponent";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}, body={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length(), resp.body());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                LOG.info("[UI].listComponents(): extracting objects from JSON files: [Start]");
                List<ApplicationComponent> applicationComponentList = mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponent>>(){});
                LOG.info("[UI].listComponents(): extracting objects from JSON files: [End]");
                LOG.trace(".listComponents(): applicationComponentList={}", applicationComponentList);
                LOG.debug(".listComponents(): [Exit]");
                return applicationComponentList;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listComponents(): [Exit]");
        return Collections.emptyList();
    }

    public List<ApplicationComponent> listSubcomponents(String id) {
        LOG.debug(".listSubcomponents(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/subcomponents";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".listSubcomponents(): [Exit]");
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
                List<ApplicationComponent> list = mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponent>>(){});
                LOG.trace(".listSubcomponents(): list={}", list);
                LOG.debug(".listSubcomponents(): [Exit]");
                return list;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listSubcomponents(): [Exit]");
        return Collections.emptyList();
    }

    public ApplicationComponentMetricsData getLatestMetrics(String id) {
        LOG.debug(".getLatestMetrics(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/oam/metrics/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getLatestMetrics(): [Exit]");
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
                ApplicationComponentMetricsData metricsData = mapper.readValue(resp.body(), ApplicationComponentMetricsData.class);
                LOG.trace(".getLatestMetrics(): metricsData={}", metricsData);
                LOG.debug(".getLatestMetrics(): [Exit]");
                return metricsData;
            } else if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                LOG.warn("[UI] GET {} returned empty body", url);
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getLatestMetrics(): [Exit]");
        return null;
    }

    public List<Pathway> listPathways() {
        LOG.debug(".listPathways(): [Entry]");
        String url = normalize(baseUrl) + "/api/pathway";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                List<Pathway> list = mapper.readValue(resp.body(), new TypeReference<List<Pathway>>() {
                });
                LOG.trace(".listPathways(): list={}", list);
                LOG.debug(".listPathways(): [Exit]");
                return list;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listPathways(): [Exit]");
        return java.util.Collections.emptyList();
    }

    public Pathway getPathway(String id) {
        LOG.debug(".getPathway(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getPathway(): [Exit]");
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                Pathway pathway = mapper.readValue(resp.body(), Pathway.class);
                LOG.trace(".getPathway(): pathway={}", pathway);
                LOG.debug(".getPathway(): [Exit]");
                return pathway;
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getPathway(): [Exit]");
        return null;
    }

    public Pathway createPathway(Pathway pathway) {
        LOG.debug(".createPathway(): [Entry] pathway={}", pathway);
        String url = normalize(baseUrl) + "/api/pathway";
        try {
            String body = mapper.writeValueAsString(pathway);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                Pathway created = mapper.readValue(resp.body(), Pathway.class);
                LOG.trace(".createPathway(): created={}", created);
                LOG.debug(".createPathway(): [Exit]");
                return created;
            }
        } catch (Exception e) {
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        LOG.debug(".createPathway(): [Exit]");
        return null;
    }

    public Pathway updatePathway(Pathway pathway) {
        LOG.debug(".updatePathway(): [Entry] pathway={}", pathway);
        String id = pathway.resolveElementInstanceKey();
        if (id == null) {
            LOG.debug(".updatePathway(): [Exit] id is null");
            return null;
        }
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".updatePathway(): [Exit] urlEncode failed");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(pathway);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                Pathway updated = mapper.readValue(resp.body(), Pathway.class);
                LOG.trace(".updatePathway(): updated={}", updated);
                LOG.debug(".updatePathway(): [Exit]");
                return updated;
            }
        } catch (Exception e) {
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        LOG.debug(".updatePathway(): [Exit]");
        return null;
    }

    public boolean deletePathway(String id) {
        LOG.debug(".deletePathway(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".deletePathway(): [Exit] urlEncode failed");
            return false;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            boolean success = resp.statusCode() >= 200 && resp.statusCode() < 300;
            LOG.debug(".deletePathway(): [Exit] success={}", success);
            return success;
        } catch (Exception e) {
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
            LOG.debug(".deletePathway(): [Exit] error");
            return false;
        }
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(String id) {
        LOG.debug(".getPathwaySegments(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(id) + "/routes";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getPathwaySegments(): [Exit]");
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
                    LOG.trace(".getPathwaySegments(): map={}", map);
                    LOG.debug(".getPathwaySegments(): [Exit]");
                    return map;
                } catch (Exception e) {
                    // Older format
                    java.util.Map<Integer, PathwayRoute> map = mapper.readValue(resp.body(), new TypeReference<java.util.Map<Integer, PathwayRoute>>(){});
                    LOG.trace(".getPathwaySegments(): map (old format)={}", map);
                    LOG.debug(".getPathwaySegments(): [Exit]");
                    return map;
                }
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getPathwaySegments(): [Exit]");
        return java.util.Collections.emptyMap();
    }

    public PathwayRoute getSegmentById(String id){
        LOG.debug(".getSegmentById(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/route/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getSegmentById(): [Exit]");
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayRoute route = mapper.readValue(resp.body(), PathwayRoute.class);
                LOG.trace(".getSegmentById(): route={}", route);
                LOG.debug(".getSegmentById(): [Exit]");
                return route;
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getSegmentById(): [Exit]");
        return null;
    }

    public PathwayRoute createPathwayRoute(String pathwayId, PathwayRoute route){
        LOG.debug(".createPathwayRoute(): [Entry] pathwayId={}, route={}", pathwayId, route);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathway/" + urlEncode(pathwayId) + "/routes";
        } catch (IOException e) {
            LOG.debug(".createPathwayRoute(): [Exit] urlEncode failed");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(route);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayRoute created = mapper.readValue(resp.body(), PathwayRoute.class);
                LOG.trace(".createPathwayRoute(): created={}", created);
                LOG.debug(".createPathwayRoute(): [Exit]");
                return created;
            }
        } catch (Exception e){
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        LOG.debug(".createPathwayRoute(): [Exit]");
        return null;
    }

    public PathwayRoute updatePathwayRoute(PathwayRoute route){
        LOG.debug(".updatePathwayRoute(): [Entry] route={}", route);
        String id = route.resolveElementInstanceKey();
        if(id==null){
            LOG.debug(".updatePathwayRoute(): [Exit] id is null");
            return null;
        }
        String url;
        try {
            url = normalize(baseUrl) + "/api/route/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".updatePathwayRoute(): [Exit] urlEncode failed");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(route);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayRoute updated = mapper.readValue(resp.body(), PathwayRoute.class);
                LOG.trace(".updatePathwayRoute(): updated={}", updated);
                LOG.debug(".updatePathwayRoute(): [Exit]");
                return updated;
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        LOG.debug(".updatePathwayRoute(): [Exit]");
        return null;
    }

    public boolean deletePathwayRoute(String id){
        LOG.debug(".deletePathwayRoute(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/route/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".deletePathwayRoute(): [Exit] urlEncode failed");
            return false;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            boolean success = resp.statusCode() >= 200 && resp.statusCode() < 300;
            LOG.debug(".deletePathwayRoute(): [Exit] success={}", success);
            return success;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
        }
        LOG.debug(".deletePathwayRoute(): [Exit]");
        return false;
    }

    public PathwayRouteSegment getPathById(String id){
        LOG.debug(".getPathById(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/segment/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getPathById(): [Exit]");
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayRouteSegment segment = mapper.readValue(resp.body(), PathwayRouteSegment.class);
                LOG.trace(".getPathById(): segment={}", segment);
                LOG.debug(".getPathById(): [Exit]");
                return segment;
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getPathById(): [Exit]");
        return null;
    }

    public PathwayRouteSegment updatePathwayRouteSegment(PathwayRouteSegment segment){
        LOG.debug(".updatePathwayRouteSegment(): [Entry] segment={}", segment);
        String id = segment.resolveElementInstanceKey();
        if(id==null){
            LOG.debug(".updatePathwayRouteSegment(): [Exit] id is null");
            return null;
        }
        String url;
        try {
            url = normalize(baseUrl) + "/api/segment/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".updatePathwayRouteSegment(): [Exit] urlEncode failed");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(segment);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayRouteSegment updated = mapper.readValue(resp.body(), PathwayRouteSegment.class);
                LOG.trace(".updatePathwayRouteSegment(): updated={}", updated);
                LOG.debug(".updatePathwayRouteSegment(): [Exit]");
                return updated;
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        LOG.debug(".updatePathwayRouteSegment(): [Exit]");
        return null;
    }

    public boolean deletePathwayRouteSegment(String id){
        LOG.debug(".deletePathwayRouteSegment(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/segment/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".deletePathwayRouteSegment(): [Exit] urlEncode failed");
            return false;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            boolean success = resp.statusCode() >= 200 && resp.statusCode() < 300;
            LOG.debug(".deletePathwayRouteSegment(): [Exit] success={}", success);
            return success;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
        }
        LOG.debug(".deletePathwayRouteSegment(): [Exit]");
        return false;
    }

    public java.util.List<PathwayElement> listPathwayElements(){
        LOG.debug(".listPathwayElements(): [Entry]");
        String url = normalize(baseUrl) + "/api/pathwayelement";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                java.util.List<PathwayElement> list = mapper.readValue(resp.body(), new TypeReference<java.util.List<PathwayElement>>(){});
                LOG.trace(".listPathwayElements(): list={}", list);
                LOG.debug(".listPathwayElements(): [Exit]");
                return list;
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listPathwayElements(): [Exit]");
        return java.util.Collections.emptyList();
    }

    public PathwayElement getFlowById(String id){
        LOG.debug(".getFlowById(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id);
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".getFlowById(): [Exit]");
            return null;
        }
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        try {
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayElement flow = mapper.readValue(resp.body(), PathwayElement.class);
                LOG.trace(".getFlowById(): flow={}", flow);
                LOG.debug(".getFlowById(): [Exit]");
                return flow;
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".getFlowById(): [Exit]");
        return null;
    }

    public static String resolveKey(ApplicationComponent s) {
        LOG.debug(".resolveKey(): [Entry] s={}", s);
        String key = s.resolveElementInstanceKey();
        LOG.debug(".resolveKey(): [Exit] key={}", key);
        return(key);
    }

    private static String normalize(String base) {
        LOG.debug(".normalize(): [Entry] base={}", base);
        if (base == null || base.isEmpty()) {
            LOG.debug(".normalize(): [Exit] base is null or empty");
            return "";
        }
        String normalized = base.endsWith("/") ? base.substring(0, base.length()-1) : base;
        LOG.debug(".normalize(): [Exit] normalized={}", normalized);
        return normalized;
    }

    // Resolve REST key from DistributableObjectId using QualifiedName.CommonName.value
    private static String resolveRestKey(ElementInstanceId id) {
        LOG.debug(".resolveRestKey(): [Entry] id={}", id);
        if (id == null) {
            LOG.debug(".resolveRestKey(): [Exit] id is null");
            return null;
        }
        try {
            String key = id.resolveKey();
            LOG.debug(".resolveRestKey(): [Exit] key={}", key);
            return key;
        } catch (Exception e) {
            LOG.warn("[UI] resolveRestKey(): Failed to resolve key from {}: {}", id, e.toString());
            LOG.debug(".resolveRestKey(): [Exit] failed");
            return null;
        }
    }

    public List<ApplicationComponent> listSubcomponents(ElementInstanceId id) {
        LOG.debug(".listSubcomponents(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".listSubcomponents(): [Exit] id is null or invalid");
            return Collections.emptyList();
        }
        List<ApplicationComponent> list = listSubcomponents(id.resolveKey());
        LOG.debug(".listSubcomponents(): [Exit]");
        return list;
    }

    public ApplicationComponentMetricsData getLatestMetrics(ElementInstanceId id) {
        LOG.debug(".getLatestMetrics(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getLatestMetrics(): [Exit] id is null or invalid");
            return null;
        }
        ApplicationComponentMetricsData metrics = getLatestMetrics(id.resolveKey());
        LOG.debug(".getLatestMetrics(): [Exit]");
        return metrics;
    }

    public Pathway getPathway(ElementInstanceId id) {
        LOG.debug(".getPathway(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getPathway(): [Exit] id is null or invalid");
            return null;
        }
        Pathway pathway = getPathway(id.resolveKey());
        LOG.debug(".getPathway(): [Exit]");
        return pathway;
    }

    public java.util.Map<Integer, PathwayRoute> getPathwaySegments(ElementInstanceId id) {
        LOG.debug(".getPathwaySegments(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getPathwaySegments(): [Exit] id is null or invalid");
            return Collections.emptyMap();
        }
        java.util.Map<Integer, PathwayRoute> segments = getPathwaySegments(id.resolveKey());
        LOG.debug(".getPathwaySegments(): [Exit]");
        return segments;
    }

    public PathwayRoute getSegmentById(ElementInstanceId id) {
        LOG.debug(".getSegmentById(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getSegmentById(): [Exit] id is null or invalid");
            return null;
        }
        PathwayRoute route = getSegmentById(id.resolveKey());
        LOG.debug(".getSegmentById(): [Exit]");
        return route;
    }

    public PathwayRouteSegment getPathById(ElementInstanceId id) {
        LOG.debug(".getPathById(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getPathById(): [Exit] id is null or invalid");
            return null;
        }
        PathwayRouteSegment path = getPathById(id.resolveKey());
        LOG.debug(".getPathById(): [Exit]");
        return path;
    }

    public PathwayElement getFlowById(ElementInstanceId id) {
        LOG.debug(".getFlowById(): [Entry] id={}", id);
        if (id == null || id.resolveKey() == null || id.resolveKey().isBlank()) {
            LOG.debug(".getFlowById(): [Exit] id is null or invalid");
            return null;
        }
        PathwayElement flow = getFlowById(id.resolveKey());
        LOG.debug(".getFlowById(): [Exit]");
        return flow;
    }

    public PathwayElement createFlow(PathwayElement flow){
        LOG.debug(".createFlow(): [Entry] flow={}", flow);
        String url = normalize(baseUrl) + "/api/pathwayelement";
        try {
            String body = mapper.writeValueAsString(flow);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayElement created = mapper.readValue(resp.body(), PathwayElement.class);
                LOG.trace(".createFlow(): created={}", created);
                LOG.debug(".createFlow(): [Exit]");
                return created;
            }
        } catch (Exception e) {
            LOG.error("[UI] POST {} failed: {}", url, e.toString());
        }
        LOG.debug(".createFlow(): [Exit]");
        return null;
    }

    public PathwayElement updateFlow(PathwayElement flow){
        LOG.debug(".updateFlow(): [Entry] flow={}", flow);
        String id = resolveRestKey(flow==null? null : flow.getElementInstanceId());
        if(id==null){
            LOG.debug(".updateFlow(): [Exit] id is null");
            return null;
        }
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".updateFlow(): [Exit] urlEncode failed");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(flow);
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null) {
                PathwayElement updated = mapper.readValue(resp.body(), PathwayElement.class);
                LOG.trace(".updateFlow(): updated={}", updated);
                LOG.debug(".updateFlow(): [Exit]");
                return updated;
            }
        } catch (Exception e){
            LOG.error("[UI] PUT {} failed: {}", url, e.toString());
        }
        LOG.debug(".updateFlow(): [Exit]");
        return null;
    }

    public boolean deleteFlow(String id){
        LOG.debug(".deleteFlow(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/api/pathwayelement/" + urlEncode(id);
        } catch (IOException e) {
            LOG.debug(".deleteFlow(): [Exit] urlEncode failed");
            return false;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            boolean success = resp.statusCode() >= 200 && resp.statusCode() < 300;
            LOG.debug(".deleteFlow(): [Exit] success={}", success);
            return success;
        } catch (Exception e){
            LOG.error("[UI] DELETE {} failed: {}", url, e.toString());
        }
        LOG.debug(".deleteFlow(): [Exit]");
        return false;
    }

    private static String urlEncode(String s) throws IOException {
        LOG.debug(".urlEncode(): [Entry] s={}", s);
        String encoded = java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
        LOG.debug(".urlEncode(): [Exit] encoded={}", encoded);
        return encoded;
    }

    // --- Interfaces for a component ---
    public List<WUPInterfaceBase> listInterfaces(String id) {
        LOG.debug(".listInterfaces(): [Entry] id={}", id);
        List<WUPInterfaceBase> interfaceList = new ArrayList<>();
        interfaceList.addAll(listIngressInterfaces(id));
        interfaceList.addAll(listEgressInterfaces(id));
        LOG.trace(".listInterfaces(): interfaceList={}", interfaceList);
        LOG.debug(".listInterfaces(): [Exit]");
        return interfaceList;
    }

    public List<IngresApplicationInterface> listIngressInterfaces(String id) {
        LOG.debug(".listIngressInterfaces(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/ingressinterfaces";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".listIngressInterfaces(): [Exit]");
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
                LOG.trace(".listIngressInterfaces(): ingressInterfaces={}", ingressInterfaces);
                LOG.debug(".listIngressInterfaces(): [Exit]");
                return ingressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listIngressInterfaces(): [Exit]");
        return java.util.Collections.emptyList();
    }

    public List<EgressApplicationInterface> listEgressInterfaces(String id) {
        LOG.debug(".listEgressInterfaces(): [Entry] id={}", id);
        String url;
        try {
            url = normalize(baseUrl) + "/oam/applicationcomponent/" + urlEncode(id) + "/egressinterfaces";
        } catch (IOException e) {
            LOG.error("[UI] urlEncode failed for id={}: {}", id, e.toString());
            LOG.debug(".listEgressInterfaces(): [Exit]");
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
                LOG.trace(".listEgressInterfaces(): egressInterfaces={}", egressInterfaces);
                LOG.debug(".listEgressInterfaces(): [Exit]");
                return egressInterfaces;
            } else {
                LOG.warn("[UI] GET {} returned non-success status {}", url, resp.statusCode());
            }
        } catch (Exception e) {
            LOG.error("[UI] GET {} failed: {}", url, e.toString());
        }
        LOG.debug(".listEgressInterfaces(): [Exit]");
        return java.util.Collections.emptyList();
    }
}
