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

import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelConfiguration;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelTCPConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jgroups.JChannel;
import org.jgroups.Receiver;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.stack.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.net.InetAddress;
import java.net.UnknownHostException;

@ApplicationScoped
public class JChannelFactory {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(JChannelFactory.class);

    //
    // Business Methods
    //

    public JChannel createJChannel(JChannelControllerBase controller){
        getLogger().debug(".createJChannel(): Entry, controller->{}", controller);
        if(controller == null){
            getLogger().debug(".createJChannel(): Exit, configurationFile->null");
            return null;
        }
        JChannelConfiguration configObject = null;
        try{
            configObject = controller.getEndpoint().getConfiguration();
        } catch(Exception e){
            getLogger().debug(".createJChannel(): Error getting configuration", e);
            getLogger().debug(".createJChannel(): Exit, cannot establish JChannel, returning null");
            return null;
        }
        try {
            getLogger().trace(".createJChannel(): Creating JChannel");
            getLogger().trace(".createJChannel(): Getting the required ProtocolStack");
            JChannel newChannel;
            if(configObject.isConfigFileBased()){
                newChannel = createJChannelUsingConfigFile(configObject);
            } else {
                newChannel = createJChannelProgrammatically(configObject);
            }
            controller.getEndpoint().setLocalChannel(newChannel);
            getLogger().trace(".createJChannel(): JChannel initialised, now setting JChannel name");
            newChannel.setName(configObject.getChannelName());
            getLogger().trace(".createJChannel(): JChannel Name set, now set ensure we don't get our own messages");
            newChannel.setDiscardOwnMessages(true);
            getLogger().trace(".createJChannel(): [Create RPC Dispatcher] Start");
            RpcDispatcher newRPCDispatcher = new RpcDispatcher(newChannel, controller);
            controller.getEndpoint().setRPCDispatcher(newRPCDispatcher);
            getLogger().trace(".createJChannel(): [Create RPC Dispatcher] Finish");
            getLogger().trace(".createJChannel(): [Set Receiver] Start");
            newChannel.setReceiver(controller);
            getLogger().trace(".createJChannel(): [Set Receiver] Finish");
            getLogger().trace(".createJChannel(): [Connect to Cluster] Start");
            newChannel.connect(configObject.getClusterName());
            getLogger().trace(".createJChannel(): [Connect to Cluster] Finish");
            getLogger().trace(".createJChannel(): Exit, JChannel & RPCDispatcher created");
            return(newChannel);
        } catch (Exception e) {
            getLogger().error(".createJChannel(): Cannot establish JGroups Channel, error->{}", ExceptionUtils.getMessage(e));
            return(null);
        }
    }

    private JChannel createJChannelProgrammatically(JChannelConfiguration configObject){
        getLogger().debug(".createJChannelProgrammatically(): Entry, configObject->{}", configObject);
        if(configObject == null){
            getLogger().debug(".createJChannelProgrammatically(): Exit, configurationFile->null");
            return null;
        }
        if(configObject instanceof JChannelTCPConfiguration){
            getLogger().trace(".createJChannelProgrammatically(): [Create TCP JChannel] Start");
            JChannel createdChannel = createJChannelProgrammatically((JChannelTCPConfiguration) configObject);
            getLogger().trace(".createJChannelProgrammatically(): [Create TCP JChannel] Finish");
            getLogger().debug(".createJChannelProgrammatically(): Exit, createdChannel->{}", createdChannel);
            return createdChannel;
        } else {
            getLogger().trace(".createJChannelProgrammatically(): [Create UDP JChannel] Start");
            getLogger().trace(".createJChannelProgrammatically(): [Create UDP JChannel] Finish");
            getLogger().debug(".createJChannelProgrammatically(): Exit, failed, UDP not supported!");
            return(null);
        }
    }

    private JChannel createJChannelProgrammatically(JChannelTCPConfiguration configurationObject){
        getLogger().debug(".createJChannelProgrammatically(): Entry, configurationObject->{}", configurationObject);
        getLogger().info(".createJChannelProgrammatically(): [Create TCP Protocol] Start");
        TCP tcp = new TCP();
        tcp.setBindPort(configurationObject.getEndpointPort());
        InetAddress inetAddress = null;
        try{
            inetAddress = InetAddress.getByName(configurationObject.getEndpointHost());
        } catch (UnknownHostException e) {
            getLogger().info(".createJChannelProgrammatically(): [Create TCP Protocol] Finish, unable to resolve host, error->{}", e.getMessage());
            getLogger().info(".createJChannelProgrammatically(): Exit, returning null");
            return(null);
        }
        tcp.setBindAddress(inetAddress);
        getLogger().info(".createJChannelProgrammatically(): [Create TCP Protocol & Add To Stack] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create PING Protocol] Start");
        TCPPING ping = new TCPPING();
        ping.setInitialHosts(configurationObject.getInitialHostList());
        ping.setPortRange(20);
        getLogger().info(".createJChannelProgrammatically(): [Create PING Protocol] Finish");

        VERIFY_SUSPECT verifySuspect = new VERIFY_SUSPECT();

        getLogger().info(".createJChannelProgrammatically(): [Create FD_SOCK Protocol] Start");
        FD_SOCK fdSock = new FD_SOCK();
        getLogger().info(".createJChannelProgrammatically(): [Create FD_SOCK Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create MERGE3 Protocol] Start");
        MERGE3 merge = new MERGE3();
        getLogger().info(".createJChannelProgrammatically(): [Create MERGE3 Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create NACKACK2 Protocol] Start");
        NAKACK2 nakack2 = new NAKACK2();
        getLogger().info(".createJChannelProgrammatically(): [Create NACKACK2 Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create BARRIER Protocol] Start");
        BARRIER barrier = new BARRIER();
        getLogger().info(".createJChannelProgrammatically(): [Create BARRIER Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create UNICAST3 Protocol] Start");
        UNICAST3 unicast3 = new UNICAST3();
        getLogger().info(".createJChannelProgrammatically(): [Create UNICAST3 Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create GMS Protocol] Start");
        GMS gms = new GMS();
        getLogger().info(".createJChannelProgrammatically(): [Create GMS Protocol] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Create Protocol Stack] Start");
        Protocol[] protocols = {tcp, ping, verifySuspect, merge, nakack2, barrier, unicast3, gms};
        getLogger().info(".createJChannelProgrammatically(): [Create Protocol Stack] Finish");

        getLogger().info(".createJChannelProgrammatically(): [Initialise JChannel] Start");
        try {
            JChannel messageChannel = new JChannel(protocols);
            getLogger().info(".createJChannelProgrammatically(): [Initialise JChannel] Finish");
            getLogger().info(".createJChannelProgrammatically(): Exit, returning messageChannel->{}", messageChannel);
            return(messageChannel);
        } catch (Exception e) {
            getLogger().info(".createJChannelProgrammatically(): [Initialise JChannel] Finish, creation failed, error->{}", e.getMessage());
            getLogger().info(".createJChannelProgrammatically(): Exit, returning null");
            return(null);
        }
    }

    private JChannel createJChannelUsingConfigFile(JChannelConfiguration configurationObject){
        if(configurationObject == null){
            getLogger().debug(".createJChannelUsingConfigFile(): Exit, configurationObject->null");
            return null;
        }
        if(StringUtils.isEmpty(configurationObject.getConfigFileName())){
            getLogger().debug(".createJChannelUsingConfigFile(): Entry, configurationFile is empty");
            return null;
        }
        try {
            getLogger().trace(".createJChannelUsingConfigFile(): [Creating JChannel] Start");
            JChannel newChannel = new JChannel(configurationObject.getConfigFileName());
            getLogger().trace(".createJChannelUsingConfigFile(): [Creating JChannel] Finish");
            getLogger().trace(".createJChannelUsingConfigFile(): Exit, JChannel created!");
            return(newChannel);
        } catch (Exception e) {
            getLogger().error(".createJChannelUsingConfigFile(): Cannot establish JGroups Channel, error->{}", ExceptionUtils.getMessage(e));
            return(null);
        }
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return LOG;
    }
}
