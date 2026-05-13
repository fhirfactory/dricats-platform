/*
 * Copyright (c) 2026 Mark A. Hunter
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
package net.fhirfactory.dricats.components.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import org.apache.camel.model.RouteDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class SingleInOutEndpoint extends WorkUnitProcessor {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(SingleInOutEndpoint.class);

    //
    // Attributes
    //


    private RouteDefinition camelRoute;
    private EgressApplicationInterface egressEndpoint;
    private IngresApplicationInterface ingresEndpoint;

    //
    // Constructor(s)
    //

    public SingleInOutEndpoint(){
        super();
    }

    public SingleInOutEndpoint(ElementReference parent, String name, String documentation){
        super(parent, name, documentation);
    }

    //
    // Methods
    //

    public void setEgressEndpoint(EgressApplicationInterface egressEndpoint){
        this.egressEndpoint = egressEndpoint;
        clearEgressInterfaces();
        addEgressInterface(egressEndpoint);
    }

    public EgressApplicationInterface getEgressEndpoint(){
        return this.egressEndpoint;
    }

    public void setIngresEndpoint(IngresApplicationInterface ingresEndpoint){
        this.ingresEndpoint = ingresEndpoint;
        clearIngresInterfaces();
        addIngresInterface(ingresEndpoint);
    }

    public IngresApplicationInterface getIngresEndpoint(){
        return this.ingresEndpoint;
    }



}
