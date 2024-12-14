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
package net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base;

import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelEndpoint;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsMembership;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import org.jgroups.Address;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JChannelMembershipTracker {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(JChannelMembershipTracker.class);

    //
    // Attributes
    //
    private JChannelEndpoint endpoint;
    private JGroupsMembership membership;
    private Boolean clusterMembershipProcessingScheduled;

    //
    // Constructor(s)
    //

    public JChannelMembershipTracker(JChannelEndpoint endpoint) {
        this.endpoint = endpoint;
        this.membership = new JGroupsMembership();
        setClusterMembershipProcessingScheduled(false);
    }

    //
    // Business Methods
    //

    public void viewAccepted(View newView) {
        getLogger().debug(".viewAccepted(): Entry, JGroups View Changed!");
        List<Address> addressList = newView.getMembers();
        getLogger().trace(".viewAccepted(): Got the Address set via view, now iterate through and see if one is suitable");
        if(getEndpoint().getLocalChannel() != null) {
            getLogger().debug("JGroupsCluster->{}", getEndpoint().getLocalChannel().getClusterName());
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
        if((getEndpoint() != null) && getEndpoint().getLocalChannel() != null && getLogger().isDebugEnabled()) {
            getLogger().debug(".viewAccepted(): -------- Starting Channel Report -------");
            String channelProperties = getEndpoint().getLocalChannel().getProperties();
            getLogger().debug(".viewAccepted(): Properties->{}", channelProperties);
            String jchannelState = getEndpoint().getLocalChannel().getState();
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
                        setClusterMembershipProcessingScheduled(false);
                    }
                    getLogger().debug(".scheduleEndpointValidation(): Exit");
                }
            };
            String timerName = "ClusterModificationProcessingTask";
            Timer timer = new Timer(timerName);
            timer.schedule(endpointValidationTask, DefaultDeploymentConstants.JGROUPS_CLUSTER_SCAN_DELAY, DefaultDeploymentConstants.JGROUPS_CLUSTER_SCAN_PERIOD);
            setClusterMembershipProcessingScheduled(true);
        }
        getLogger().debug(".scheduleEndpointValidation(): Exit");
    }

    //
    // JGroups Group/Cluster Membership Event Listener
    //

    public void processInterfaceAddition(JGroupsNetworkAddress addedInterface){
    }


    public void processInterfaceRemoval(JGroupsNetworkAddress removedInterface) {

    }

    public void processInterfaceSuspect(JGroupsNetworkAddress suspectInterface) {

    }


    protected boolean performEndpointValidationCheck(){
        return(false);
    }

    //
    // Getters and Setters
    //
    protected Logger getLogger(){
        return LOG;
    }

    protected JChannelEndpoint getEndpoint() {
        return (endpoint);
    }

    public JGroupsMembership getMembership() {
        return(membership);
    }

    public void setMembership(JGroupsMembership membership) {
        this.membership = membership;
    }

    public Boolean getClusterMembershipProcessingScheduled(){
        return(this.clusterMembershipProcessingScheduled);
    }

    public void setClusterMembershipProcessingScheduled(Boolean clusterMembershipProcessingScheduled) {
        this.clusterMembershipProcessingScheduled = clusterMembershipProcessingScheduled;
    }
}

