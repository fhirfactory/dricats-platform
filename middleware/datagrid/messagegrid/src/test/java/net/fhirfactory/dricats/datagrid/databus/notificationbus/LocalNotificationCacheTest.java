package net.fhirfactory.dricats.datagrid.databus.notificationbus;

import net.fhirfactory.dricats.datagrid.notificationbus.LocalNotificationCache;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry;
import net.fhirfactory.dricats.internals.messaging.NotificationObject;
import net.fhirfactory.dricats.internals.messaging.NotificationSet;
import net.fhirfactory.dricats.model.topology.implementation.layers.application.TestSubsystemForTests;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class LocalNotificationCacheTest {

    private LocalNotificationCache cache;

    @BeforeEach
    public void setUp() throws Exception {
        cache = new LocalNotificationCache();
        // Inject a stubbed ISubsystem so postNotification() can route locally/remotely
        ISubsystem subsystem = () -> new TestSubsystemForTests("subsystem1");
        Field f = LocalNotificationCache.class.getDeclaredField("subsystem");
        f.setAccessible(true);
        f.set(cache, subsystem);
    }

    @Test
    public void queueIncomingNotification_nullAndMissingTarget_areIgnored() {
        // null notification
        cache.queueIncomingNotification(null);
        assertNull(cache.peekNextNotification(new QualifiedNameToken("<0:subsystem>consumer</0:subsystem>")));

        // notification with null target
        NotificationObject notif = new NotificationObject();
        notif.setNotificationTarget(null);
        cache.queueIncomingNotification(notif);
        assertNull(cache.peekNextNotification(new QualifiedNameToken("<0:subsystem>consumer</0:subsystem>")));
    }

    @Test
    public void incomingQueue_peekAndPoll_perConsumerToken() {
        QualifiedNameToken consumerA = new QualifiedNameToken("<0:subsystem>consumerA</0:subsystem>");
        QualifiedNameToken consumerB = new QualifiedNameToken("<0:subsystem>consumerB</0:subsystem>");

        NotificationObject a1 = new NotificationObject();
        a1.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("consumerA")));
        NotificationObject a2 = new NotificationObject();
        a2.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("consumerA")));
        NotificationObject b1 = new NotificationObject();
        b1.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("consumerB")));

        cache.queueIncomingNotification(a1);
        cache.queueIncomingNotification(a2);
        cache.queueIncomingNotification(b1);

        assertSame(a1, cache.peekNextNotification(consumerA));
        assertSame(a1, cache.pollNextNotification(consumerA));
        assertSame(a2, cache.peekNextNotification(consumerA));
        assertSame(a2, cache.pollNextNotification(consumerA));
        assertNull(cache.pollNextNotification(consumerA));

        assertSame(b1, cache.peekNextNotification(consumerB));
        assertSame(b1, cache.pollNextNotification(consumerB));
        assertNull(cache.pollNextNotification(consumerB));
    }

    @Test
    public void incomingQueue_batchPoll_returnsAtMostSizeAndFIFO() {
        QualifiedNameToken consumer = new QualifiedNameToken("<0:subsystem>batchConsumer</0:subsystem>");
        NotificationObject n1 = new NotificationObject(); n1.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("batchConsumer")));
        NotificationObject n2 = new NotificationObject(); n2.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("batchConsumer")));
        NotificationObject n3 = new NotificationObject(); n3.setNotificationTarget(new DistributableObjectId(qualifiedNameForConsumer("batchConsumer")));
        cache.queueIncomingNotification(n1);
        cache.queueIncomingNotification(n2);
        cache.queueIncomingNotification(n3);

        NotificationSet set = cache.pollNextNotification(consumer, 2);
        assertNotNull(set);
        assertEquals(2, set.getNotificationSequence().size());
        assertSame(n1, set.getNotificationSequence().get(0));
        assertSame(n2, set.getNotificationSequence().get(1));

        // Remaining one
        set = cache.pollNextNotification(consumer, 10);
        assertEquals(1, set.getNotificationSequence().size());
        assertSame(n3, set.getNotificationSequence().get(0));
    }

    @Test
    public void outgoingQueue_queuePeekPoll_FIFO() {
        NotificationObject n1 = new NotificationObject();
        NotificationObject n2 = new NotificationObject();
        cache.queueOutgoingNotification(n1);
        cache.queueOutgoingNotification(n2);
        assertSame(n1, cache.peekOutgoingQueue());
        assertSame(n1, cache.pollOutgoingQueue());
        assertSame(n2, cache.pollOutgoingQueue());
        assertNull(cache.pollOutgoingQueue());
    }

    @Test
    public void postNotification_routesBasedOnTargetSubsystem() throws Exception {
        // Local target: should go to incoming
        NotificationObject local = new NotificationObject();
        local.setNotificationTarget(new DistributableObjectId(qualifiedNameForSubsystem("subsystem1")));
        LocalDateTime before = LocalDateTime.now();
        LocalDateTime posted = cache.postNotification(local);
        assertNotNull(posted);
        assertTrue(!posted.isBefore(before));
        QualifiedNameToken localConsumer = local.getNotificationTarget().getQualifiedName().getQualifiedNameToken();
        assertSame(local, cache.peekNextNotification(localConsumer));

        // Remote target: should go to outgoing
        NotificationObject remote = new NotificationObject();
        remote.setNotificationTarget(new DistributableObjectId(qualifiedNameForSubsystem("subsystem2")));
        cache.postNotification(remote);
        assertSame(remote, cache.peekOutgoingQueue());
    }

    private QualifiedName qualifiedNameForConsumer(String consumerName) {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedNameEntry("subsystem", consumerName));
        return qn;
    }

    private QualifiedName qualifiedNameForSubsystem(String subsystemName) {
        // To exercise postNotification routing, we need the last element to be SUBSYSTEM_APPLICATION_INSTANCE
        // but for comparison in LocalNotificationCache it extracts by the qualifier string value from enum.
        // The code extracts using SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType(), which equals "subsystem".
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedNameEntry("Solution", "sol"));
        qn.appendUnqualifiedName(new UnqualifiedNameEntry("SubsystemApplicationInstance", subsystemName));
        return qn;
    }

}
