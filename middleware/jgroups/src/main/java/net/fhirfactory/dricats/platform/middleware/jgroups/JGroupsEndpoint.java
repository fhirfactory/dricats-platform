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

import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupsEndpointStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private Boolean clusterMembershipProcessingScheduled;
    
    @Inject
    private JGroupsNamingServices namingServices;

    //
    // Constructor(s)
    //

    public JGroupsEndpoint(){
    	super();
        this.clusterMembershipProcessingScheduled = false;
    }
    
    //
    // Abstract Methods
    //



    //
    // Channel Initialization
    //

    @PostConstruct
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
        getLogger().debug(".viewAccepted(): [Update Unprocessed Change Lists] Start");
        getMembership().updateUnprocessedMembershipAdditionsList();
        getMembership().updateUnprocessedMembershipRemovalsList();
        getLogger().debug(".viewAccepted(): [Update Unprocessed Change Lists] Finish");
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

    public void processInterfaceAddition(JGroupsNetworkAddress addedInterface){
    }
    
    
    public void processInterfaceRemoval(JGroupsNetworkAddress removedInterface) {
    	
    }
    
    public void processInterfaceSuspect(JGroupsNetworkAddress suspectInterface) {
    	
    }

    public void clusterModificationsProcessing() {
        getLogger().debug(".clusterModificationsProcessing(): Entry");

        getLogger().debug(".clusterModificationsProcessing(): Exit");
    }

    public void scheduleEndpointValidation() {
        getLogger().debug(".scheduleEndpointValidation(): Entry ");
        if (getClusterMembershipProcessingScheduled()) {
            // do nothing, it is already scheduled
        } else {
            TimerTask endpointValidationTask = new TimerTask() {
                public void run() {
                    getLogger().debug(".scheduleEndpointValidation(): Entry");
                    boolean doAgain = performEndpointValidationCheck();
                    getLogger().debug(".scheduleEndpointValidation(): doAgain ->{}", doAgain);
                    if (!doAgain) {
                        cancel();
                        endpointCheckScheduled = false;
                    }
                    getLogger().debug(".scheduleEndpointValidation(): Exit");
                }
            };
            String timerName = "ClusterModificationProcessingTask";
            Timer timer = new Timer(timerName);
            timer.schedule(endpointValidationTask, getJgroupsParticipantInformationService().getEndpointValidationStartDelay(), getJgroupsParticipantInformationService().getEndpointValidationPeriod());
            endpointCheckScheduled = true;
        }
        getLogger().debug(".scheduleEndpointValidation(): Exit");
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

    public Boolean getClusterMembershipProcessingScheduled(){
        return(this.clusterMembershipProcessingScheduled);
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

    protected Address getMyAddress(){
        if(getIPCChannel() != null){
            Address myAddress = getIPCChannel().getAddress();
            return(myAddress);
        }
        return(null);
    }

    public void buildChannelName(){
        String channelName = getNamingServices().buildChannelName(getSubsystemDeploymentSite(), getSubsystem().getSystemName(), getEndpointType().getEndpointType(), getEndpointGroup().getEndpointGroupName(), getInstanceId());
        setChannelName(channelName);
    }
    
    //
    // Utility Methods
    //
    
    protected Logger getLogger() {
    	return(LOG);
    }
}
