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
package net.fhirfactory.dricats.ui.uitest.rest;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.ui.uitest.UitestApplication;
import net.fhirfactory.dricats.ui.uitest.handlers.TestTopologyServices;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Camel REST routes that mirror the dricats-middleware-oam-central API, but
 * backed by an in-memory stub service for UI testing.
 */
@ApplicationScoped
public class TestPMRESTEndpoint extends RouteBuilder {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TestPMRESTEndpoint.class);

    //
    // Attributes
    //
    private boolean initialized = false;

    @Inject
    UitestApplication mainApplication;

    @Inject
    TestTopologyServices testOamService;

    @Inject
    CamelContext camelContext;

    @PostConstruct
    public void initialize(){
        if(!initialized){
            LOG.info("UitestOamRestRoute:initialize(): Initialising");

            LOG.info("UitestOamRestRoute:initialize(): mainApplication.getUiTestServerConfiguration() -> {}", mainApplication.getUiTestServerConfiguration());
            testOamService.initialise();
            try {
                camelContext.addRoutes(this);
                camelContext.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            initialized = true;
            LOG.info("UitestOamRestRoute:initialize(): Initialising.... Done!");
        }
    }

    private void forceEagerInitialization(@Observes Startup startup) {
        LOG.info("UitestOamRestRoute:forceEagerInitialization(): !!!");
    }

    //
     // REST Configuration
    //
    @Override
    public void configure() {
        onException(JsonParseException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody().constant("Invalid json data");

        // Global exception logging for the uitest REST routes
        onException(Exception.class)
                .handled(true)
                .log("[UITEST] Exception handled: ${exception.class} - ${exception.message}")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .logStackTrace(true);

        // Configure REST with Netty-HTTP + Jackson JSON binding
        String host = System.getProperty("camel.rest.host", "localhost");
        int port;
        try {
            port = Integer.parseInt(System.getProperty("camel.rest.port", "12000"));
        } catch (NumberFormatException nfe) {
            port = 12000;
        }
        LOG.info("Configuring REST: component=netty-http host="+host+" port="+port+ " contextPath=/ apiContextPath=/api-doc");

        restConfiguration()
                .component("netty-http")
                .host(host)
                .port(port)
                .scheme("http")
                .bindingMode(RestBindingMode.off)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "DRICaTS OAM UI Test API")
                .apiProperty("api.version", "1.0");

        // Define REST endpoints identical to central module
        rest("/oam")
                // Application Components
                .get("/applicationcomponent").to("direct:uitest-list-components").produces("application/json")
                .get("/applicationcomponent/{id}").to("direct:uitest-get-component")
                .get("/applicationcomponent/{id}/subcomponents").to("direct:uitest-get-subcomponents")
                // Metrics
                .get("/metrics").to("direct:uitest-get-metrics-range")
                .get("/metrics/{id}").to("direct:uitest-get-metrics-latest");


        from("direct:uitest-list-components").routeId("uitest-list-components")
                .log("[UITEST] Listing all application components. headers=${headers}")
                .bean(testOamService, "listComponentsAsJSON")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Components returned");

        from("direct:uitest-get-component").routeId("uitest-get-component")
                .log("[UITEST] Get component by id='${header.id}'")
                .bean(testOamService, "getComponentAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Component lookup completed");

        from("direct:uitest-get-subcomponents").routeId("uitest-get-subcomponents")
                .log("[UITEST] Get subcomponents for id='${header.id}'")
                .bean(testOamService, "getSubComponentsAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Subcomponents lookup completed");

        from("direct:uitest-get-metrics-range").routeId("uitest-get-metrics-range")
                .log("[UITEST] Get metrics range start='${header.start}' end='${header.end}'")
                .bean(testOamService, "getMetricsInRangeAsJSON(${header.start}, ${header.end})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Metrics range lookup completed");

        from("direct:uitest-get-metrics-latest").routeId("uitest-get-metrics-latest")
                .log("[UITEST] Get latest metrics for id='${header.id}'")
                .bean(testOamService, "getLatestMetricsForComponentAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Latest metrics lookup completed");
    }
}
