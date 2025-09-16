/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.databus.messagebus;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.common.naming.QualifiedName;
import net.fhirfactory.dricats.model.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.model.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.model.messaging.MessageObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class LocalMessageCacheTest {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(LocalMessageCacheTest.class);

    //
    // Attributes
    //
    private LocalMessageCache cache;

    //
    // Business Methods
    //
    @BeforeEach
    void setUp() {
        cache = new LocalMessageCache();
    }

    private QualifiedNameToken buildToken(String qualifier1, String value1, String qualifier2, String value2) {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName(qualifier1, value1));
        if (qualifier2 != null) {
            qn.appendUnqualifiedName(new UnqualifiedName(qualifier2, value2));
        }
        return new QualifiedNameToken(qn);
    }

    private MessageObject buildMessageForTarget(QualifiedNameToken token, String messageId) {
        MessageObject mo = new MessageObject();
        mo.setMessageId(messageId);
        // DistributableObjectId can be constructed from a token string
        DistributableObjectId target = new DistributableObjectId(token.getContent());
        mo.setMessageTarget(target);
        return mo;
    }

    @Test
    void queueIncomingMessage_nullsAreIgnored() {
        // null message ignored
        cache.queueIncomingMessage(null);
        assertNull(cache.peekNextMessage(new QualifiedNameToken("<0:X>y</0:X>")));

        // message with null target ignored
        MessageObject noTarget = new MessageObject();
        noTarget.setMessageId("no-target");
        cache.queueIncomingMessage(noTarget);
        assertNull(cache.peekNextMessage(new QualifiedNameToken("<0:X>y</0:X>")));
    }

    @Test
    void incomingQueue_routeByQualifiedNameToken_peekAndPoll() {
        QualifiedNameToken token = buildToken("A", "1", "B", "2");
        MessageObject m1 = buildMessageForTarget(token, "m1");
        MessageObject m2 = buildMessageForTarget(token, "m2");

        cache.queueIncomingMessage(m1);
        cache.queueIncomingMessage(m2);

        // Peek should be first-in and not remove
        MessageObject peeked = cache.peekNextMessage(token);
        assertNotNull(peeked);
        assertEquals("m1", peeked.getMessageId());

        // Poll twice should return m1 then m2 then null
        MessageObject polled1 = cache.pollNextMessage(token);
        assertNotNull(polled1);
        assertEquals("m1", polled1.getMessageId());

        MessageObject polled2 = cache.pollNextMessage(token);
        assertNotNull(polled2);
        assertEquals("m2", polled2.getMessageId());

        assertNull(cache.pollNextMessage(token));
    }

    @Test
    void incomingQueue_separateTokensHaveSeparateQueues() {
        QualifiedNameToken tokenA = buildToken("T", "A", null, null);
        QualifiedNameToken tokenB = buildToken("T", "B", null, null);

        MessageObject mA1 = buildMessageForTarget(tokenA, "a1");
        MessageObject mB1 = buildMessageForTarget(tokenB, "b1");

        cache.queueIncomingMessage(mA1);
        cache.queueIncomingMessage(mB1);

        // Ensure routing respects token equality
        assertEquals("a1", cache.peekNextMessage(tokenA).getMessageId());
        assertEquals("b1", cache.peekNextMessage(tokenB).getMessageId());

        // Poll from A should not affect B
        cache.pollNextMessage(tokenA);
        assertNull(cache.peekNextMessage(tokenA));
        assertEquals("b1", cache.peekNextMessage(tokenB).getMessageId());
    }

    @Test
    void incomingQueue_nullTokenPeekPollReturnNull() {
        assertNull(cache.peekNextMessage(null));
        assertNull(cache.peekNextMessage(null));
    }

    @Test
    void outgoingQueue_basicFIFOAndNullSafety() {
        // Null message ignored safely
        cache.queueOutgoingMessage(null);
        assertNull(cache.peekOutgoingQueue());

        // Enqueue two
        MessageObject o1 = new MessageObject();
        o1.setMessageId("o1");
        MessageObject o2 = new MessageObject();
        o2.setMessageId("o2");
        cache.queueOutgoingMessage(o1);
        cache.queueOutgoingMessage(o2);

        // Peek is o1 and not removed
        MessageObject peek = cache.peekOutgoingQueue();
        assertNotNull(peek);
        assertEquals("o1", peek.getMessageId());

        // Poll returns o1 then o2 then null
        MessageObject p1 = cache.pollOutgoingMessage();
        assertNotNull(p1);
        assertEquals("o1", p1.getMessageId());

        MessageObject p2 = cache.pollOutgoingMessage();
        assertNotNull(p2);
        assertEquals("o2", p2.getMessageId());

        assertNull(cache.pollOutgoingMessage());
        assertNull(cache.peekOutgoingQueue());
    }
}
