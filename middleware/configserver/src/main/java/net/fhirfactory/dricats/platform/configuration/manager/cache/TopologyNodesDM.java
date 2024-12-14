/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.platform.configuration.manager.cache;

import net.fhirfactory.pegacorn.core.constants.systemwide.DeploymentSystemIdentificationInterface;
import net.fhirfactory.pegacorn.core.model.component.SoftwareComponent;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentIdType;
import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.core.model.generalid.FDNToken;
import net.fhirfactory.pegacorn.core.model.topology.mode.NetworkSecurityZoneEnum;
import net.fhirfactory.pegacorn.core.model.topology.nodes.SolutionTopologyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author Mark A. Hunter
 * @since 2020-07-01
 */
@ApplicationScoped
public class TopologyNodesDM implements DeploymentSystemIdentificationInterface {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyNodesDM.class);

    private SolutionTopologyNode deploymentSolution;
    private ConcurrentHashMap<TopologyNodeFDN, SoftwareComponent> fdnNodeSetMap;
    private ConcurrentHashMap<ComponentIdType, SoftwareComponent> idNodeSetMap;
    private Object nodeSetLock;

    public TopologyNodesDM() {
        LOG.debug(".TopologyDM(): Constructor initialisation");
        this.deploymentSolution = null;
        this.fdnNodeSetMap = new ConcurrentHashMap<TopologyNodeFDN, SoftwareComponent>();
        this.idNodeSetMap = new ConcurrentHashMap<>();
        this.nodeSetLock = new Object();
    }

    private void insertOrOverwriteTopologyNode(SolutionTopologyNode node){
        LOG.debug(".insertOrOverwriteTopologyNode(): Entry");
        synchronized (nodeSetLock) {
            if (fdnNodeSetMap.containsKey(node.getComponentFDN())) {
                fdnNodeSetMap.remove(node.getComponentFDN());
            }
            fdnNodeSetMap.put(node.getComponentFDN(), node);
        }
        LOG.debug(".insertOrOverwriteTopologyNode(): Exit");
    }

    public SolutionTopologyNode getDeploymentSolution() {
        return deploymentSolution;
    }

    public void setDeploymentSolution(SolutionTopologyNode deploymentSolution) {
        LOG.debug(".setDeploymentSolution(): Entry");
        this.deploymentSolution = deploymentSolution;
        insertOrOverwriteTopologyNode(deploymentSolution);
        LOG.debug(".setDeploymentSolution(): Exit");
    }

    @Override
    public String getSystemName() {
        return (getDeploymentSolution().getComponentFDN().extractRDNForNodeType(ComponentTypeTypeEnum.SOLUTION).getNodeName());
    }

    @Override
    public String getSystemVersion() {
        return (getDeploymentSolution().getComponentFDN().extractRDNForNodeType(ComponentTypeTypeEnum.SOLUTION).getNodeVersion());
    }

    public FDNToken getSystemIdentifier() {
        return (getDeploymentSolution().getComponentFDN().toTypeBasedFDN().getToken());
    }

    @Override
    public String getSystemOwnerName() {
        return (getDeploymentSolution().getSystemOwner());
    }

    /**
     * This function adds an entry to the Element Set.
     * <p>
     * Note that the default behaviour is to UPDATE the values with the set if
     * there already exists an instance for the specified FDNToken (identifier).
     *
     * Note, we have to do a deep inspection of the ConcurrentHashMap key (FDNToken) content,
     * as the default only only looks for equivalence with respect to the action Object instance.
     *
     * @param newElement The NodeElement to be added to the Set
     */
    public void addTopologyNode(SoftwareComponent newElement) {
        LOG.debug(".addTopologyNode(): Entry, newElement --> {}", newElement);
        if (newElement == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): newElement is null"));
        }
        if (newElement.getComponentFDN() == null) {
            throw (new IllegalArgumentException(".addTopologyNode(): bad elementID within newElement"));
        }
        synchronized (nodeSetLock) {
            ComponentIdType componentId = null;
            if(this.fdnNodeSetMap.containsKey(newElement.getComponentFDN())){
                SoftwareComponent node = this.fdnNodeSetMap.get(newElement.getComponentFDN());
                if(node != null){
                    componentId = node.getComponentID();
                }
                this.fdnNodeSetMap.remove(newElement.getComponentFDN());
            }
            if(componentId != null){
                if(this.idNodeSetMap.containsKey(componentId)) {
                    this.idNodeSetMap.remove(componentId);
                }
            }
            this.idNodeSetMap.put(newElement.getComponentID(), newElement);
            this.fdnNodeSetMap.put(newElement.getComponentFDN(), newElement);
        }
        LOG.debug(".addTopologyNode(): Exit");
    }

    /**
     *
     * @param elementID
     */
    public void deleteTopologyNode(TopologyNodeFDN elementID) {
        LOG.debug(".deleteTopologyNode(): Entry, elementID --> {}", elementID);
        if (elementID == null) {
            throw (new IllegalArgumentException(".removeNode(): elementID is null"));
        }
        synchronized (nodeSetLock){
            if(this.fdnNodeSetMap.containsKey(elementID)){
                this.fdnNodeSetMap.remove(elementID);
            }
            SoftwareComponent node = this.fdnNodeSetMap.get(elementID);
            ComponentIdType componentId = null;
            if(node != null){
                componentId = node.getComponentID();
            }
            if(componentId != null) {
                if (idNodeSetMap.containsKey(componentId)){
                    idNodeSetMap.remove(componentId);
                }
            }
        }
        LOG.debug(".deleteTopologyNode(): Exit");
    }

    /**
     *
     * @param componentId
     */
    public void deleteTopologyNode(ComponentIdType componentId) {
        LOG.debug(".deleteTopologyNode(): Entry, componentId --> {}", componentId);
        if (componentId == null) {
            throw (new IllegalArgumentException(".removeNode(): elementID is null"));
        }
        synchronized (nodeSetLock){
            TopologyNodeFDN topologyNodeFDN = null;
            if(this.idNodeSetMap.containsKey(componentId)){
                SoftwareComponent node = idNodeSetMap.get(componentId);
                if(node != null) {
                    topologyNodeFDN = node.getComponentFDN();
                }
                this.idNodeSetMap.remove(componentId);
            }
            if(topologyNodeFDN != null){
                if(fdnNodeSetMap.containsKey(topologyNodeFDN)){
                    this.fdnNodeSetMap.remove(topologyNodeFDN);
                }
            }
        }
        LOG.debug(".deleteTopologyNode(): Exit");
    }

    /**
     *
     * @return
     */
    public Set<SoftwareComponent> getTopologyNodeSet() {
        LOG.debug(".getTopologyNodeSet(): Entry");
        LinkedHashSet<SoftwareComponent> elementSet = new LinkedHashSet<SoftwareComponent>();
        if (this.fdnNodeSetMap.isEmpty()) {
            LOG.debug(".getTopologyNodeSet(): Exit, The module map is empty, returning null");
            return (null);
        }
        synchronized (nodeSetLock) {
            elementSet.addAll(this.fdnNodeSetMap.values());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(".getTopologyNodeSet(): Exit, returning an element set, size --> {}", elementSet.size());
        }
        return (elementSet);
    }

    /**
     *
     * @param nodeFDN
     * @return
     */
    public SoftwareComponent getSoftwareComponent(TopologyNodeFDN nodeFDN) {
        LOG.debug(".getSoftwareComponent(): Entry, nodeFDN --> {}", nodeFDN);
        if (nodeFDN == null) {
            LOG.debug(".getSoftwareComponent(): Exit, provided a null nodeFDN , so returning null");
            return (null);
        }
        SoftwareComponent retrievedNode = null;
        synchronized (nodeSetLock) {
            retrievedNode = this.fdnNodeSetMap.get(nodeFDN);
        }
        LOG.debug(".getSoftwareComponent(): Exit, retrievedNode->{}", retrievedNode);
        return (retrievedNode);
    }

    public SoftwareComponent getSoftwareComponent(ComponentIdType nodeId) {
        LOG.debug(".getSoftwareComponent(): Entry, nodeId --> {}", nodeId);
        if (nodeId == null) {
            LOG.debug(".getSoftwareComponent(): Exit, provided a null nodeId , so returning null");
            return (null);
        }
        SoftwareComponent retrievedNode = null;
        synchronized (nodeSetLock) {
            retrievedNode = this.idNodeSetMap.get(nodeId);
        }
        LOG.debug(".getSoftwareComponent(): Exit, retrievedNode->{}", retrievedNode);
        return (retrievedNode);
    }


    private boolean stringValuesMatch(String stringA, String stringB){
        if(stringA == null && stringB == null){
            return(true);
        }
        if(stringA == null || stringB == null){
            return(false);
        }
        boolean stringsAreEqual = stringA.contentEquals(stringB);
        return(stringsAreEqual);
    }

    public List<SoftwareComponent> getSoftwareComponent(ComponentTypeTypeEnum nodeType, String nodeName, String nodeVersion){
        LOG.debug(".nodeSearch(): Entry, nodeType->{}, nodeName->{}, nodeVersion->{}", nodeType, nodeName, nodeVersion);
        ArrayList<SoftwareComponent> nodeList = new ArrayList<>();
        Collection<SoftwareComponent> topologyNodes= null;
        synchronized (nodeSetLock) {
            topologyNodes = fdnNodeSetMap.values();
        }
        for (SoftwareComponent currentNode: topologyNodes) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(".nodeSearch(): Search Cache Entry : nodeRDN->{}, nodeComponentType->{}", currentNode.getComponentRDN(), currentNode.getComponentType());
            }
            boolean nodeTypeMatches = nodeType.equals(currentNode.getComponentType());
            boolean nodeNameMatches = nodeName.contentEquals(currentNode.getComponentRDN().getNodeName());
            boolean nodeVersionMatches = nodeVersion.contentEquals(currentNode.getComponentRDN().getNodeVersion());
            if (nodeTypeMatches && nodeNameMatches && nodeVersionMatches) {
                LOG.trace(".nodeSearch(): Node found!!! Adding to search result!");
                nodeList.add(currentNode);
            }
        }
        return(nodeList);
    }

    public NetworkSecurityZoneEnum getDeploymentNetworkSecurityZone(String nodeName){
        Collection<SoftwareComponent> nodeCollection = null;
        synchronized (nodeSetLock) {
            nodeCollection = fdnNodeSetMap.values();
        }
        for(SoftwareComponent currentNode: nodeCollection){
            boolean nameSame = currentNode.getComponentRDN().getNodeName().contentEquals(nodeName);
            boolean isProcessingPlant = currentNode.getComponentType().equals(ComponentTypeTypeEnum.PROCESSING_PLANT);
            if(nameSame && isProcessingPlant){
                return(currentNode.getSecurityZone());
            }
        }
        return(NetworkSecurityZoneEnum.INTERNET);
    }
}
