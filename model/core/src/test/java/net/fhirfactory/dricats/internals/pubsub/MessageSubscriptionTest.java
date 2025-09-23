package net.fhirfactory.dricats.internals.pubsub;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import net.fhirfactory.dricats.internals.events.messages.MessagePayload;
import net.fhirfactory.dricats.internals.pubsub.common.ApplicationComponentIdMask;
import net.fhirfactory.dricats.internals.pubsub.common.EventTemporalWindow;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MessageSubscriptionTest {

    private QualifiedName qn(String qualifier, String value){
        QualifiedName q = new QualifiedName();
        q.appendUnqualifiedName(new UnqualifiedName(qualifier, value));
        return q;
    }

    private DistributableObjectId did(String qualifier, String value){
        return new DistributableObjectId(qn(qualifier, value));
    }

    private Topic topic(String qualifier, String value){
        return new Topic(qn(qualifier, value));
    }

    private MessageObject buildMessage(DistributableObjectId source, DistributableObjectId target, Topic topic){
        MessagePayload payload = new MessagePayload();
        payload.setDataTopic(topic);
        return new MessageObject(source, target, LocalDateTime.now(), payload);
    }

    private MessageSubscriptionMask maskFor(QualifiedName sourceMaskQN, QualifiedName targetMaskQN, QualifiedName topicMaskQN){
        ApplicationComponentIdMask srcMask = new ApplicationComponentIdMask();
        srcMask.setComponentIdMask(new QualifiedNameMask(sourceMaskQN));
        ApplicationComponentIdMask tgtMask = new ApplicationComponentIdMask();
        tgtMask.setComponentIdMask(new QualifiedNameMask(targetMaskQN));
        TopicSubscription topicSub = new TopicSubscription(topicMaskQN, true);
        EventTemporalWindow window = new EventTemporalWindow(); // MIN..MAX
        return new MessageSubscriptionMask(srcMask, tgtMask, List.of(topicSub), "*", "*", window);
    }

    @Test
    public void testFilterMessage_allMatch_returnsTrue(){
        MessageSubscription sub = new MessageSubscription();
        MessageSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        MessageObject msg = buildMessage(did("comp","A"), did("comp","B"), topic("topic","alpha"));
        assertTrue(sub.filterMessage(msg), "Expected message to match all criteria");
    }

    @Test
    public void testFilterMessage_sourceMismatch_returnsFalse(){
        MessageSubscription sub = new MessageSubscription();
        MessageSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        MessageObject msg = buildMessage(did("comp","X"), did("comp","B"), topic("topic","alpha"));
        assertFalse(sub.filterMessage(msg), "Expected source mismatch to fail");
    }

    @Test
    public void testFilterMessage_topicMismatch_returnsFalse(){
        MessageSubscription sub = new MessageSubscription();
        MessageSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        MessageObject msg = buildMessage(did("comp","A"), did("comp","B"), topic("topic","beta"));
        assertFalse(sub.filterMessage(msg), "Expected topic mismatch to fail");
    }

    @Test
    public void testFilterMessage_temporalWindowOutside_returnsFalse(){
        MessageSubscription sub = new MessageSubscription();
        // Narrow window in the past
        EventTemporalWindow narrow = new EventTemporalWindow();
        narrow.setEffectiveStartDate(LocalDateTime.now().minusHours(2));
        narrow.setEffectiveEndDate(LocalDateTime.now().minusHours(1));

        ApplicationComponentIdMask srcMask = new ApplicationComponentIdMask();
        srcMask.setComponentIdMask(new QualifiedNameMask(qn("comp","A")));
        ApplicationComponentIdMask tgtMask = new ApplicationComponentIdMask();
        tgtMask.setComponentIdMask(new QualifiedNameMask(qn("comp","B")));
        TopicSubscription topicSub = new TopicSubscription(qn("topic","alpha"), true);
        MessageSubscriptionMask mask = new MessageSubscriptionMask(srcMask, tgtMask, List.of(topicSub), "*", "*", narrow);
        sub.setMessageSubscriptionMask(mask);

        MessageObject msg = buildMessage(did("comp","A"), did("comp","B"), topic("topic","alpha"));
        // message sendDate is now, which is outside the narrow past window
        assertFalse(sub.filterMessage(msg), "Expected temporal window outside to fail");
    }

    @Test
    public void testFilterMessage_payloadNull_returnsFalse(){
        MessageSubscription sub = new MessageSubscription();
        MessageSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        MessageObject msg = new MessageObject(did("comp","A"), did("comp","B"), LocalDateTime.now(), null);
        assertFalse(sub.filterMessage(msg), "Expected null payload to fail");
    }
}
