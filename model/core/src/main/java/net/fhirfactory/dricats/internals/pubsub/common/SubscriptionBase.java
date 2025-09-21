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
package net.fhirfactory.dricats.internals.pubsub.common;

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationFunction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class SubscriptionBase extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private ApplicationFunction subscriberFunction;
    private DistributableObjectId subscriberInstance;

    //
    // Constructor(s)
    //

    public SubscriptionBase() {
        super();
        this.subscriberInstance = null;
    }

    public SubscriptionBase(DistributableObjectId subscriber, ApplicationFunction subscriberFunction, String subscriptionEventType) {
        super();
        this.subscriberInstance = subscriber;
        QualifiedName qualifiedName = subscriber.getQualifiedName();
        UnqualifiedName unqualifiedName = new UnqualifiedName(subscriptionEventType, UUID.randomUUID().toString());
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        DistributableObjectId subscriptionId = new DistributableObjectId(qualifiedName);
        setObjectID(subscriptionId);
        setId(subscriptionId.getQualifiedName().getCommonName());
        this.subscriberFunction = subscriberFunction;
    }

    //
    // Accessor(s)
    //

    public ApplicationFunction getSubscriberFunction() {
        return subscriberFunction;
    }

    public void setSubscriberFunction(ApplicationFunction subscriberFunction) {
        this.subscriberFunction = subscriberFunction;
    }

    public DistributableObjectId getSubscriberInstance() {
        return subscriberInstance;
    }

    public void setSubscriberInstance(DistributableObjectId subscriberInstance) {
        this.subscriberInstance = subscriberInstance;
    }

    //
    // Standard Methods
    //

    @Override
    public String
    toString() {
        return new ToStringBuilder(this)
                .append("subscriberFunction", subscriberFunction)
                .append("subscriberInstance", subscriberInstance)
                .appendSuper(super.toString())
                .toString();
    }
}
