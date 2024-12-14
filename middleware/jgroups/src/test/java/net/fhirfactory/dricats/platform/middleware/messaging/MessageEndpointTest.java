package net.fhirfactory.dricats.platform.middleware.messaging;

import net.fhirfactory.dricats.platform.middleware.messaging.test.TestMessageReceiver;
import net.fhirfactory.dricats.platform.middleware.messaging.test.TestMessageSender;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageEndpointTest {
    public static final Logger LOG = LoggerFactory.getLogger(MessageEndpointTest.class);

    // Configure JChannel to use TCP and fixed addresses
    private static final String CLUSTER_NAME = "TestCluster";
    private TestMessageReceiver receiver;
    private TestMessageSender messageSender;
    private Collection<InetSocketAddress> initialHosts;

    @BeforeEach
    public void setUp() throws Exception {
        System.out.println("MessageEndpointTest.setUp(): Entry");
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Create Receiver] Start");
        receiver = new TestMessageReceiver();
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Create Receiver] Finish");
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Assign Port] Start");
        receiver.setPort(45000);
        receiver.setHost(InetAddress.getByName("Marks-Air"));
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Assign Port] Finish");
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Assign Cluster Name] Start");
        receiver.setClusterName(CLUSTER_NAME);
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Assign Cluster Name] Finish");
        createInitialHosts("Marks-Air", 45000,45010);
        receiver.setInitialHosts(initialHosts);
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Initialise Receiver] Start");
        receiver.setup();
        System.out.println("MessageEndpointTest.setUp(): [Create MessageReceiver][Initialise Receiver] Finish");

        // Wait for receivers to join the cluster before sending the message
        Thread.sleep(5000);

        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Create Sender] Start");
        messageSender = new TestMessageSender();
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Create Sender] Finish");
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Assign Port] Start");
        messageSender.setPort(45001);
        messageSender.setHost(InetAddress.getByName("Marks-Air"));
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Assign Port] Finish");
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Assign Cluster Name] Start");
        messageSender.setClusterName(CLUSTER_NAME);
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Assign Cluster Name] Finish");
        messageSender.setInitialHosts(initialHosts);
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Initialise Sender] Start");
        messageSender.setup();
        System.out.println("MessageEndpointTest.setUp(): [Create MessageSender][Initialise Sender] Finish");

        // Wait for receivers to join the cluster before sending the message
        Thread.sleep(5000);
    }

    @Test
    public void testMessagePassing() throws Exception {
        String messageContent = "Hello from the JGroups sender to 7801";
        // Run sender in a separate thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            for(int c = 0; c < 5; c++) {
                try {
                    System.out.println("MessageEndpointTest.testReceivers(): [Send Message] Start");
                    messageSender.sendMessage(messageContent);
                    System.out.println("MessageEndpointTest.testReceivers(): [Send Message] Finish");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread.sleep(5000);
        assertTrue(StringUtils.equals(receiver.getLastReceivedMessage(), messageContent));
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Close receiver channels after the test
        receiver.shutdown();
        messageSender.shutdown();
    }

    public Logger getLogger() {
        return LOG;
    }

    public void createInitialHosts(String hostname, Integer startingPort, Integer endingPort){
        initialHosts = new ArrayList<InetSocketAddress>();
        for(Integer port = startingPort; port<=endingPort; port++ ) {
            try {
                initialHosts.add(new InetSocketAddress(Inet4Address.getByName(hostname), port));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
