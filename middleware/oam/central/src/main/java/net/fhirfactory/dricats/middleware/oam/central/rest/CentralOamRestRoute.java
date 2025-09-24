package net.fhirfactory.dricats.middleware.oam.central.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.context.ApplicationScoped;

/**
 * Camel REST configuration and routes for OAM Central.
 *
 * Uses a common base path "/oam" and separates route handlers for
 * application components and metrics via different direct: routes.
 */
@ApplicationScoped
public class CentralOamRestRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Global REST configuration using Undertow and Jackson
        restConfiguration()
                .component("undertow")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "DRICaTS OAM Central API")
                .apiProperty("api.version", "1.0");

        // Common base path
        rest("/oam")
                // ---------------- Application Components -----------------
                .get("/applicationcomponents")
                    .to("direct:list-components")
                .get("/applicationcomponents/{id}")
                    .to("direct:get-component")
                .get("/applicationcomponents/{id}/subcomponents")
                    .to("direct:get-subcomponents")
                // ---------------- Metrics ---------------------------------
                .get("/metrics")
                    .to("direct:get-metrics-range")
                .get("/metrics/{id}")
                    .description("Get latest metrics for component id")
                    .to("direct:get-metrics-latest");

        // Handlers for application components
        from("direct:list-components")
                .bean(CentralOamService.class, "listComponents");

        from("direct:get-component")
                .bean(CentralOamService.class, "getComponent(${header.id})");

        from("direct:get-subcomponents")
                .bean(CentralOamService.class, "getSubComponents(${header.id})");

        // Handlers for metrics
        from("direct:get-metrics-range")
                .bean(CentralOamService.class, "getMetricsInRange(${header.start}, ${header.end})");

        from("direct:get-metrics-latest")
                .bean(CentralOamService.class, "getLatestMetricsForComponent(${header.id})");
    }
}
