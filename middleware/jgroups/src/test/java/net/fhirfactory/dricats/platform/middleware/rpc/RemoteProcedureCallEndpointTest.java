package net.fhirfactory.dricats.platform.middleware.rpc;

import net.fhirfactory.dricats.platform.middleware.rpc.test.TestRPCClient;
import net.fhirfactory.dricats.platform.middleware.rpc.test.TestRPCServer;
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

public class RemoteProcedureCallEndpointTest {
    public static final Logger LOG = LoggerFactory.getLogger(RemoteProcedureCallEndpointTest.class);

    // Configure JChannel to use TCP and fixed addresses
    private TestRPCServer server;
    private TestRPCClient client;
    private Collection<InetSocketAddress> initialHosts;

    @BeforeEach
    public void setUp() throws Exception {
        LOG.debug("RemoteProcedureCallEndpointTest.setUp(): Entry");
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Create Receiver] Start");
        server = new TestRPCServer();
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Create Receiver] Finish");
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Assign Port] Start");
        server.setPort(45000);
        server.setHost(InetAddress.getByName("localhost"));
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Assign Port] Finish");
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Assign Cluster Name] Start");
        server.setClusterName(TestRPCServer.TEST_CLUSTER_NAME);
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Assign Cluster Name] Finish");
        createInitialHosts("localhost", 45000,45010);
        server.setInitialHosts(initialHosts);
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Initialise Receiver] Start");
        server.setup();
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageReceiver][Initialise Receiver] Finish");

        // Wait for clients to join the cluster before do anything
        Thread.sleep(5000);

        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Create Sender] Start");
        client = new TestRPCClient();
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Create Sender] Finish");
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Assign Port] Start");
        client.setPort(45001);
        client.setHost(InetAddress.getByName("localhost"));
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Assign Port] Finish");
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Assign Cluster Name] Start");
        client.setClusterName(TestRPCServer.TEST_CLUSTER_NAME);
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Assign Cluster Name] Finish");
        client.setInitialHosts(initialHosts);
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Initialise Sender] Start");
        client.setup();
        LOG.trace("RemoteProcedureCallEndpointTest.setUp(): [Create MessageSender][Initialise Sender] Finish");

        // Wait for receivers to join the cluster before sending the message
        Thread.sleep(5000);
    }

    @Test
    public void testRPCInvocation() throws Exception {
        LOG.debug("RemoteProcedureCallEndpointTest.testRPCInvocation(): Entry");
        String messageContent = "RPC Call";
        // Run sender in a separate thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                LOG.trace("RemoteProcedureCallEndpointTest.testRPCInvocation(): [Make RPC Call] Start");
                String response = client.makeRPC(messageContent);
                LOG.trace("RemoteProcedureCallEndpointTest.testRPCInvocation(): [Make RPC Call] response -> {}", response);
                assertTrue(StringUtils.equals(response, TestRPCServer.TEST_RESPONSE_PREFIX + messageContent));
                LOG.trace("RemoteProcedureCallEndpointTest.testRPCInvocation(): [Make RPC Call] Finish");

            } catch (Exception e) {
                LOG.error("RemoteProcedureCallEndpointTest.testRPCInvocation(): [Make RPC Call] Error", e);
            }
        });
        LOG.debug("RemoteProcedureCallEndpointTest.testMessagePassing(): Exit");
    }

    @AfterEach
    public void tearDown() throws Exception {
        // Close receiver channels after the test
        server.shutdown();
        client.shutdown();
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
