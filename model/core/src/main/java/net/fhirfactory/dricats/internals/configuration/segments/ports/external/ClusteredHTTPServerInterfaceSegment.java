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
package net.fhirfactory.dricats.internals.configuration.segments.ports.external;

import java.io.Serial;
import java.util.StringJoiner;

import net.fhirfactory.dricats.internals.configuration.segments.ports.datatypes.IPPortInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredHTTPServerInterfaceSegment extends HTTPServerConfigurationObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900912L;
    private static Logger LOG = LoggerFactory.getLogger(ClusteredHTTPServerInterfaceSegment.class);

    //
    // Attributes
    //

    private IPPortInterface servicePortValue;
    private Integer clusterServicePortOffsetValue;

    //
    // Constructor(s)
    //

    public ClusteredHTTPServerInterfaceSegment(){
        super();
        this.servicePortValue = null;
        this.clusterServicePortOffsetValue = null;
    }

    //
     // Bean Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    public IPPortInterface getServicePortValue() {
        return servicePortValue;
    }

    public void setServicePortValue(IPPortInterface servicePortValue) {
        this.servicePortValue = servicePortValue;
    }

    public Integer getClusterServicePortOffsetValue() {
        return clusterServicePortOffsetValue;
    }

    public void setClusterServicePortOffsetValue(Integer clusterServicePortOffsetValue) {
        this.clusterServicePortOffsetValue = clusterServicePortOffsetValue;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", ClusteredHTTPServerInterfaceSegment.class.getSimpleName() + "[", "]")
                .add("servicePortValue=" + getServicePortValue())
                .add("clusterServicePortOffsetValue=" + getClusterServicePortOffsetValue())
                .add("encrypted=" + isEncrypted())
                .add("supportedInterfaceProfiles=" + getSupportedInterfaceProfiles())
                .add("startupDelay=" + getStartupDelay())
                .add("servicePorts=" + getServicePorts())
                .add("port=" + getPort())
                .add("configurationParameters=" + getConfigurationParameters())
                .add("name='" + getId() + "'")
                .toString();
    }

}
