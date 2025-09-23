package net.fhirfactory.dricats.internals.pubsub;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.events.notifications.NotificationObject;
import net.fhirfactory.dricats.internals.events.notifications.NotificationPayload;
import net.fhirfactory.dricats.internals.pubsub.common.ApplicationComponentIdMask;
import net.fhirfactory.dricats.internals.pubsub.common.EventTemporalWindow;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationSubscriptionTest {

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

    private NotificationObject buildNotification(DistributableObjectId source, DistributableObjectId target, Topic topic){
        NotificationPayload payload = new NotificationPayload();
        payload.setDataTopic(topic);
        return new NotificationObject(source, target, LocalDateTime.now(), payload);
    }

    private NotificationSubscriptionMask maskFor(QualifiedName sourceMaskQN, QualifiedName targetMaskQN, QualifiedName topicMaskQN){
        ApplicationComponentIdMask srcMask = new ApplicationComponentIdMask();
        srcMask.setComponentIdMask(new QualifiedNameMask(sourceMaskQN));
        ApplicationComponentIdMask tgtMask = new ApplicationComponentIdMask();
        tgtMask.setComponentIdMask(new QualifiedNameMask(targetMaskQN));
        TopicSubscription topicSub = new TopicSubscription(topicMaskQN, true);
        EventTemporalWindow window = new EventTemporalWindow(); // MIN..MAX
        return new NotificationSubscriptionMask(srcMask, tgtMask, List.of(topicSub), "*", "*", window);
    }

    @Test
    public void testFilterNotification_allMatch_returnsTrue(){
        NotificationSubscription sub = new NotificationSubscription();
        NotificationSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        NotificationObject n = buildNotification(did("comp","A"), did("comp","B"), topic("topic","alpha"));
        assertTrue(sub.filterNotification(n), "Expected notification to match all criteria");
    }

    @Test
    public void testFilterNotification_sourceMismatch_returnsFalse(){
        NotificationSubscription sub = new NotificationSubscription();
        NotificationSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        NotificationObject n = buildNotification(did("comp","X"), did("comp","B"), topic("topic","alpha"));
        assertFalse(sub.filterNotification(n), "Expected source mismatch to fail");
    }

    @Test
    public void testFilterNotification_topicMismatch_returnsFalse(){
        NotificationSubscription sub = new NotificationSubscription();
        NotificationSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        NotificationObject n = buildNotification(did("comp","A"), did("comp","B"), topic("topic","beta"));
        assertFalse(sub.filterNotification(n), "Expected topic mismatch to fail");
    }

    @Test
    public void testFilterNotification_temporalWindowOutside_returnsFalse(){
        NotificationSubscription sub = new NotificationSubscription();
        // Narrow window in the past
        EventTemporalWindow narrow = new EventTemporalWindow();
        narrow.setEffectiveStartDate(LocalDateTime.now().minusHours(2));
        narrow.setEffectiveEndDate(LocalDateTime.now().minusHours(1));

        ApplicationComponentIdMask srcMask = new ApplicationComponentIdMask();
        srcMask.setComponentIdMask(new QualifiedNameMask(qn("comp","A")));
        ApplicationComponentIdMask tgtMask = new ApplicationComponentIdMask();
        tgtMask.setComponentIdMask(new QualifiedNameMask(qn("comp","B")));
        TopicSubscription topicSub = new TopicSubscription(qn("topic","alpha"), true);
        NotificationSubscriptionMask mask = new NotificationSubscriptionMask(srcMask, tgtMask, List.of(topicSub), "*", "*", narrow);
        sub.setMessageSubscriptionMask(mask);

        NotificationObject n = buildNotification(did("comp","A"), did("comp","B"), topic("topic","alpha"));
        // notification sendDate is now, which is outside the narrow past window
        assertFalse(sub.filterNotification(n), "Expected temporal window outside to fail");
    }

    @Test
    public void testFilterNotification_payloadNull_returnsFalse(){
        NotificationSubscription sub = new NotificationSubscription();
        NotificationSubscriptionMask mask = maskFor(qn("comp","A"), qn("comp","B"), qn("topic","alpha"));
        sub.setMessageSubscriptionMask(mask);

        NotificationObject n = new NotificationObject(did("comp","A"), did("comp","B"), LocalDateTime.now(), null);
        assertFalse(sub.filterNotification(n), "Expected null payload to fail");
    }
}
