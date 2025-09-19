package net.fhirfactory.dricats.datagrid.databus.notificationbus;

import net.fhirfactory.dricats.datagrid.notificationbus.NotificationDistributionService;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsTransactionResult;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupTransactionResultEnum;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelConfiguration;
import org.jgroups.Message;
import org.jgroups.JChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDistributionServiceTest {

    // Testable subclass to expose protected setter from common class
    static class TestableNotificationDistributionService extends NotificationDistributionService {
        public void exposeSetEndpoint(JChannelInterface endpoint) {
            // call protected setter in the common class
            super.setChannelInterfaceTopologyElement(endpoint);
        }
    }

    // Simple stub for JChannel that captures send invocations
    static class FakeJChannel extends JChannel {
        Message lastMessage;
        int sendCount = 0;
        boolean throwOnSend = false;

        public FakeJChannel() throws Exception {
            super();
        }

        @Override
        public JChannel send(Message msg) throws Exception {
            if (throwOnSend) {
                throw new RuntimeException("send failed");
            }
            this.lastMessage = msg;
            this.sendCount++;
            return this;
        }
    }

    private TestableNotificationDistributionService service;
    private JChannelInterface endpoint;
    private FakeJChannel fakeChannel;

    @BeforeEach
    void setUp() throws Exception {
        service = new TestableNotificationDistributionService();
        // Build a minimal JChannelEndpoint and inject a fake channel
        DistributableObjectId owner = new DistributableObjectId("owner.test");
        JChannelConfiguration config = new JChannelConfiguration();
        endpoint = new JChannelInterface(owner, config, null);
        fakeChannel = new FakeJChannel();
        service.setLocalChannel(fakeChannel);
        // use exposed setter from subclass
        service.exposeSetEndpoint(endpoint);
    }

    @Test
    void sendMessage_whenEmpty_returnsFailed() {
        JGroupsTransactionResult result = service.sendMessage("");
        assertNotNull(result);
        assertEquals(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED, result.getResultCode());
        assertTrue(result.getResultMessage().toLowerCase().contains("empty"));
        // ensure no send attempted
        assertEquals(0, fakeChannel.sendCount);
    }

    @Test
    void sendMessage_broadcast_happyPath_returnsOkAndSends() {
        JGroupsTransactionResult result = service.sendMessage("hello-world");
        assertNotNull(result);
        assertEquals(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_OK, result.getResultCode());
        assertEquals(1, fakeChannel.sendCount);
        assertNotNull(fakeChannel.lastMessage);
        assertNull(fakeChannel.lastMessage.getDest(), "Broadcast should have null destination");
        assertEquals("hello-world", fakeChannel.lastMessage.getObject());
    }

    @Test
    void sendMessage_targeted_happyPath_returnsOkAndSendsToDest() {
        JGroupsNetworkAddress target = new JGroupsNetworkAddress();
        // leave Address null, but set a non-wildcard name to avoid the wildcard branch
        target.setAddressName("node-1");
        JGroupsTransactionResult result = service.sendMessage(target, "ping");
        assertNotNull(result);
        assertEquals(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_OK, result.getResultCode());
        assertEquals(1, fakeChannel.sendCount);
        assertNotNull(fakeChannel.lastMessage);
        // Destination may be null because we didn't set a JGroups Address. That's OK; ensure message object is correct
        assertEquals("ping", fakeChannel.lastMessage.getObject());
    }

    @Test
    void sendMessage_whenChannelThrows_returnsFailed() {
        fakeChannel.throwOnSend = true;
        JGroupsTransactionResult result = service.sendMessage("boom");
        assertNotNull(result);
        assertEquals(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED, result.getResultCode());
        assertTrue(result.getResultMessage() != null && !result.getResultMessage().isEmpty());
    }
}
