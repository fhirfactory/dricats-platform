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
package net.fhirfactory.dricats.internals.pubsub;

import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class TopicSubscription extends QualifiedNameMask implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TopicSubscription.class);

    //
    // Attributes
    //

    //
    // Constructor(s)
    //
    public TopicSubscription() {
        super();
    }

    public TopicSubscription(QualifiedName subscriptionMask) {
        super(subscriptionMask);
    }

    public TopicSubscription(QualifiedName subscriptionMask, Boolean includeSubtopics) {
        super(subscriptionMask, includeSubtopics);
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger(){
        return LOG;
    }

    //
    // Business Methods
    //

    public boolean filterTopic(Topic topic){
        boolean outcome = filter(topic.getTopicName());
        return(outcome);
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
