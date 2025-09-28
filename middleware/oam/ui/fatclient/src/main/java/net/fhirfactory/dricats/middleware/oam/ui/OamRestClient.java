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
package net.fhirfactory.dricats.middleware.oam.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummaryList;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OamRestClient {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(OamRestClient.class);

    //
    // Attributes
    //
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private String baseUrl;

    //
    // Constructor(s)
    //
    public OamRestClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
        String url = normalize(baseUrl) + "/oam/applicationcomponents";
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        Instant start = Instant.now();
        try {
            LOG.info("[UI] GET {} - sending", url);
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            Duration d = Duration.between(start, Instant.now());
            LOG.info("[UI] GET {} - status={} duration={}ms bytes={}", url, resp.statusCode(), d.toMillis(), resp.body() == null ? 0 : resp.body().length());
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
            url = normalize(baseUrl) + "/oam/applicationcomponents/" + urlEncode(id) + "/subcomponents";
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
