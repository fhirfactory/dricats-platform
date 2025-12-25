/*
 * Copyright (c) 2025 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.ui.serverside.rest.api.topology;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Solution;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.rest.api.base.ResourceAPIBase;
import net.fhirfactory.dricats.ui.serverside.rest.handlers.ApplicationComponentHandler;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ApplicationComponentRoutes extends ResourceAPIBase {
        private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponentRoutes.class);

        @Inject
        private ApplicationComponentHandler serviceHandler;

        @Override
        protected Logger getLogger(){return(LOG);}

        @Override
        protected String getResourceName() {
            return (ApplicationComponent.class.getSimpleName());
        }

        @Override
        protected Class getResourceClass() {
            return (ApplicationComponent.class);
        }

        @Override
        protected String getResourceCollectionPath() {
            return ("/oam/topology");
        }

        @Override
        public void configure() throws Exception {

            //
            // Camel REST Configuration
            //

            getRestConfigurationDefinition();

            //
            // The PractitionerRoleDirectory Resource Handler (Exceptions)
            //

            getResourceNotFoundException();
            getResourceUpdateException();
            getJsonParseException();
            getGeneralException();

            //
            // The PractitionerESR Resource Handler
            //

            getRestGetDefinition()
                    .get("/search?shortName={shortName}&longName={longName}&displayName={displayName}"
                            + "&pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                    .param().name("unqualifiedName").type(RestParamType.query).required(false).endParam()
                    .param().name("commonName").type(RestParamType.query).required(false).endParam()
                    .param().name("qualifiedName").type(RestParamType.query).required(false).endParam()
                    .param().name("displayName").type(RestParamType.query).required(false).endParam()
                    .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                    .param().name("page").type(RestParamType.query).required(false).endParam()
                    .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                    .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                    .to("direct:"+getResourceName()+"SearchGET")
                    .post().type(Solution.class)
                    .to("direct:"+getResourceName()+"POST")
                    .put().type(Solution.class)
                    .to("direct:"+getResourceName()+"PUT");


            from("direct:"+getResourceName()+"GET")
                    .bean(serviceHandler, "getResource")
                    .log(LoggingLevel.INFO, "GET Request --> ${body}");

            from("direct:"+getResourceName()+"ListGET")
                    .bean(serviceHandler, "defaultGetResourceList")
                    .log(LoggingLevel.INFO, "GET Request --> ${body}");

            from("direct:"+getResourceName()+"SearchGET")
                    .bean(serviceHandler, "defaultSearch")
                    .log(LoggingLevel.INFO, "GET (Search) Request --> ${body}");

            from("direct:"+getResourceName()+"POST")
                    .log(LoggingLevel.INFO, "POST Request --> ${body}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                    .setBody(simple("Action not support for this Directory Entry"));

            from("direct:"+getResourceName()+"PUT")
                    .log(LoggingLevel.INFO, "PUT Request --> ${body}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                    .setBody(simple("Action not support for this Directory Entry"));

            from("direct:"+getResourceName()+"DELETE")
                    .log(LoggingLevel.INFO, "DELETE Request --> ${body}")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(501))
                    .setBody(simple("Action not support for this Directory Entry"));
        }
}
