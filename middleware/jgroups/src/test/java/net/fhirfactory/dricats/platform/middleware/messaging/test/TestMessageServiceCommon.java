package net.fhirfactory.dricats.platform.middleware.messaging.test;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.stack.Protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

public class TestMessageServiceCommon implements Receiver {
    //
     // Housekeeping
    //
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestMessageServiceCommon.class);

    //
    // Attributes
    //
    public static final String TEST_CLUSTER_NAME = "TEST_MESSAGE_CLUSTER";

    private Integer port;
    private InetAddress host;
    private String clusterName;
    private JChannel messageChannel;
    private Collection<InetSocketAddress> initialHosts;
    private String lastReceivedMessage;

    //
    // Getters & Setters
    //
    public Integer getPort() {
        return port;
    }
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

    //
    // Business Methods
    //
    public void setup(){
        LOG.debug("TestMessageServiceCommon.setup(): Entry");

        LOG.trace("TestMessageServiceCommon.setup(): [Create Protocol Stack] Start");
        Protocol[] protocols = new Protocol[9];
        LOG.trace("TestMessageServiceCommon.setup(): [Create Protocol Stack] Finish");

        LOG.trace("TestMessageServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Start");
        TCP tcp = new TCP();
        // tcp.setProtocolStack(stack);
        tcp.setBindPort(port);
        tcp.setBindAddress(host);
        protocols[0] = tcp;
        LOG.trace("TestMessageServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Finish");

        LOG.trace("TestMessageServiceCommon.setup(): [Create PING Protocol & Add To Stack] Start");
        TCPPING ping = new TCPPING();
        ping.setInitialHosts(initialHosts);
        protocols[1] = ping;
        LOG.trace("TestMessageServiceCommon.setup(): [Create PING Protocol & Add To Stack] Finish");

        LOG.trace("TestMessageServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Start");
        FD_SOCK fdSock = new FD_SOCK();
        //fdSock.setProtocolStack(stack);
        protocols[2] = fdSock;
        LOG.trace("TestMessageServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Finish");
        LOG.trace("TestMessageServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Start");
        MERGE3 merge = new MERGE3();
        //merge.setProtocolStack(stack);
        protocols[3] = merge;
        LOG.trace("TestMessageServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Finish");

        VERIFY_SUSPECT verifySuspect = new VERIFY_SUSPECT();
        protocols[4] = verifySuspect;

        LOG.trace("TestMessageServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Start");
        BARRIER barrier = new BARRIER();
        //barrier.setProtocolStack(stack);
        protocols[5] = barrier;
        LOG.trace("TestMessageServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Finish");

        NAKACK2 nakack2 = new NAKACK2();
        protocols[6] = nakack2;

        LOG.trace("TestMessageServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Start");
        UNICAST3 unicast3 = new UNICAST3();
        // unicast3.setProtocolStack(stack);
        protocols[7] = unicast3;
        LOG.trace("TestMessageServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Finish");

        GMS gms = new GMS();
        protocols[8] = gms;

        LOG.trace("TestMessageServiceCommon.setup(): [Initialise JChannel] Start");
        try {
            messageChannel = new JChannel(protocols);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.trace("TestMessageServiceCommon.setup(): [Initialise JChannel] Finish");

        messageChannel.setReceiver(this);

        LOG.trace("TestMessageServiceCommon.setup(): [Connect To Cluster] Start");
        try {
            messageChannel.connect(clusterName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOG.trace("TestMessageServiceCommon.setup(): [Connect To Cluster] Finish");

        LOG.trace("TestMessageServiceCommon.setup(): [Channel Details] - IPAddress -> " + getHost().toString());
        LOG.trace("TestMessageServiceCommon.setup(): [Channel Details] - Address -> " + messageChannel.getAddressAsString());
        LOG.trace("TestMessageServiceCommon.setup(): [Channel Details] - Cluster Name -> " + messageChannel.getClusterName());
        LOG.trace("TestMessageServiceCommon.setup(): [Channel Details] - View -> " + messageChannel.getViewAsString());

        LOG.debug("TestMessageServiceCommon.setup(): Exit");
    }

    public void sendMessage(String messageContent){
        LOG.debug("TestMessageServiceCommon.sendMessage(): Entry");
        LOG.trace("TestMessageServiceCommon.sendMessage(): [Create Message from String] Start");
        Message message = new ObjectMessage(null, messageContent);
        LOG.trace("TestMessageServiceCommon.sendMessage(): [Create Message from String] Finish");
        LOG.trace("TestMessageServiceCommon.sendMessage(): [Send Message] Start");
        try {
            messageChannel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.trace("TestMessageServiceCommon.sendMessage(): [Send Message] FinishØ");
        LOG.trace("TestMessageServiceCommon.sendMessage(): Message sent: " + messageContent);
        LOG.debug("TestMessageServiceCommon.sendMessage(): Exit");
    }

    public void shutdown(){
        messageChannel.close();
    }

    @Override
    public void viewAccepted(View view) {
        LOG.trace("TestMessageServiceCommon.viewAccepted():New view -> " + view);
    }

    @Override
    public void receive(Message msg) {
        setLastReceivedMessage(msg.getObject().toString());
        LOG.trace("TestMessageServiceCommon.receive(): Received message: " + msg.getObject());
    }
}
