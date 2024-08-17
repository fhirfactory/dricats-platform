/*
 * Copyright (c) 2024 Mark A. Hunter
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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupsEndpointStatusEnum;

public abstract class JGroupsEndpoint extends JGroupsNetworkEndpoint implements MembershipListener{

	//
	// Housekeeping
	//
	
	@Serial
	private static final long serialVersionUID = -1313768408260801607L;
	private static final Logger LOG = LoggerFactory.getLogger(JGroupsEndpoint.class);
	
	//
	// Attributes
	//

    private static int INITIALISATION_RETRY_COUNT = 5;
    private static Long INITIALISATION_RETRY_WAIT = 500L;
    
    @Inject
    private JGroupsNamingServices namingServices;

    //
    // Constructor(s)
    //

    public JGroupsEndpoint(){
    	super();
    }
    
    //
    // Abstract Methods
    //



    //
    // Channel Initialization
    //

    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (getEndpointStatus() != JGroupsEndpointStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED) {
            getLogger().debug(".initialise(): Exit, status is not JGROUPS_ENDPOINT_STATE_UNINITIALISED!");
            return;
        }

        // 1st, Initialize my JChannel
        getLogger().info(".initialise(): Step 1: [JChannel Initialisation] Start");
        establishJChannel();
        getLogger().info(".initialise(): Step 1: [JChannel Initialisation] Finish");

        //
        // 2nd, Update Metrics
        //
        getLogger().info(".initialise(): Step 2: [Update Metrics] Start");
        getMetricsData().touchLastActivityInstant();
        getLogger().info(".initialise(): Step 2: [Update Metrics] Finish");

    }
    
    
    //
    // JGroups Group/Cluster Membership Event Listener
    //

    @Override
    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getIPCChannel() != null) {
            getLogger().debug("JGroupsCluster->{}", getIPCChannel().getClusterName());
        } else {
            getLogger().debug("JGroupsCluster still Forming");
        }
        getMembership().updateCurrentMembership(addressList);

        if(getLogger().isInfoEnabled()) {
            for (Address currentAddress : addressList) {
                getLogger().debug("Visible Member->{}", currentAddress);
            }
        }

        //
        // A Report
        //
        if((getIPCChannel() != null) && getLogger().isDebugEnabled()) {
            getLogger().debug(".viewAccepted(): -------- Starting Channel Report -------");
            String channelProperties = getIPCChannel().getProperties();
            getLogger().debug(".viewAccepted(): Properties->{}", channelProperties);
            String jchannelState = getIPCChannel().getState();
            getLogger().debug(".viewAccepted(): State->{}", jchannelState);
            getLogger().debug(".viewAccepted(): -------- End Channel Report -------");
        }
        //
        // Handle View Change
        getLogger().debug(".viewAccepted(): Checking PubSub Participants");
        List<JGroupsNetworkAddress> removals = getMembership().getMembershipRemovals();
        List<JGroupsNetworkAddress> additions = getMembership().getMembershipAdditions();
        getLogger().debug(".viewAccepted(): Changes(MembersAdded->{}, MembersRemoved->{}", additions.size(), removals.size());
        getLogger().debug(".viewAccepted(): Iterating through ActionInterfaces");
        for(JGroupsNetworkAddress currentAddedElement: additions){
            processInterfaceAddition(currentAddedElement);
        }
        for(JGroupsNetworkAddress currentRemovedElement: removals){
            processInterfaceRemoval(currentRemovedElement);
        }
        getLogger().debug(".viewAccepted(): PubSub Participants check completed");
        getLogger().debug(".viewAccepted(): Exit");
    }

    @Override
    public void suspect(Address suspected_mbr) {
        MembershipListener.super.suspect(suspected_mbr);
    }

    @Override
    public void block() {
        MembershipListener.super.block();
    }

    @Override
    public void unblock() {
        MembershipListener.super.unblock();
    }
    
    /**
     * This method parses the list of "interfaces" ADDED (exposed/visible) to a JChannel instance (i.e. visible within the
     * same JGroups cluster) and works out if a scan of the enpoint is (a) not another instance (different POD) of
     * this service and is implementing the same "function".
     *
     * Note, it has to check the "name" quality/validity/structure - as sometimes JGroups can pass some wacky values
     * to us...
     *
     * @param addedInterface
     */
    public void processInterfaceAddition(JGroupsNetworkAddress addedInterface){
        getLogger().info(".interfaceAdded(): Entry, addedInterface->{}", addedInterface);
        String endpointSubsystemName = getNamingServices().getApplicationNameFromEndpointName(addedInterface.getAddressName());
        String endpointFunctionName = getNamingServices().getEndpointFunctionNameFromChannelName(addedInterface.getAddressName());
        if(StringUtils.isNotEmpty(endpointSubsystemName) && StringUtils.isNotEmpty(endpointFunctionName)) {
            boolean itIsAnotherInstanceOfMe = endpointSubsystemName.contentEquals(getSubsystemParticipantName());
            boolean itIsSameType = endpointFunctionName.contentEquals(PetasosEndpointFunctionTypeEnum.PETASOS_TOPOLOGY_ENDPOINT.getDisplayName());
            if (!itIsAnotherInstanceOfMe && itIsSameType) {
                getLogger().debug(".interfaceAdded(): itIsAnotherInstanceOfMe && !itIsSameType");
                String endpointChannelName = addedInterface.getAddressName();
                JGroupsIntegrationPointSummary jgroupsIP = buildFromChannelName(endpointChannelName);
                integrationPointCheckScheduleMap.scheduleJGroupsIntegrationPointCheck(jgroupsIP, false, true);
                scheduleEndpointValidation();
            }
        }
        getLogger().debug(".interfaceAdded(): Exit");
    }
    
    
    public void processInterfaceRemoval(JGroupsNetworkAddress removedInterface) {
    	
    }
    
    public void processInterfaceSuspect(JGroupsNetworkAddress suspectInterface) {
    	
    }

    //
    // JChannel Initialisation
    //

    protected void establishJChannel(){
        getLogger().info(".establishJChannel(): Entry, fileName->{}, clusterName->{}, channelName->{}",  getStackFilename(), getClusterName(), getChannelName());
        int retryCount = 0;
        synchronized (getIPCChannelLock()) {
            while (retryCount < 5) {
                try {
                    getLogger().trace(".establishJChannel(): Creating JChannel");
                    getLogger().trace(".establishJChannel(): Getting the required ProtocolStack");
                    JChannel newChannel = new JChannel(getStackFilename());
                    getLogger().trace(".establishJChannel(): JChannel initialised, now setting JChannel name");
                    newChannel.name(getChannelName());
                    getLogger().trace(".establishJChannel(): JChannel Name set, now set ensure we don't get our own messages");
                    newChannel.setDiscardOwnMessages(true);
                    getLogger().trace(".establishJChannel(): Now setting RPCDispatcher");
                    RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, this);
                    newRPCDispatcher.setMembershipListener(this);
                    getLogger().trace(".establishJChannel(): RPCDispatcher assigned, now connect to JGroup");
                    newChannel.connect(getClusterName());
                    getLogger().trace(".establishJChannel(): Connected to JGroup complete, now assigning class attributes");
                    this.setIPCChannel(newChannel);
                    this.setRPCDispatcher(newRPCDispatcher);
                    getLogger().trace(".establishJChannel(): Exit, JChannel & RPCDispatcher created");
                    break;
                } catch (Exception e) {
                    getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, error->{}", ExceptionUtils.getMessage(e));
                    if (retryCount < INITIALISATION_RETRY_COUNT) {
                        getLogger().error(".establishJChannel(): Cannot establish JGroups Channel, retrying");
                    }
                }
                retryCount += 1;
                if (retryCount >= INITIALISATION_RETRY_COUNT) {
                    break;
                } else {
                    try {
                        Thread.sleep(INITIALISATION_RETRY_WAIT);
                    } catch (Exception e) {
                        getLogger().warn(".establishJChannel():Sleep period interrupted, warn->{}", ExceptionUtils.getMessage(e));
                    }
                }
            }
        }
    }

    //
    // Getters and Setters
    //

    public JGroupsNamingServices getNamingServices() {
    	return(namingServices);
    }

    //
    // JGroups Membership Methods
    //

    public List<Address> getAllViewMembers() {
        if (getIPCChannel() == null) {
            return (new ArrayList<>());
        }
        if (getIPCChannel().getView() == null) {
            return (new ArrayList<>());
        }
        try {
        	List<Address> members = new ArrayList<>();
            synchronized (getIPCChannelLock()) {
                members.addAll(getIPCChannel().getView().getMembers());
            }
            return (members);
        } catch (Exception ex) {
            getLogger().warn(".getAllMembers(): Failed to get View Members, Error: Message->{}, StackTrace->{}", ExceptionUtils.getMessage(ex), ExceptionUtils.getStackTrace(ex));
        }
        return (new ArrayList<>());
    }


    public Address getTargetMemberAddress(String name){
        getLogger().debug(".getTargetMemberAddress(): Entry, name->{}", name);
        if(getIPCChannel() == null){
            getLogger().debug(".getTargetMemberAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getTargetMemberAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        Address foundAddress = null;
        getLogger().trace(".getTargetMemberAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for (Address currentAddress : addressList) {
            getLogger().trace(".getTargetMemberAddress(): Iterating through Address list, current element->{}", currentAddress);
            if (currentAddress.toString().contentEquals(name)) {
                getLogger().trace(".getTargetMemberAddress(): Exit, A match!");
                foundAddress = currentAddress;
                break;
            }
        }
        getLogger().debug(".getTargetMemberAddress(): Exit, address->{}", foundAddress);
        return(foundAddress);
    }


    public Address getCandidateTargetServiceAddress(String targetServiceName){
        getLogger().debug(".getCandidateTargetServiceAddress(): Entry, targetServiceName->{}", targetServiceName);
        if(getIPCChannel() == null){
            getLogger().debug(".getCandidateTargetServiceAddress(): IPCChannel is null, exit returning (null)");
            return(null);
        }
        getLogger().trace(".getCandidateTargetServiceAddress(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        Address foundAddress = null;
        getLogger().debug(".getCandidateTargetServiceAddress(): Got the Address set via view, now iterate through and see if one is suitable");
        for (Address currentAddress : addressList) {
            getLogger().debug(".getCandidateTargetServiceAddress(): Iterating through Address list, current element->{}", currentAddress);
            String currentService = currentAddress.toString();
            if (currentService.equals(targetServiceName)) {
                getLogger().debug(".getCandidateTargetServiceAddress(): Exit, A match!");
                foundAddress = currentAddress;
                break;
            }
        }
        getLogger().debug(".getCandidateTargetServiceAddress(): Exit, foundAddress->{}",foundAddress );
        return(foundAddress);
    }

    protected boolean isTargetAddressActive(String addressName){
        getLogger().debug(".isTargetAddressActive(): Entry, addressName->{}", addressName);
        if(getIPCChannel() == null){
            getLogger().debug(".isTargetAddressActive(): IPCChannel is null, exit returning -false-");
            return(false);
        }
        if(StringUtils.isEmpty(addressName)){
            getLogger().debug(".isTargetAddressActive(): addressName is empty, exit returning -false-");
            return(false);
        }
        getLogger().trace(".isTargetAddressActive(): IPCChannel is NOT null, get updated Address set via view");
        List<Address> addressList = getAllViewMembers();
        boolean addressIsActive = false;
        getLogger().trace(".isTargetAddressActive(): Got the Address set via view, now iterate through and see our address is there");
        for (Address currentAddress : addressList) {
            getLogger().trace(".isTargetAddressActive(): Iterating through Address list, current element->{}", currentAddress);
            if (currentAddress.toString().contentEquals(addressName)) {
                getLogger().trace(".isTargetAddressActive(): Exit, A match");
                addressIsActive = true;
                break;
            }
        }
        getLogger().debug(".isTargetAddressActive(): Exit, addressIsActive->{}",addressIsActive);
        return(addressIsActive);
    }

    public List<JGroupsNetworkAddress> getAllClusterTargets(){
        getLogger().debug(".getAllClusterTargets(): Entry");
        List<Address> addressList = getAllViewMembers();
        List<JGroupsNetworkAddress> adapterAddresses = new ArrayList<>();
        for (Address currentAddress : addressList) {
            getLogger().debug(".getAllTargets(): Iterating through Address list, current element->{}", currentAddress);
            JGroupsNetworkAddress currentAdapterAddress = new JGroupsNetworkAddress();
            currentAdapterAddress.setJGroupsAddress(currentAddress);
            currentAdapterAddress.setAddressName(currentAddress.toString());
            adapterAddresses.add(currentAdapterAddress);
        }
        getLogger().debug(".getAllClusterTargets(): Exit, adapterAddresses->{}", adapterAddresses);
        return(adapterAddresses);
    }

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }
    
    //
    // Utility Methods
    //
    
    protected Logger getLogger() {
    	return(LOG);
    }
}
