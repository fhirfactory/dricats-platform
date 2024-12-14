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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.networking.NetworkEndpoint;
import net.fhirfactory.dricats.internals.model.networking.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;
import net.fhirfactory.dricats.internals.model.software.interfaces.SubsystemInterface;
import net.fhirfactory.dricats.internals.model.software.valuesets.SoftwareComponentIdentifierTypeEnum;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelConfiguration;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JChannelStatusEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JChannelEndpoint extends NetworkEndpoint{

	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 3403310128066770387L;
	private static final Logger LOG = LoggerFactory.getLogger(JChannelEndpoint.class);
	
	//
	// Attributes
	//

    private JChannel localChannel;
    private RpcDispatcher rpcDispatcher;
    private JChannelConfiguration configuration;
    private JChannelStatusEnum endpointStatus;

    private Object localChannelLock;

    //
    // Constructor(s)
    //
    /*
    public JChannelEndpoint(){
    	super();
        this.localChannel = null;
        this.rpcDispatcher = null;
        this.localChannelLock = new Object();
        setEndpointStatus(JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }
    */

    public JChannelEndpoint(
            DistributableObjectReference parentComponent,
            DistributableObjectIdentifier componentIdentifier,
            SoftwareComponentType componentType,
            URI endpointURI,
            String subsystemDeploymentSite,
            String subsystemDeploymentGroup,
            NetworkSecurityZoneEnum deploymentZone,
            JChannelConfiguration conifgurationObject,
            SubsystemInterface subsystemInterface) {
    	super(parentComponent, componentIdentifier, componentType, endpointURI, subsystemDeploymentSite, subsystemDeploymentGroup, deploymentZone, subsystemInterface );
    	setConfiguration(conifgurationObject);
    	setEndpointStatus(JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }

    //
    // Bean Methods
    //
    
    /**
   	 * @return the endpointStatus
   	 */
   	public JChannelStatusEnum getEndpointStatus() {
   		return endpointStatus;
   	}

   	/**
   	 * @param endpointStatus the endpointStatus to set
   	 */
   	public void setEndpointStatus(JChannelStatusEnum endpointStatus) {
   		this.endpointStatus = endpointStatus;
   	}

    public JChannelConfiguration getConfiguration() {
    	return(this.configuration);
    }

	public void setConfiguration(JChannelConfiguration configurationObject) {
    	this.configuration = configurationObject;
    }
    
    protected Object getLocalChannelLock(){
        return(this.localChannelLock);
    }
    
    public JChannel getLocalChannel() {
        return localChannel;
    }

    public void setLocalChannel(JChannel channel) {
        this.localChannel = channel;
    }

    public RpcDispatcher getRPCDispatcher() {
        return rpcDispatcher;
    }

    public void setRPCDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
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

    public List<Address> getAllViewMembers() {
        if (getLocalChannel() == null) {
            return (new ArrayList<>());
        }
        if (getLocalChannel().getView() == null) {
            return (new ArrayList<>());
        }
        try {
            List<Address> members = new ArrayList<>();
            synchronized (getLocalChannelLock()) {
                members.addAll(getLocalChannel().getView().getMembers());
            }
            return (members);
        } catch (Exception ex) {
            getLogger().warn(".getAllMembers(): Failed to get View Members, Error: Message->{}, StackTrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }
        return (new ArrayList<>());
    }

    protected Address getMyAddress(){
        if(getLocalChannel() != null){
            Address myAddress = getLocalChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

    
    //
    // Utility Methods
    //
    
    @Override
    protected Logger getLogger() {
    	return(LOG);
    }
}
