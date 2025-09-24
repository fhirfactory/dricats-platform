package net.fhirfactory.dricats.middleware.oam.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OamRestClient {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private String baseUrl;

    public OamRestClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<ApplicationComponentSummary> listComponents() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(normalize(baseUrl) + "/oam/applicationcomponents"))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponentSummary>>(){});
            }
        } catch (Exception e) {
            // ignore for minimal UI; return empty list
        }
        return Collections.emptyList();
    }

    public List<ApplicationComponentSummary> listSubcomponents(String id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(normalize(baseUrl) + "/oam/applicationcomponents/" + urlEncode(id) + "/subcomponents"))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(resp.body(), new TypeReference<List<ApplicationComponentSummary>>(){});
            }
        } catch (Exception e) {
            // ignore for minimal UI; return empty list
        }
        return Collections.emptyList();
    }

    public ApplicationComponentMetricsData getLatestMetrics(String id) {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(normalize(baseUrl) + "/oam/metrics/" + urlEncode(id)))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() >= 200 && resp.statusCode() < 300 && resp.body() != null && !resp.body().isBlank()) {
                return mapper.readValue(resp.body(), ApplicationComponentMetricsData.class);
            }
        } catch (Exception e) {
            // ignore; return null for minimal UI
        }
        return null;
    }

    public static String resolveKey(ApplicationComponentSummary s) {
        try {
            CommonName cn = s.getId();
            if (cn != null && cn.getValue() != null && !cn.getValue().isEmpty()) {
                return cn.getValue();
            }
        } catch (Exception ignored) {}
        try {
            DistributableObjectId oid = s.getObjectID();
            if (oid != null && oid.getId() != null && oid.getId().getValue() != null && !oid.getId().getValue().isEmpty()) {
                return oid.getId().getValue();
            }
        } catch (Exception ignored) {}
        return Objects.toString(s.getName(), "");
    }

    private static String normalize(String base) {
        if (base == null || base.isEmpty()) return "";
        return base.endsWith("/") ? base.substring(0, base.length()-1) : base;
    }

    private static String urlEncode(String s) throws IOException {
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
