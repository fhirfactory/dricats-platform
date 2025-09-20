package net.fhirfactory.dricats.model.messaging;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.messaging.MessageObject;
import net.fhirfactory.dricats.internals.messaging.MessagePayload;
import net.fhirfactory.dricats.internals.messaging.NotificationSubscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSubscriptionTest {

    private DistributableObjectId id(String q1, String v1, String q2, String v2) {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName(q1, v1));
        if (q2 != null) {
            qn.appendUnqualifiedName(new UnqualifiedName(q2, v2));
        }
        return new DistributableObjectId(qn);
    }

    private MessageObject message(String msgId, DistributableObjectId src, DistributableObjectId tgt,
                                  LocalDateTime send, int seq) {
        MessagePayload payload = new MessagePayload();
        payload.setPayloadContent("x");
        MessageObject mo = new MessageObject(msgId, src, tgt, send, payload, seq);
        mo.setMessageReceiveDate(send.plusMinutes(1));
        return mo;
    }

    @Test
    void defaultSubscription_acceptsAnyMessage() {
        NotificationSubscription sub = new NotificationSubscription();
        MessageObject mo = new MessageObject();
        assertTrue(sub.filterNotification(mo));
        assertFalse(sub.filterNotification(null));
    }

    @Test
    void sourceAndTaskMasks_exactMatchAndMismatch() {
        NotificationSubscription sub = new NotificationSubscription();
        DistributableObjectId src = id("TYPE", "A", "NODE", "N1");
        DistributableObjectId task = id("TASK", "B", "NODE", "N2");
        MessageObject mo = message("n1", src, task, LocalDateTime.of(2024,1,1,0,0), 10);

        sub.setNotificationSourceMask(id("TYPE", "A", "NODE", "N1"));
        sub.setNotificationTaskMask(id("TASK", "B", "NODE", "N2"));
        assertTrue(sub.filterNotification(mo));

        sub.setNotificationTaskMask(id("TASK", "B", "NODE", "OTHER"));
        assertFalse(sub.filterNotification(mo));
    }

    @Test
    void wildcardInMasks_isSupported() {
        NotificationSubscription sub = new NotificationSubscription();
        DistributableObjectId src = id("TYPE", "Service", "NODE", "alpha-1");
        DistributableObjectId task = id("TASK", "Handler", "NODE", "beta-2");
        MessageObject mo = message("n2", src, task, LocalDateTime.of(2024,6,1,12,0), 5);

        sub.setNotificationSourceMask(id("TYPE", "Serv*", "NODE", "alpha-*"));
        assertTrue(sub.filterNotification(mo));

        sub.setNotificationSourceMask(id("TYPE", "X*", "NODE", "alpha-*"));
        assertFalse(sub.filterNotification(mo));
    }

    @Test
    void dateWindow_isStrictlyExclusive() {
        NotificationSubscription sub = new NotificationSubscription();
        DistributableObjectId src = id("S", "1", "N", "a");
        DistributableObjectId task = id("T", "2", "N", "b");
        LocalDateTime send = LocalDateTime.of(2024, 3, 10, 10, 0);
        MessageObject mo = message("n3", src, task, send, 1);

        // inside window
        sub.setNotificationsAfterDate(send.minusSeconds(1));
        sub.setNotificationsBeforeDate(send.plusSeconds(1));
        assertTrue(sub.filterNotification(mo));

        // boundary equals are excluded (isAfter/isBefore)
        sub.setNotificationsAfterDate(send);
        sub.setNotificationsBeforeDate(null);
        assertFalse(sub.filterNotification(mo));

        sub.setNotificationsAfterDate(null);
        sub.setNotificationsBeforeDate(send);
        assertFalse(sub.filterNotification(mo));
    }

    @Test
    void idBounds_supportLexCompareAndWildcard() {
        NotificationSubscription sub = new NotificationSubscription();
        DistributableObjectId src = id("S", "1", "N", "a");
        DistributableObjectId task = id("T", "2", "N", "b");
        MessageObject mo = message("note-100", src, task, LocalDateTime.of(2024,1,1,0,0), 0);

        sub.setNotificationIdGreaterThan("note-000");
        sub.setNotificationIdLessThan("note-zzz");
        assertTrue(sub.filterNotification(mo));

        sub.setNotificationIdGreaterThan("note-200");
        assertFalse(sub.filterNotification(mo));

        sub.setNotificationIdGreaterThan("note-1*");
        sub.setNotificationIdLessThan(null);
        assertTrue(sub.filterNotification(mo));

        sub.setNotificationIdGreaterThan("abc*");
        assertFalse(sub.filterNotification(mo));
    }

    @Test
    void sequenceBounds_areStrict() {
        NotificationSubscription sub = new NotificationSubscription();
        MessageObject mo = message("n5", id("S","1","N","a"), id("T","2","N","b"),
                LocalDateTime.of(2024,5,5,5,5), 10);

        sub.setNotificationSequenceNumberGreaterThan(9);
        sub.setNotificationSequenceNumberLessThan(11);
        assertTrue(sub.filterNotification(mo));

        sub.setNotificationSequenceNumberGreaterThan(10); // equal is not allowed
        assertFalse(sub.filterNotification(mo));

        sub.setNotificationSequenceNumberGreaterThan(null);
        sub.setNotificationSequenceNumberLessThan(10); // equal is not allowed
        assertFalse(sub.filterNotification(mo));
    }
}
