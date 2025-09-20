package net.fhirfactory.dricats.model.messaging;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.messaging.MessageObject;
import net.fhirfactory.dricats.internals.messaging.MessagePayload;
import net.fhirfactory.dricats.internals.messaging.MessageSubscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageSubscriptionTest {

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
        // also assign a receive date after send for completeness
        mo.setMessageReceiveDate(send.plusMinutes(1));
        return mo;
    }

    @Test
    void defaultSubscription_acceptsAnyMessage() {
        MessageSubscription sub = new MessageSubscription();
        MessageObject mo = new MessageObject();
        assertTrue(sub.filterMessage(mo));
        assertFalse(sub.filterMessage(null));
    }

    @Test
    void sourceAndTargetMasks_exactMatchAndMismatch() {
        MessageSubscription sub = new MessageSubscription();
        DistributableObjectId src = id("TYPE", "A", "NODE", "N1");
        DistributableObjectId tgt = id("TYPE", "B", "NODE", "N2");
        MessageObject mo = message("m1", src, tgt, LocalDateTime.of(2024,1,1,0,0), 10);

        sub.setMessageSourceMask(id("TYPE", "A", "NODE", "N1"));
        sub.setMessageTargetMask(id("TYPE", "B", "NODE", "N2"));
        assertTrue(sub.filterMessage(mo));

        sub.setMessageTargetMask(id("TYPE", "B", "NODE", "OTHER"));
        assertFalse(sub.filterMessage(mo));
    }

    @Test
    void wildcardInMasks_isSupported() {
        MessageSubscription sub = new MessageSubscription();
        DistributableObjectId src = id("TYPE", "Service", "NODE", "alpha-1");
        DistributableObjectId tgt = id("TYPE", "Client", "NODE", "beta-2");
        MessageObject mo = message("m2", src, tgt, LocalDateTime.of(2024,6,1,12,0), 5);

        // wildcard in source value
        sub.setMessageSourceMask(id("TYPE", "Serv*", "NODE", "alpha-*"));
        assertTrue(sub.filterMessage(mo));

        // wildcard that should not match
        sub.setMessageSourceMask(id("TYPE", "X*", "NODE", "alpha-*"));
        assertFalse(sub.filterMessage(mo));
    }

    @Test
    void dateWindow_isStrictlyExclusive() {
        MessageSubscription sub = new MessageSubscription();
        DistributableObjectId src = id("S", "1", "N", "a");
        DistributableObjectId tgt = id("T", "2", "N", "b");
        LocalDateTime send = LocalDateTime.of(2024, 3, 10, 10, 0);
        MessageObject mo = message("m3", src, tgt, send, 1);

        // inside window
        sub.setMessagesAfterDate(send.minusSeconds(1));
        sub.setMessagesBeforeDate(send.plusSeconds(1));
        assertTrue(sub.filterMessage(mo));

        // boundary equals are excluded (isAfter/isBefore)
        sub.setMessagesAfterDate(send);
        sub.setMessagesBeforeDate(null);
        assertFalse(sub.filterMessage(mo));

        sub.setMessagesAfterDate(null);
        sub.setMessagesBeforeDate(send);
        assertFalse(sub.filterMessage(mo));
    }

    @Test
    void idBounds_supportLexCompareAndWildcard() {
        MessageSubscription sub = new MessageSubscription();
        DistributableObjectId src = id("S", "1", "N", "a");
        DistributableObjectId tgt = id("T", "2", "N", "b");
        MessageObject mo = message("msg-100", src, tgt, LocalDateTime.of(2024,1,1,0,0), 0);

        // lexicographic compare
        sub.setMessageIdGreaterThan("msg-000");
        sub.setMessageIdLessThan("msg-zzz");
        assertTrue(sub.filterMessage(mo));

        sub.setMessageIdGreaterThan("msg-200");
        assertFalse(sub.filterMessage(mo));

        // wildcard path: when pattern contains '*', it uses wildcardMatch
        sub.setMessageIdGreaterThan("msg-1*");
        sub.setMessageIdLessThan(null);
        assertTrue(sub.filterMessage(mo));

        // wildcard not matching
        sub.setMessageIdGreaterThan("abc*");
        assertFalse(sub.filterMessage(mo));
    }

    @Test
    void sequenceBounds_areStrict() {
        MessageSubscription sub = new MessageSubscription();
        MessageObject mo = message("m5", id("S","1","N","a"), id("T","2","N","b"),
                LocalDateTime.of(2024,5,5,5,5), 10);

        sub.setMessageSequenceNumberGreaterThan(9);
        sub.setMessageSequenceNumberLessThan(11);
        assertTrue(sub.filterMessage(mo));

        sub.setMessageSequenceNumberGreaterThan(10); // equal is not allowed
        assertFalse(sub.filterMessage(mo));

        sub.setMessageSequenceNumberGreaterThan(null);
        sub.setMessageSequenceNumberLessThan(10); // equal is not allowed
        assertFalse(sub.filterMessage(mo));
    }
}
