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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * Camel REST configuration and routes for OAM Central.
 *
 * Uses a common base path "/oam" and separates route handlers for
 * application components and metrics via different direct: routes.
 */

public class OAMBackEndForFrontEndServer extends RouteBuilder {
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
                .bean(OAMBackEndForFrontEndHandler.class, "listComponents");

        from("direct:get-component")
                .bean(OAMBackEndForFrontEndHandler.class, "getComponent(${header.id})");

        from("direct:get-subcomponents")
                .bean(OAMBackEndForFrontEndHandler.class, "getSubComponents(${header.id})");

        // Handlers for metrics
        from("direct:get-metrics-range")
                .bean(OAMBackEndForFrontEndHandler.class, "getMetricsInRange(${header.start}, ${header.end})");

        from("direct:get-metrics-latest")
                .bean(OAMBackEndForFrontEndHandler.class, "getLatestMetricsForComponent(${header.id})");
    }
}
