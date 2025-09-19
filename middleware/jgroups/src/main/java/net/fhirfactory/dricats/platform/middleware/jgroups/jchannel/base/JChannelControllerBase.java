/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
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

import net.fhirfactory.dricats.internals.oam.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.platform.configuration.LocalConfigurationServer;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsNamingServices;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JChannelStatusEnum;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.RpcDispatcher;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

abstract public class JChannelControllerBase extends ApplicationComponent implements Receiver {

    //
    // Attributes
    //
    private JChannelInterface channelInterfaceTopologyElement;
    private JChannelMembershipTracker membershipHandler;
    private JChannelWatchdog watchdog;
    private Boolean initialised;
    private JChannel localChannel;
    private RpcDispatcher rpcDispatcher;
    private Object localChannelLock;

    @Inject
    private JGroupsNamingServices namingServices;

    @Inject
    private ILocalMetricsServerInterface localMetricsServer;

    @Inject
    private JChannelFactory channelFactory;

    @Inject
    private LocalConfigurationServer configurationServer;

    @Inject
    private ISubsystem subsystem;

    //
    // Constructor(s)
    //
    public JChannelControllerBase() {
        super();
        setInitialised(false);
    }

    //
    // Abstract Methods
    //

    abstract protected Logger getLogger();
    abstract protected JChannelInterface resolveEndpoint();
    abstract protected void populateIdentityDetails();


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

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initialise(): Entry");

        getLogger().info(".initialise(): [Populate Identity Details] Start");
        populateIdentityDetails();
        getLogger().info(".initialise(): [Populate Identity Details] Finish");

        getLogger().info(".initialise(): [Resolve Endpoint] Start");
        setChannelInterfaceTopologyElement(resolveEndpoint());
        getLogger().info(".initialise(): [Resolve Endpoint] Finish");

        if (getChannelInterfaceTopologyElement().getEndpointStatus() != JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED) {
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
        getChannelInterfaceTopologyElement().getMetricsData().touchLastActivityInstant();
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

    protected LocalConfigurationServer getConfigurationServer(){
        return(this.configurationServer);
    }

    public Boolean getInitialised() {
        return initialised;
    }

    public void setInitialised(Boolean initialised) {
        this.initialised = initialised;
    }

    protected JChannelInterface getChannelInterfaceTopologyElement(){
        return(this.channelInterfaceTopologyElement);
    }

    // Minimal accessor to satisfy factory and other components expecting this name
    public JChannelInterface getEndpoint() {
        return getChannelInterfaceTopologyElement();
    }

    protected void setChannelInterfaceTopologyElement(JChannelInterface endpointTopologyElement){
        this.channelInterfaceTopologyElement = endpointTopologyElement;
    }

    protected ILocalMetricsServerInterface getLocalMetricsServer() {
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

    protected Object getLocalChannelLock(){
        return(this.localChannelLock);
    }

    protected ISubsystem getSubsystem(){
        return(subsystem);
    }

    //
    // JGroups Interface Methods
    //

    public List<Address> getAllViewMembers() {
        if ((getLocalChannel() == null) || (getLocalChannel().getView() == null)) {
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

}
