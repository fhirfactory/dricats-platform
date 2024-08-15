/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.model.networking;

import java.io.Serial;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.networking.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.dricats.internals.model.oam.EndpointMetricsData;
import net.fhirfactory.dricats.internals.model.oam.MetricsExtractionService;
import net.fhirfactory.dricats.internals.model.oam.common.CommonComponentMetricsData;
import net.fhirfactory.dricats.internals.model.software.SoftwareComponent;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;
abstract public class NetworkEndpoint extends SoftwareComponent implements MetricsExtractionService {

    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900053L;
    private static final Logger LOG = LoggerFactory.getLogger(NetworkEndpoint.class);

    //
    // Attributes
    //

    private URI endpointURI;
    private EndpointMetricsData metricsData;

    //
    // Constructor(s)
    //

    public NetworkEndpoint() {
    	super();
    	this.endpointURI = null;
    	this.metricsData = new EndpointMetricsData();
    }
    
    public NetworkEndpoint(
    		DistributableObjectReference parentComponent, 
    		DistributableObjectIdentifier componentIdentifier,
    		SoftwareComponentType componentType,
    		URI endpointURI,
    	    String subsystemDeploymentSite,
    	    String subsystemDeploymentGroup, 
    		NetworkSecurityZoneEnum deploymentZone) {
    	super(parentComponent, componentIdentifier, componentType, subsystemDeploymentSite, subsystemDeploymentGroup, deploymentZone );
    	setEndpointURI(endpointURI);
    	this.metricsData = new EndpointMetricsData();
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
    
    public EndpointMetricsData getMetricsData() {
    	return(this.metricsData);
    }
    
    public void setMetricsData(EndpointMetricsData metricsData) {
    	if(metricsData != null) {
    		this.metricsData = metricsData;
    	}
    }
	
	//
	// Metrics Export
	//
	
	@Override
	public CommonComponentMetricsData retrieveMetricsData() {
		return (this.metricsData);
	}
	
    //
    // Utility Methods
    //

	protected Logger getLogger(){
        return(LOG);
    }
}
