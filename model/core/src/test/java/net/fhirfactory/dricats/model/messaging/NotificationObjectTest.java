package net.fhirfactory.dricats.model.messaging;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.messaging.NotificationObject;
import net.fhirfactory.dricats.internals.messaging.NotificationPayload;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class NotificationObjectTest {

    private DistributableObjectId buildId(String q1, String v1, String q2, String v2) {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName(q1, v1));
        if (q2 != null) {
            qn.appendUnqualifiedName(new UnqualifiedName(q2, v2));
        }
        return new DistributableObjectId(qn);
    }

    @Test
    void defaultConstructor_setsDefaultsAndIds() {
        NotificationObject no = new NotificationObject();

        // IDs
        assertNotNull(no.getNotificationId());
        assertFalse(no.getNotificationId().isEmpty());
        assertNotNull(no.getId());
        assertTrue(no.getId().getValue().startsWith("Notification("));
        assertTrue(no.getId().getValue().contains(no.getNotificationId()));

        // Dates
        assertNotNull(no.getNotificationReceiveDate());
        assertEquals(LocalDateTime.MIN, no.getNotificationSendDate());

        // Defaults
        assertEquals(-1, no.getNotificationSequenceNumber());
        assertNotNull(no.getNotificationSource());
        assertNotNull(no.getNotificationTarget());
        assertNotNull(no.getNotificationPayload());

        // toString
        assertNotNull(no.toString());
        assertFalse(no.toString().isEmpty());
    }

    @Test
    void parameterizedConstructor_setsFieldsCorrectly() {
        DistributableObjectId source = buildId("S", "1", null, null);
        DistributableObjectId target = buildId("T", "2", null, null);
        LocalDateTime send = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        NotificationPayload payload = new NotificationPayload();
        payload.setNotificationPayloadTypeName("TYPE");

        NotificationObject no = new NotificationObject(source, target, send, payload);

        assertEquals(source, no.getNotificationSource());
        assertEquals(target, no.getNotificationTarget());
        assertEquals(send, no.getNotificationSendDate());
        assertEquals(0, no.getNotificationSequenceNumber());
        assertEquals(payload, no.getNotificationPayload());
        assertNotNull(no.getNotificationId());
        assertTrue(no.getId().getValue().startsWith("Notification("));
    }

    @Test
    void parameterizedConstructor_withExplicitIdAndSequence() {
        DistributableObjectId source = buildId("S", "1", null, null);
        DistributableObjectId target = buildId("T", "2", null, null);
        LocalDateTime send = LocalDateTime.of(2024, 12, 31, 23, 59);
        NotificationPayload payload = new NotificationPayload();
        payload.setNotificationPayloadDetail(new HashMap<>());
        String id = "notif-123";
        int seq = 77;

        NotificationObject no = new NotificationObject(id, source, target, send, payload, seq);

        assertEquals(id, no.getNotificationId());
        assertEquals(seq, no.getNotificationSequenceNumber());
        assertEquals(send, no.getNotificationSendDate());
        assertEquals(source, no.getNotificationSource());
        assertEquals(target, no.getNotificationTarget());
        assertTrue(no.getId().getValue().contains(id));
    }

    @Test
    void settersAndGetters_workAsExpected() {
        NotificationObject no = new NotificationObject();
        DistributableObjectId source = buildId("A", "a", null, null);
        DistributableObjectId target = buildId("B", "b", null, null);
        LocalDateTime send = LocalDateTime.of(2022, 2, 2, 2, 2);
        LocalDateTime recv = LocalDateTime.of(2022, 2, 2, 3, 3);
        NotificationPayload payload = new NotificationPayload();
        payload.setNotificationPayloadTypeDescription("desc");

        no.setNotificationSource(source);
        no.setNotificationTarget(target);
        no.setNotificationSendDate(send);
        no.setNotificationReceiveDate(recv);
        no.setNotificationId("id-2");
        no.setNotificationSequenceNumber(5);
        no.setNotificationPayload(payload);
        no.setId(new CommonName("Notification(id-2)"));

        assertEquals(source, no.getNotificationSource());
        assertEquals(target, no.getNotificationTarget());
        assertEquals(send, no.getNotificationSendDate());
        assertEquals(recv, no.getNotificationReceiveDate());
        assertEquals("id-2", no.getNotificationId());
        assertEquals(5, no.getNotificationSequenceNumber());
        assertEquals(payload, no.getNotificationPayload());
        assertEquals("Notification(id-2)", no.getId().getValue());
    }
}
