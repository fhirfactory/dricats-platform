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
    private Integer port;
    private InetAddress host;
    private String clusterName;
    private JChannel messageChannel;
    private Collection<InetSocketAddress> initialHosts;
    private String lastReceivedMessage;

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

    public void setup(){

        System.out.println("TestMessageServiceCommon.setup(): [Create Protocol Stack] Start");
        Protocol[] protocols = new Protocol[9];
        System.out.println("TestMessageServiceCommon.setup(): [Create Protocol Stack] Finish");

        System.out.println("TestMessageServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Start");
        TCP tcp = new TCP();
        // tcp.setProtocolStack(stack);
        tcp.setBindPort(port);
        tcp.setBindAddress(host);
        protocols[0] = tcp;
        System.out.println("TestMessageServiceCommon.setup(): [Create TCP Protocol & Add To Stack] Finish");

        System.out.println("TestMessageServiceCommon.setup(): [Create PING Protocol & Add To Stack] Start");
        TCPPING ping = new TCPPING();
        ping.setInitialHosts(initialHosts);
        protocols[1] = ping;
        System.out.println("TestMessageServiceCommon.setup(): [Create PING Protocol & Add To Stack] Finish");

        System.out.println("TestMessageServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Start");
        FD_SOCK fdSock = new FD_SOCK();
        //fdSock.setProtocolStack(stack);
        protocols[2] = fdSock;
        System.out.println("TestMessageServiceCommon.setup(): [Create FD_SOCK Protocol & Add To Stack] Finish");
        System.out.println("TestMessageServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Start");
        MERGE3 merge = new MERGE3();
        //merge.setProtocolStack(stack);
        protocols[3] = merge;
        System.out.println("TestMessageServiceCommon.setup(): [Create MERGE3 Protocol & Add To Stack] Finish");

        VERIFY_SUSPECT verifySuspect = new VERIFY_SUSPECT();
        protocols[4] = verifySuspect;

        System.out.println("TestMessageServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Start");
        BARRIER barrier = new BARRIER();
        //barrier.setProtocolStack(stack);
        protocols[5] = barrier;
        System.out.println("TestMessageServiceCommon.setup(): [Create BARRIER Protocol & Add To Stack] Finish");

        NAKACK2 nakack2 = new NAKACK2();
        protocols[6] = nakack2;

        System.out.println("TestMessageServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Start");
        UNICAST3 unicast3 = new UNICAST3();
        // unicast3.setProtocolStack(stack);
        protocols[7] = unicast3;
        System.out.println("TestMessageServiceCommon.setup(): [Create UNICAST3 Protocol & Add To Stack] Finish");

        GMS gms = new GMS();
        protocols[8] = gms;

        System.out.println("TestMessageServiceCommon.setup(): [Initialise JChannel] Start");
        try {
            messageChannel = new JChannel(protocols);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("TestMessageServiceCommon.setup(): [Initialise JChannel] Finish");

        messageChannel.setReceiver(this);

        System.out.println("TestMessageServiceCommon.setup(): [Connect To Cluster] Start");
        try {
            messageChannel.connect(clusterName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("TestMessageServiceCommon.setup(): [Connect To Cluster] Finish");

        System.out.println("TestMessageServiceCommon.setup(): [Channel Details] - IPAddress -> " + getHost().toString());
        System.out.println("TestMessageServiceCommon.setup(): [Channel Details] - Address -> " + messageChannel.getAddressAsString());
        System.out.println("TestMessageServiceCommon.setup(): [Channel Details] - Cluster Name -> " + messageChannel.getClusterName());
        System.out.println("TestMessageServiceCommon.setup(): [Channel Details] - View -> " + messageChannel.getViewAsString());

        System.out.println("TestMessageServiceCommon.setup(): Exit");
    }

    public void sendMessage(String messageContent){
        System.out.println("TestMessageServiceCommon.sendMessage(): Entry");
        System.out.println("TestMessageServiceCommon.sendMessage(): [Create Message from String] Start");
        Message message = new ObjectMessage(null, messageContent);
        System.out.println("TestMessageServiceCommon.sendMessage(): [Create Message from String] Finish");
        System.out.println("TestMessageServiceCommon.sendMessage(): [Send Message] Start");
        try {
            messageChannel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("TestMessageServiceCommon.sendMessage(): [Send Message] FinishØ");
        System.out.println("TestMessageServiceCommon.sendMessage(): Message sent: " + messageContent);
        System.out.println("TestMessageServiceCommon.sendMessage(): Exit");
    }

    public void shutdown(){
        messageChannel.close();
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("TestMessageServiceCommon.viewAccepted():New view -> " + view);
    }

    @Override
    public void receive(Message msg) {
        setLastReceivedMessage(msg.getObject().toString());
        System.out.println("TestMessageServiceCommon.receive(): Received message: " + msg.getObject());
    }
}
