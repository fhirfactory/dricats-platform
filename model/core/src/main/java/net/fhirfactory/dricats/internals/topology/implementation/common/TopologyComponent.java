package net.fhirfactory.dricats.internals.topology.implementation.common;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

public class TopologyComponent extends ApplicationComponent implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //

    private static final String TOPOLOGY_COMPONENT_IDENTIFIER_TYPE_CODE = "TopologyComponent";
    private static final String TOPOLOGY_COMPONENT_IDENTIFIER_TYPE_DISPLAY = "Topology Component";
    private static final URI TOPOLOGY_COMPONENT_IDENTIFIER_TYPE_SYSTEM = URI.create("http://fhirfactory.net/dricats/valuesets/identifier_types");
    private static final String TOPOLOGY_COMPONENT_IDENTIFIER_TYPE_VALUE = "TopologyComponent";

    private static final String APPLICATION_COMPONENT_TYPE = "TopologyComponent";


    //
    // Constructor(s)
    //

    public TopologyComponent() {
        super();
        DistinguishedName topologyComponentName = new DistinguishedName();
        RelativeDistinguishedName relativeTopologyComponentName = new RelativeDistinguishedName(APPLICATION_COMPONENT_TYPE, getElementInstanceId().getIdValue());
        topologyComponentName.appendUnqualifiedName(relativeTopologyComponentName);
        ElementIdentifier identifier = new ElementIdentifier();
        identifier.setIdentifierValue(topologyComponentName);
        setIdentifier(identifier);
        setShortName(relativeTopologyComponentName.getUnqualifiedValue());
    }

    public TopologyComponent(ElementReference parent, String shortName, String description, String specialization, Map<String, String> extensions) {
        super(parent, shortName, description, specialization, extensions);
    }

    //
    // Bean Methods
    //

    //
    // Business Methods
    //

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
}
