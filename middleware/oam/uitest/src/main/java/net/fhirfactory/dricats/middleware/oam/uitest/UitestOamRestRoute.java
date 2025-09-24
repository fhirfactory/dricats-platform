package net.fhirfactory.dricats.middleware.oam.uitest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * Camel REST routes that mirror the dricats-middleware-oam-central API, but
 * backed by an in-memory stub service for UI testing.
 */
public class UitestOamRestRoute extends RouteBuilder {
    @Override
    public void configure() {
        // Configure REST with Undertow + Jackson JSON binding
        restConfiguration()
                .component("undertow")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "DRICaTS OAM UI Test API")
                .apiProperty("api.version", "1.0");

        // Define REST endpoints identical to central module
        rest("/oam")
                // Application Components
                .get("/applicationcomponents").to("direct:uitest-list-components")
                .get("/applicationcomponents/{id}").to("direct:uitest-get-component")
                .get("/applicationcomponents/{id}/subcomponents").to("direct:uitest-get-subcomponents")
                // Metrics
                .get("/metrics").to("direct:uitest-get-metrics-range")
                .get("/metrics/{id}").to("direct:uitest-get-metrics-latest");

        // Wire to service beans (constructed directly here for simplicity)
        from("direct:uitest-list-components")
                .bean(UitestOamService.class, "listComponents");

        from("direct:uitest-get-component")
                .bean(UitestOamService.class, "getComponent(${header.id})");

        from("direct:uitest-get-subcomponents")
                .bean(UitestOamService.class, "getSubComponents(${header.id})");

        from("direct:uitest-get-metrics-range")
                .bean(UitestOamService.class, "getMetricsInRange(${header.start}, ${header.end})");

        from("direct:uitest-get-metrics-latest")
                .bean(UitestOamService.class, "getLatestMetricsForComponent(${header.id})");
    }
}
