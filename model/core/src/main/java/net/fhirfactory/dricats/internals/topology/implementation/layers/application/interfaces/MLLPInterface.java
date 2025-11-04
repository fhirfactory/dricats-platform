/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.configuration.segments.ports.internal.JGroupsInterfaceConfigurationObject;
import net.fhirfactory.dricats.internals.oam.metrics.base.CommonComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.IMetricsExtractionService;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.InterfaceImplementationBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.net.URI;

abstract public class MLLPInterface extends InterfaceImplementationBase implements IMetricsExtractionService {

    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900053L;
    private static final Logger LOG = LoggerFactory.getLogger(MLLPInterface.class);

    //
    // Constants
    //
    private static final String EXTERNAL_MLLP_SERVER = "MLLP_SERVER";
    private static final String EXTERNAL_MLLP_CLIENT = "MLLP_CLIENT";

    //
    // Attributes
    //

    private URI endpointURI;

    //
    // Constructor(s)
    //

    public MLLPInterface(){
        super();
        getLogger().trace("MLLPInterface(): constructed");
    }

    public MLLPInterface(String name, String documentation, String interfaceSpecialisation){
        super(name,documentation,interfaceSpecialisation);
        getLogger().trace("MLLPInterface(name, documentation, interfaceSpecialisation): constructed");
    }

    public MLLPInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation){
        super(parent, name, documentation, interfaceSpecialisation);
        getLogger().trace("MLLPInterface(parent, name, documentation, interfaceSpecialisation): constructed");
    }

    public MLLPInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, JGroupsInterfaceConfigurationObject configurationObject){
        super(parent, name, documentation, interfaceSpecialisation);
        getLogger().trace("MLLPInterface(parent, name, documentation, specialization, configurationObject): constructed");
    }

    public MLLPInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, URI uri) {
        super(parent, name, documentation, interfaceSpecialisation, uri);
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }
    
    //
    // Bean Methods
    //
    
    public void setEndpointURI(URI endpointURI) {
    	this.endpointURI = endpointURI;
    }
    
    public URI getEndpointURI() {
    	return(this.endpointURI);
    }
    

	//
	// Metrics Export
	//
	
	@Override
	public CommonComponentMetricsData retrieveMetricsData() {
		return (getMetricsData());
	}
	
    //
    // Utility Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("endpointURI", endpointURI)
                .appendSuper(super.toString())
                .toString();
    }

    protected Logger getLogger(){
        return(LOG);
    }
}
