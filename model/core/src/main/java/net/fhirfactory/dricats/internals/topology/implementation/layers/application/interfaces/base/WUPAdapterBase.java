package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;

public class WUPAdapterBase extends ApplicationComponent implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //


    //
    // Constructor(s)
    //(

    public WUPAdapterBase() {
        super();
    }

    public WUPAdapterBase(String name, String documentation) {
        super(name, documentation, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_ADAPTER.getType());
        setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
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

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("adapterType", getAdapterType())
                .append("supportedApplicationFunctions", getSupportedApplicationFunctions())
                .append("supportedApplicationServices", getSupportedApplicationServices())
                .append("interfaces", getInterfaces())
                .append("parent", getParent())
                .append("subComponents", getSubComponents())
                .append("metricsData", getMetricsData())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getExtensions())
                .append("identifiers", getIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getLocalId())
                .toString();
    }

    //
     // Type Enum
    //
    public enum WUPAdapterTypeEnum {
        CAMEL_CONSUMER_ADAPTER,
        CAMEL_PRODUCER_ADAPTER,
        MLLP_RECEIVER_ADAPTER,
        MLLP_SENDER_ADAPTER,
        HTTP_CLIENT_ADAPTER,
        HTTP_SERVER_ADAPTER,
        JGROUPS_MESSAGING_ADAPTER,
        JGROUPS_RMI_ADAPTER,
        INTERNAL_DATA_GRID_ADAPTER,
        KAFKA_CONSUMER_ADAPTER,
        KAFKA_PRODUCER_ADAPTER;
    }
}
