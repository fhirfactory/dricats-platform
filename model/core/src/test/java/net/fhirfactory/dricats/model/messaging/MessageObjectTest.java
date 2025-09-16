package net.fhirfactory.dricats.model.messaging;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.common.naming.CommonName;
import net.fhirfactory.dricats.model.common.naming.QualifiedName;
import net.fhirfactory.dricats.model.common.naming.UnqualifiedName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageObjectTest {

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
        MessageObject mo = new MessageObject();

        // IDs
        assertNotNull(mo.getMessageId());
        assertFalse(mo.getMessageId().isEmpty());
        assertNotNull(mo.getId());
        assertTrue(mo.getId().getValue().startsWith("Message("));
        assertTrue(mo.getId().getValue().contains(mo.getMessageId()));

        // Dates
        assertNotNull(mo.getMessageReceiveDate());
        assertEquals(LocalDateTime.MIN, mo.getMessageSendDate());

        // Defaults
        assertEquals(-1, mo.getMessageSequenceNumber());
        assertNotNull(mo.getMessageSource());
        assertNotNull(mo.getMessageTarget());
        assertNotNull(mo.getMessagePayload());

        // toString
        assertNotNull(mo.toString());
        assertFalse(mo.toString().isEmpty());
    }

    @Test
    void parameterizedConstructor_setsFieldsCorrectly() {
        DistributableObjectId source = buildId("S", "1", null, null);
        DistributableObjectId target = buildId("T", "2", null, null);
        LocalDateTime send = LocalDateTime.of(2023, 1, 2, 3, 4, 5);
        MessagePayload payload = new MessagePayload();
        payload.setPayloadContent("hello");

        MessageObject mo = new MessageObject(source, target, send, payload);

        assertEquals(source, mo.getMessageSource());
        assertEquals(target, mo.getMessageTarget());
        assertEquals(send, mo.getMessageSendDate());
        assertEquals(0, mo.getMessageSequenceNumber());
        assertEquals(payload, mo.getMessagePayload());
        assertNotNull(mo.getMessageId());
        assertTrue(mo.getId().getValue().startsWith("Message("));
    }

    @Test
    void parameterizedConstructor_withExplicitIdAndSequence() {
        DistributableObjectId source = buildId("S", "1", null, null);
        DistributableObjectId target = buildId("T", "2", null, null);
        LocalDateTime send = LocalDateTime.of(2024, 12, 31, 23, 59);
        MessagePayload payload = new MessagePayload();
        String id = "abc-123";
        int seq = 42;

        MessageObject mo = new MessageObject(id, source, target, send, payload, seq);

        assertEquals(id, mo.getMessageId());
        assertEquals(seq, mo.getMessageSequenceNumber());
        assertEquals(send, mo.getMessageSendDate());
        assertEquals(source, mo.getMessageSource());
        assertEquals(target, mo.getMessageTarget());
        assertTrue(mo.getId().getValue().contains(id));
    }

    @Test
    void settersAndGetters_workAsExpected() {
        MessageObject mo = new MessageObject();
        DistributableObjectId source = buildId("A", "a", null, null);
        DistributableObjectId target = buildId("B", "b", null, null);
        LocalDateTime send = LocalDateTime.of(2022, 2, 2, 2, 2);
        LocalDateTime recv = LocalDateTime.of(2022, 2, 2, 3, 3);
        MessagePayload payload = new MessagePayload();
        payload.setDocumentation("desc");

        mo.setMessageSource(source);
        mo.setMessageTarget(target);
        mo.setMessageSendDate(send);
        mo.setMessageReceiveDate(recv);
        mo.setMessageId("id-1");
        mo.setMessageSequenceNumber(7);
        mo.setMessagePayload(payload);
        mo.setId(new CommonName("Message(id-1)"));

        assertEquals(source, mo.getMessageSource());
        assertEquals(target, mo.getMessageTarget());
        assertEquals(send, mo.getMessageSendDate());
        assertEquals(recv, mo.getMessageReceiveDate());
        assertEquals("id-1", mo.getMessageId());
        assertEquals(7, mo.getMessageSequenceNumber());
        assertEquals(payload, mo.getMessagePayload());
        assertEquals("Message(id-1)", mo.getId().getValue());
    }
}
