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
package net.fhirfactory.dricats.middleware.oam.ui.server;

import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.platform.configuration.LocalConfigurationServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OAMBackEndForFrontEndWUP extends WorkUnitProcessor {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(OAMBackEndForFrontEndWUP.class);

    //
     // Attributes
    //

    private RouteBuilder serverRouteBuilder;
    private boolean initialised;

    @Inject
    private OAMBackEndForFrontEndHandler serverHandler;

    @Inject
    private CamelContext camelContext;
    @Inject
    private LocalConfigurationServer localConfigurationServer;

    //
     // Constructor(s)
    //

    public OAMBackEndForFrontEndWUP(){
     super();
     this.initialised = false;
    }

    //
     // Post Construct
    //

    @PostConstruct
    public void postConstruct(){
        LOG.debug("OAMBackEndForFrontEndWUP:postConstruct(): Entry");
        if(!isInitialised()){
            LOG.debug("OAMBackEndForFrontEndWUP:postConstruct(): Initialising");
            this.initialised = true;
            try {
                getCamelContext().addRoutes(new OAMBackEndForFrontEndServer());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        LOG.debug("OAMBackEndForFrontEndWUP:postConstruct(): Exit");
    }

    //
     // Bean Methods
    //

    protected CamelContext getCamelContext() {
        return camelContext;
    }

    protected boolean isInitialised(){
        return(this.initialised);
    }

    //
     // Business Methods
    //

    public void buildRoute(){

    }
}
