/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments.ports.base;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServicePortConfigurationObject implements Serializable{
	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 3403310128022770387L;
	private static final Logger LOG = LoggerFactory.getLogger(WebServicePortConfigurationObject.class);
	
	//
	// Attributes
	//
    
    private Integer servicePortValue;
    private String servicePortName;
    private String serviceDNSEntry;
    private String webServicePath;
    private Integer clusterServicePortOffsetValue;
    
    //
    // Constructor(s)
    //

    public WebServicePortConfigurationObject(){
        super();
        clusterServicePortOffsetValue = 0;
        servicePortValue = 0;
        servicePortName = null;
        serviceDNSEntry = null;
        webServicePath = null;
    }
    
    //
    // Bean Methods
    //

    public Integer getServicePortValue() {
        return servicePortValue;
    }

    public void setServicePortValue(Integer servicePortValue) {
        this.servicePortValue = servicePortValue;
    }

    public String getServicePortName() {
        return servicePortName;
    }

    public void setServicePortName(String servicePortName) {
        this.servicePortName = servicePortName;
    }

    public Integer getClusterServicePortIncludingOffset(Integer offset){
        if(clusterServicePortOffsetValue == null){
            getLogger().error(".getEdgeReceiveCommunicationClusterServicePort(): Not such port type defined");
            return(0);
        }
        Integer servicePortNumber = offset + getClusterServicePortOffsetValue();
        return(servicePortNumber);
    }

    public Integer getClusterServicePortOffsetValue() {
        return clusterServicePortOffsetValue;
    }

    public void setClusterServicePortOffsetValue(Integer clusterServicePortOffsetValue) {
        this.clusterServicePortOffsetValue = clusterServicePortOffsetValue;
    }

    public String getServiceDNSEntry() {
        return serviceDNSEntry;
    }

    public void setServiceDNSEntry(String serviceDNSEntry) {
        this.serviceDNSEntry = serviceDNSEntry;
    }
    
    protected Logger getLogger() {
    	return(LOG);
    }
    
    public String getWebServicePath() {
    	return(this.webServicePath);
    }
    
    public void setWebServicePath(String webServicePath) {
    	this.webServicePath = webServicePath;
    }

    
    //
    // Utility Methods
    //

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WebServicePortConfigurationObject [servicePortValue=");
		builder.append(servicePortValue);
		builder.append(", servicePortName=");
		builder.append(servicePortName);
		builder.append(", serviceDNSEntry=");
		builder.append(serviceDNSEntry);
		builder.append(", webServicePath=");
		builder.append(webServicePath);
		builder.append(", clusterServicePortOffsetValue=");
		builder.append(clusterServicePortOffsetValue);
		builder.append("]");
		return builder.toString();
	}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WebServicePortConfigurationObject that = (WebServicePortConfigurationObject) o;
        return Objects.equals(getServicePortValue(), that.getServicePortValue()) && Objects.equals(getServicePortName(), that.getServicePortName()) && Objects.equals(getServiceDNSEntry(), that.getServiceDNSEntry()) && Objects.equals(getWebServicePath(), that.getWebServicePath()) && Objects.equals(getClusterServicePortOffsetValue(), that.getClusterServicePortOffsetValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServicePortValue(), getServicePortName(), getServiceDNSEntry(), getWebServicePath(), getClusterServicePortOffsetValue());
    }
}
