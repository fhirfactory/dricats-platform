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

import net.fhirfactory.dricats.internals.model.oam.interfaces.LocalMetricsServerInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelEndpoint;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsNamingServices;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JChannelStatusEnum;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.slf4j.Logger;

abstract public class JChannelControllerBase implements Receiver {

    //
    // Attributes
    //
    private JChannelEndpoint endpoint;
    private JGroupsNamingServices namingServices;
    private LocalMetricsServerInterface localMetricsServer;
    private JChannelFactory channelFactory;
    private JChannelMembershipTracker membershipHandler;
    private JChannelWatchdog watchdog;

    //
    // Constructor(s)
    //
    public JChannelControllerBase(
            JChannelEndpoint channelEndpoint,
            JGroupsNamingServices namingServices,
            JChannelFactory channelFactory,
            LocalMetricsServerInterface localMetricsServer) {
        this.endpoint = channelEndpoint;
        this.localMetricsServer = localMetricsServer;
        this.namingServices = namingServices;
        this.channelFactory = channelFactory;
        this.membershipHandler = new JChannelMembershipTracker(channelEndpoint);

        getEndpoint().getConfiguration().setChannelName(specifyChannelName());
        getEndpoint().getConfiguration().setClusterName(specifyClusterName());
    }

    //
    // Abstract Methods
    //

    abstract protected Logger getLogger();
    abstract protected String specifyClusterName();
    abstract protected String specifyChannelName();


    //
    // Business Methods
    //

    @Override
    public void viewAccepted(View view) {
        getLogger().info(".viewAccepted():New view -> " + view);
        getMembershipHandler().viewAccepted(view);
    }

    //
    // Channel Initialization
    //

    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (getEndpoint().getEndpointStatus() != JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED) {
            getLogger().debug(".initialise(): Exit, status is not JGROUPS_ENDPOINT_STATE_UNINITIALISED!");
            return;
        }

        // 1st, Initialize my JChannel
        getLogger().info(".initialise(): Step 1: [JChannel Initialisation] Start");
        getLocalChannelFactory().createJChannel(this);
        getLogger().info(".initialise(): Step 1: [JChannel Initialisation] Finish");

        //
        // 2nd, Update Metrics
        //
        getLogger().info(".initialise(): Step 2: [Update Metrics] Start");
        getEndpoint().getMetricsData().touchLastActivityInstant();
        getLogger().info(".initialise(): Step 2: [Update Metrics] Finish");

    }

    //
    // JChannel Watchdog
    //

    public void channelWatchDog(){

    }

    //
    // Getters and Setters
    //

    protected JChannelEndpoint getEndpoint() {
        return(endpoint);
    }

    protected LocalMetricsServerInterface getLocalMetricsServer() {
        return(localMetricsServer);
    }

    protected JGroupsNamingServices getNamingServices() {
        return(namingServices);
    }

    public JChannelMembershipTracker getMembershipHandler() {
        return membershipHandler;
    }

    protected JChannelFactory getLocalChannelFactory() {
        return channelFactory;
    }

}
