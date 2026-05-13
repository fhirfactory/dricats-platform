/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.technology;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node extends ElementBase {
    //
     // Housekeeping
    //
    @Serial private static final long serialVersionUID = -22345678910101L;
    private static final Logger LOG = LoggerFactory.getLogger(Node.class);

    // ArchiMate Node attributes (by reference IDs)
    // - interfaces: TechnologyInterface(s) this node exposes or uses
    // - subNodes: child Node(s) in a composition/aggregation structure
    // - parent: parent Node reference
    // - services: TechnologyService(s) provided by this node
    private List<ElementReference> interfaces;
    private List<ElementReference> subNodes;
    private ElementReference parent;
    private List<ElementReference> services;

    public Node() {
        super();
        this.interfaces = new ArrayList<>();
        this.subNodes = new ArrayList<>();
        this.services = new ArrayList<>();
        getLogger().trace("Node(): constructed");
    }

    protected Logger getLogger(){ return LOG; }

    public List<ElementReference> getInterfaces() { return interfaces; }
    public void setInterfaces(List<ElementReference> interfaces) { this.interfaces = interfaces == null ? new ArrayList<>() : interfaces; }
    public void addInterface(ElementReference interfaceId) {
        if(interfaceId == null){ return; }
        if(this.interfaces == null){ this.interfaces = new ArrayList<>(); }
        this.interfaces.add(interfaceId);
    }

    public List<ElementReference> getSubNodes() { return subNodes; }
    public void setSubNodes(List<ElementReference> subNodes) { this.subNodes = subNodes == null ? new ArrayList<>() : subNodes; }
    public void addSubNode(ElementReference nodeId) {
        if(nodeId == null){ return; }
        if(this.subNodes == null){ this.subNodes = new ArrayList<>(); }
        this.subNodes.add(nodeId);
    }

    public ElementReference getParent() { return parent; }
    public void setParent(ElementReference parent) { this.parent = parent; }

    public List<ElementReference> getServices() { return services; }
    public void setServices(List<ElementReference> services) { this.services = services == null ? new ArrayList<>() : services; }
    public void addService(ElementReference serviceId) {
        if(serviceId == null){ return; }
        if(this.services == null){ this.services = new ArrayList<>(); }
        this.services.add(serviceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Node node = (Node) o;
        return Objects.equals(interfaces, node.interfaces) &&
                Objects.equals(subNodes, node.subNodes) &&
                Objects.equals(parent, node.parent) &&
                Objects.equals(services, node.services);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interfaces, subNodes, parent, services);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interfaces", getInterfaces())
                .append("subNodes", getSubNodes())
                .append("parent", getParent())
                .append("services", getServices())
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
                .toString();
    }
}
