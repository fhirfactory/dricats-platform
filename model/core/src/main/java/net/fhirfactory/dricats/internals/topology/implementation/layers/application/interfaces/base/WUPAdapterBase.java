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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.common.TopologyComponent;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.print.attribute.HashPrintJobAttributeSet;
import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WUPAdapterBase extends TopologyComponent implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //

    private String adapterType;

    //
    // Constructor(s)
    //(

    public WUPAdapterBase() {
        super();
    }

    public WUPAdapterBase(ElementReference parent, String name, String documentation, WUPAdapterBase.WUPAdapterTypeEnum adapterType) {
        super(parent, name, documentation, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_ADAPTER.getType(), new HashMap<>());
        setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        setAdapterType(adapterType);
    }

    //
    // Accessors
    //

    @JsonIgnore
    public WUPAdapterTypeEnum getAdapterType() {
        String adapterTypeString = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName());
        if(adapterTypeString != null){
            return(WUPAdapterTypeEnum.valueOf(adapterTypeString));
        }
        return(null);
    }

    public void setAdapterType(WUPAdapterTypeEnum adapterType) {
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), adapterType.name());
    }

    public void setEndpointURI(URI endpointURI) {
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ENDPOINT_URI.getExtensionName(), endpointURI.toString());
    }

    public URI getEndpointURI() {
        String endpointURIString = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ENDPOINT_URI.getExtensionName());
        if(endpointURIString != null){
            return(URI.create(endpointURIString));
        }
        return(null);
    }

    @JsonIgnore
    public Map<String, String> getAdapterComponentProperties(){
        return(new HashMap<>());
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("elementType", getElementType())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("componentStatus", getComponentStatus())
                .append("parent", getParent())
                .append("metricsData", getMetricsData())
                .toString();
    }

    //
     // Type Enum
    //
    public enum WUPAdapterTypeEnum {
        CAMEL_CONSUMER_ADAPTER("Camel-Consumer", "Apache Camel Consumer Adapter"),
        CAMEL_PRODUCER_ADAPTER("Camel-Producer", "Apache Camel Producer Adapter"),
        MLLP_RECEIVER_ADAPTER("MLLP-Receiver", "Apache Camel MLLP Receiver Adapter"),
        MLLP_SENDER_ADAPTER("MLLP-Sender", "Apache Camel MLLP Sender Adapter"),
        HTTP_CLIENT_ADAPTER( "HTTP-Client", "HTTP Client Adapter"),
        HTTP_SERVER_ADAPTER("HTTP-Server", "HTTP Server Adapter"),
        JGROUPS_MESSAGING_ADAPTER( "JGroups-Messaging", "JGroups Messaging Adapter"),
        JGROUPS_RMI_ADAPTER( "JGroups-RMI", "JGroups Remote Method Invocation Adapter"),
        INTERNAL_DATA_GRID_ADAPTER( "Internal-DataGrid", "Internal Data Grid Adapter"),
        KAFKA_CONSUMER_ADAPTER( "KAFKA-Consumer", "KAFKA Consumer Adapter"),
        KAFKA_PRODUCER_ADAPTER( "KAFKA-Producer", "KAFKA Producer Adapter");

        private String name;
        private String description;

        private WUPAdapterTypeEnum(String name, String description){
            this.name = name;
            this.description = description;
        }

        public String getName(){
            return(this.name);
        }

        public String getDescription(){
            return(this.description);
        }

        public WUPAdapterTypeEnum fromName(String name){
            for(WUPAdapterTypeEnum currentValue: WUPAdapterTypeEnum.values()){
                if(currentValue.getName().equals(name)){
                    return(currentValue);
                }
            }
            return(null);
        }
    }
}
