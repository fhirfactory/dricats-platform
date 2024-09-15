/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.platform.middleware.jgroups;

import java.io.Serial;
import java.net.URI;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.model.software.valuesets.SoftwareComponentIdentifierTypeEnum;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupsEndpointGroupEnum;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.networking.NetworkEndpoint;
import net.fhirfactory.dricats.internals.model.networking.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsMembership;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupsEndpointStatusEnum;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupsEndpointTypeEnum;

abstract public class JGroupsNetworkEndpoint extends NetworkEndpoint{

	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 3403310128066770387L;
	private static final Logger LOG = LoggerFactory.getLogger(JGroupsNetworkEndpoint.class);
	
	//
	// Attributes
	//

    private String clusterName;
    private JGroupsEndpointGroupEnum endpointGroup;
    private JGroupsMembership membership;
    private JChannel ipcChannel;
    private RpcDispatcher rpcDispatcher;
    private String stackFilename;
    private JGroupsEndpointStatusEnum endpointStatus;
    private JGroupsEndpointTypeEnum endpointType;

    private Object ipcChannelLock;

    //
    // Constructor(s)
    //
    public JGroupsNetworkEndpoint(){
    	super();
        this.clusterName = null;
        this.membership = new JGroupsMembership();
        this.ipcChannel = null;
        this.rpcDispatcher = null;
        this.ipcChannelLock = new Object();
        setEndpointStatus(JGroupsEndpointStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }
    
    public JGroupsNetworkEndpoint(
    		DistributableObjectReference parentComponent, 
    		DistributableObjectIdentifier componentIdentifier,
    		SoftwareComponentType componentType,
    		URI endpointURI,
    	    String subsystemDeploymentSite,
    	    String subsystemDeploymentGroup, 
    		NetworkSecurityZoneEnum deploymentZone,
    	    String jgroupsStackFilename, 
    	    JGroupsEndpointTypeEnum endpointType,
    	    String channelName,
    	    String clusterName,
            JGroupsEndpointGroupEnum endpointGroup) {
    	super(parentComponent, componentIdentifier, componentType, endpointURI, subsystemDeploymentSite, subsystemDeploymentGroup, deploymentZone );
    	setStackFilename(jgroupsStackFilename);
    	setEndpointType(endpointType);
    	setClusterName(clusterName);
    	setChannelName(channelName);
        setEndpointGroup(endpointGroup);
    	setEndpointStatus(JGroupsEndpointStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }

    //
    // Bean Methods
    //

    public JGroupsEndpointGroupEnum getEndpointGroup() {
        return endpointGroup;
    }

    public void setEndpointGroup(JGroupsEndpointGroupEnum endpointGroup) {
        this.endpointGroup = endpointGroup;
    }

    public String getClusterName() {
    	return(this.clusterName);
    }
    
    public void setClusterName(String clusterName) {
    	this.clusterName = clusterName;
    }
    
    /**
   	 * @return the endpointStatus
   	 */
   	public JGroupsEndpointStatusEnum getEndpointStatus() {
   		return endpointStatus;
   	}

   	/**
   	 * @param endpointStatus the endpointStatus to set
   	 */
   	public void setEndpointStatus(JGroupsEndpointStatusEnum endpointStatus) {
   		this.endpointStatus = endpointStatus;
   	}

   	/**
   	 * @return the endpointType
   	 */
   	public JGroupsEndpointTypeEnum getEndpointType() {
   		return endpointType;
   	}

   	/**
   	 * @param endpointType the endpointType to set
   	 */
    
    public String getStackFilename() {
    	return(this.stackFilename);
    }
    
	public void setEndpointType(JGroupsEndpointTypeEnum endpointType) {
		this.endpointType = endpointType;
	}

	public void setStackFilename(String filename) {
    	this.stackFilename = filename;
    }
    
    protected Object getIPCChannelLock(){
        return(this.ipcChannelLock);
    }
    
    public JChannel getIPCChannel() {
        return ipcChannel;
    }

    public void setIPCChannel(JChannel ipcChannel) {
        this.ipcChannel = ipcChannel;
    }

    public RpcDispatcher getRPCDispatcher() {
        return rpcDispatcher;
    }

    protected void setRPCDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }
    
    public JGroupsMembership getMembership() {
    	return(membership);
    }
    
    public void setMembership(JGroupsMembership membership) {
    	this.membership = membership;
    }

    @JsonIgnore
    public String getChannelName() {
        DistributableObjectIdentifier identifier = getIdentifier(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_JGROUPS_CHANNEL_NAME);
        if(identifier != null) {
            return(identifier.getIdentifierValue());
        }
        return (null);
    }

    @JsonIgnore
    public void setChannelName(String channelName) {
        DistributableObjectIdentifier identifier = getIdentifier(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_JGROUPS_CHANNEL_NAME);
        if(identifier != null) {
            identifier.setIdentifierValue(channelName);
            identifier.getEffectiveDate().setEffectiveStartDate(LocalDateTime.now());
        } else {
            identifier = new DistributableObjectIdentifier();
            identifier.setIdentifierType(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_JGROUPS_CHANNEL_NAME.toDistributableObjectIdentifierType());
            identifier.setIdentifierValue(channelName);
            addIdentifier(identifier);
        }
    }


    
    //
    // Utility Methods
    //
    
    @Override
    protected Logger getLogger() {
    	return(LOG);
    }
}
