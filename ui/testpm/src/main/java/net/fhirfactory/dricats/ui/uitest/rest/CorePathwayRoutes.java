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
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.ui.uitest.handlers.PathwayResourceHandler;
import net.fhirfactory.dricats.ui.uitest.testdata.PathwayTestResourceSetBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class CorePathwayRoutes extends RouteBuilder {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(CorePathwayRoutes.class);

    //
     // Attributes
    //

    private boolean initialized = false;

    @Inject
    private PathwayResourceHandler pathwayResourceHandler;

    @Inject
    private PathwayTestResourceSetBuilder pathwayTestResourceSetBuilder;

    //
     // Constructor(s)
    //

    public CorePathwayRoutes() {
        LOG.debug(".constructor(): Entry");
        LOG.debug(".constructor(): Exit");
    }

    //
    // Post Construct (for dependency injection)
    //

    @PostConstruct
    public void initialize(){
        if(!initialized){
            LOG.info("CorePathwayRoutes:initialize(): Initialising");
            pathwayResourceHandler.initialise();
            pathwayTestResourceSetBuilder.initialise();
            initialized = true;
            LOG.info("CorePathwayRoutes:initialize(): Initialising.... Done!");
        }
    }

    //
    // Accessors and Mutators
    //

    protected Logger getLogger(){
        return LOG;
    }

    protected PathwayResourceHandler getTestPathwayServices() {
        return pathwayResourceHandler;
    }

    //
     // Business Methods
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

        from("direct:uitest-list-pathways").routeId("uitest-list-pathways")
                .log("[UITEST] Listing all pathways. headers=${headers}")
                .bean(pathwayResourceHandler, "listPathwaysAsJSON")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Pathways returned");

        from("direct:uitest-get-pathway").routeId("uitest-get-pathway")
                .log("[UITEST] Get Pathway by id='${header.id}'")
                .bean(pathwayResourceHandler, "getPathwayAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Pathway lookup completed");

        from("direct:uitest-create-pathway").routeId("uitest-create-pathway")
                .log("[UITEST] Create Pathway body='${body}'")
                .bean(pathwayResourceHandler, "createOrUpdatePathway(${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-update-pathway").routeId("uitest-update-pathway")
                .log("[UITEST] Update Pathway id='${header.id}' body='${body}'")
                .bean(pathwayResourceHandler, "createOrUpdatePathway(${body})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-delete-pathway").routeId("uitest-delete-pathway")
                .log("[UITEST] Delete Pathway id='${header.id}'")
                .bean(pathwayResourceHandler, "deletePathway(${header.id})");

        from("direct:uitest-get-pathway-routes").routeId("uitest-get-pathway-routes")
                .log("[UITEST] Get Pathway Routes for id='${header.id}'")
                .bean(pathwayResourceHandler, "getPathwayRoutesAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("[UITEST] Segments lookup completed");

        from("direct:uitest-get-pathway-route-segment").routeId("uitest-get-PathwayRouteSegment")
                .log("[UITEST] Get Segment by id='${header.id}'")
                .bean(pathwayResourceHandler, "getPathwayRouteSegmentAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-update-pathway-route-segment").routeId("uitest-update-PathwayRouteSegment")
                .log("[UITEST] Update Segment id='${header.id}' body='${body}'")
                .bean(pathwayResourceHandler, "createOrUpdatePathwayRouteSegment(${body})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-delete-pathway-route-segment").routeId("uitest-delete-PathwayRouteSegment")
                .log("[UITEST] Delete Segment id='${header.id}'")
                .bean(pathwayResourceHandler, "deletePathwayRouteSegment(${header.id})");

        from("direct:uitest-get-pathway-route").routeId("uitest-get-PathwayRoute")
                .log("[UITEST] Get Path by id='${header.id}'")
                .bean(pathwayResourceHandler, "getPathwayRouteAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-create-pathway-route").routeId("uitest-create-PathwayRoute")
                .log("[UITEST] Create Route for pathway='${header.id}' body='${body}'")
                .bean(pathwayResourceHandler, "createPathwayRoute(${header.id}, ${body})")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-update-pathway-route").routeId("uitest-update-PathwayRoute")
                .log("[UITEST] Update Route id='${header.id}' body='${body}'")
                .bean(pathwayResourceHandler, "createOrUpdatePathwayRoute(${body})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-delete-pathway-route").routeId("uitest-delete-PathwayRoute")
                .log("[UITEST] Delete Route id='${header.id}'")
                .bean(pathwayResourceHandler, "deletePathwayRoute(${header.id})");

        from("direct:uitest-list-pathway-elements").routeId("uitest-list-PathwayElements")
                .log("[UITEST] List all PathwayElements")
                .bean(pathwayResourceHandler, "listPathwayElementsAsJSON")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        from("direct:uitest-get-pathway-element").routeId("uitest-get-PathwayElement")
                .log("[UITEST] Get Flow by id='${header.id}'")
                .bean(pathwayResourceHandler, "getPathwayElementAsJSON(${header.id})")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));
    }
}
