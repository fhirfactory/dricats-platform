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
package net.fhirfactory.dricats.platform.middleware.rpc.test;

import org.jgroups.*;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.stack.Protocol;
import org.jgroups.util.RspList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

public class TestRemoteProcedureCallServiceCommon implements Receiver {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TestRemoteProcedureCallServiceCommon.class);
    
    //
    // Attributes
    //
    public static final String TEST_CLUSTER_NAME = "TEST_RPC_CLUSTER";
    public static final String TEST_RESPONSE_PREFIX = "TestResponse: ";

    private Integer port;
    private InetAddress host;
    private String clusterName;
    private JChannel remoteProcedureCallChannel;
    private Collection<InetSocketAddress> initialHosts;
    private String lastReceivedMessage;
    private RpcDispatcher rpcDispatcher;
    private RequestOptions options;

    //
    // Getters & Setters
    //
    public void setPort(Integer port) {
        this.port = port;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setInitialHosts(Collection<InetSocketAddress> initialHosts) {
        this.initialHosts = initialHosts;
    }

    public InetAddress getHost() {
        return host;
    }

    public void setHost(InetAddress host) {
        this.host = host;
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void setLastReceivedMessage(String lastReceivedMessage) {
        this.lastReceivedMessage = lastReceivedMessage;
    }

    public RpcDispatcher getRpcDispatcher() {
        return rpcDispatcher;
    }

    public void setRpcDispatcher(RpcDispatcher rpcDispatcher) {
        this.rpcDispatcher = rpcDispatcher;
    }

    public RequestOptions getOptions() {
        return options;
    }

    public void setOptions(RequestOptions options) {
        this.options = options;
    }

    //
    // Business Methods
    //
    public void setup(){
        LOG.debug("TestRemoteProcedureCallServiceCommon.setup(): Entry");
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create Protocol Stack] Start");
        Protocol[] protocols = new Protocol[9];
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create Protocol Stack] Finish");

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Start");
        TCP tcp = new TCP();
        // tcp.setProtocolStack(stack);
        tcp.setBindPort(port);
        tcp.setBindAddress(host);
        protocols[0] = tcp;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Finish");

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create PING Protocol & Add To Stack] Start");
        TCPPING ping = new TCPPING();
        ping.setInitialHosts(initialHosts);
        protocols[1] = ping;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create PING Protocol & Add To Stack] Finish");

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Start");
        FD_SOCK fdSock = new FD_SOCK();
        //fdSock.setProtocolStack(stack);
        protocols[2] = fdSock;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Finish");
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Start");
        MERGE3 merge = new MERGE3();
        //merge.setProtocolStack(stack);
        protocols[3] = merge;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Finish");

        VERIFY_SUSPECT verifySuspect = new VERIFY_SUSPECT();
        protocols[4] = verifySuspect;

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Start");
        BARRIER barrier = new BARRIER();
        //barrier.setProtocolStack(stack);
        protocols[5] = barrier;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Finish");

        NAKACK2 nakack2 = new NAKACK2();
        protocols[6] = nakack2;

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Start");
        UNICAST3 unicast3 = new UNICAST3();
        // unicast3.setProtocolStack(stack);
        protocols[7] = unicast3;
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Finish");

        GMS gms = new GMS();
        protocols[8] = gms;

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Initialise JChannel] Start");
        try {
            MethodCall call = new MethodCall(getClass().getMethod("testRPC", String.class));
            setOptions(new RequestOptions(ResponseMode.GET_ALL, 5000));
            remoteProcedureCallChannel = new JChannel(protocols);
            rpcDispatcher = new RpcDispatcher(remoteProcedureCallChannel, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Initialise JChannel] Finish");

        remoteProcedureCallChannel.setReceiver(this);

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Connect To Cluster] Start");
        try {
            remoteProcedureCallChannel.connect(clusterName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Connect To Cluster] Finish");

        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Channel Details] - IPAddress -> " + getHost().toString());
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Channel Details] - Address -> " + remoteProcedureCallChannel.getAddressAsString());
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Channel Details] - Cluster Name -> " + remoteProcedureCallChannel.getClusterName());
        LOG.trace("TestRemoteProcedureCallServiceCommon.setup(): [Channel Details] - View -> " + remoteProcedureCallChannel.getViewAsString());
        LOG.debug("TestRemoteProcedureCallServiceCommon.setup(): Exit");
    }

    public String makeRPC(String testString){
        LOG.debug("TestRemoteProcedureCallServiceCommon.makeRPC(): Entry");
        try {
            RspList<String> rsp_list = getRpcDispatcher().callRemoteMethods(null,"testRPC", new Object[]{testString}, new Class[]{String.class}, getOptions());
            return rsp_list.getFirst();
        } catch (Exception e) {
            LOG.error("TestRemoteProcedureCallServiceCommon.makeRPC(): Error making RPC call", e);
            return "Error making RPC call";
        }
    }
    
    public String testRPC(String testString){
        LOG.debug("TestRemoteProcedureCallServiceCommon.testRCP(): Entry");
        LOG.trace("TestRemoteProcedureCallServiceCommon.testRCP(): Received test string: " + testString);
        String response = TEST_RESPONSE_PREFIX + testString;
        LOG.trace("TestRemoteProcedureCallServiceCommon.testRCP(): Sending test response: " + response);
        LOG.debug("TestRemoteProcedureCallServiceCommon.testRCP(): Exit");
        return response;
    }

    public void shutdown(){
        remoteProcedureCallChannel.close();
    }

    @Override
    public void viewAccepted(View view) {
        LOG.info("TestRemoteProcedureCallServiceCommon.viewAccepted():New view -> " + view);
    }

    @Override
    public void receive(Message msg) {
        setLastReceivedMessage(msg.getObject().toString());
        LOG.trace("TestRemoteProcedureCallServiceCommon.receive(): Received message: " + msg.getObject());
    }
}
