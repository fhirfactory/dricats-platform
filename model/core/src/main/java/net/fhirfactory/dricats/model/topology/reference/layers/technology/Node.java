/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.layers.technology;

import net.fhirfactory.dricats.model.topology.reference.common.ElementBase;
import net.fhirfactory.dricats.model.common.DistributableObjectId;
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
    private List<DistributableObjectId> interfaces;
    private List<DistributableObjectId> subNodes;
    private DistributableObjectId parent;
    private List<DistributableObjectId> services;

    public Node() {
        super();
        this.interfaces = new ArrayList<>();
        this.subNodes = new ArrayList<>();
        this.services = new ArrayList<>();
        getLogger().trace("Node(): constructed");
    }

    protected Logger getLogger(){ return LOG; }

    public List<DistributableObjectId> getInterfaces() { return interfaces; }
    public void setInterfaces(List<DistributableObjectId> interfaces) { this.interfaces = interfaces == null ? new ArrayList<>() : interfaces; }
    public void addInterface(DistributableObjectId interfaceId) {
        if(interfaceId == null){ return; }
        if(this.interfaces == null){ this.interfaces = new ArrayList<>(); }
        this.interfaces.add(interfaceId);
    }

    public List<DistributableObjectId> getSubNodes() { return subNodes; }
    public void setSubNodes(List<DistributableObjectId> subNodes) { this.subNodes = subNodes == null ? new ArrayList<>() : subNodes; }
    public void addSubNode(DistributableObjectId nodeId) {
        if(nodeId == null){ return; }
        if(this.subNodes == null){ this.subNodes = new ArrayList<>(); }
        this.subNodes.add(nodeId);
    }

    public DistributableObjectId getParent() { return parent; }
    public void setParent(DistributableObjectId parent) { this.parent = parent; }

    public List<DistributableObjectId> getServices() { return services; }
    public void setServices(List<DistributableObjectId> services) { this.services = services == null ? new ArrayList<>() : services; }
    public void addService(DistributableObjectId serviceId) {
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
        return getClass().getSimpleName()+"{"+
                "name='"+getName()+'\''+
                ", interfacesCount="+(interfaces==null?0:interfaces.size())+
                ", subNodesCount="+(subNodes==null?0:subNodes.size())+
                ", parentRef="+(parent==null?"null":parent)+
                ", servicesCount="+(services==null?0:services.size())+
                ", documentation='"+getDocumentation()+'\''+
                ", specialization='"+getSpecialization()+'\''+
                ", properties="+getProperties()+
                ", objectID="+getObjectID()+
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }
}
