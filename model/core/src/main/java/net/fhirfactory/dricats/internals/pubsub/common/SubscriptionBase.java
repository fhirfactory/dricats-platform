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

import net.fhirfactory.dricats.internals.common.object.DistributableObject;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationFunction;
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
    private ElementReference subscriberInstance;

    //
    // Constructor(s)
    //

    public SubscriptionBase() {
        super();
        this.subscriberInstance = null;
    }

    public SubscriptionBase(ElementReference subscriber, ApplicationFunction subscriberFunction, String subscriptionEventType) {
        super();
        this.subscriberInstance = subscriber;
        FullyDistinguishedName qualifiedName = subscriber.getLocalObjectId().getFullyDistinguishedName();
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(subscriptionEventType, UUID.randomUUID().toString());
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        ObjectId subscriptionId = new ObjectId(qualifiedName);
        setObjectId(subscriptionId);
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

    public ElementReference getSubscriberInstance() {
        return subscriberInstance;
    }

    public void setSubscriberInstance(ElementReference subscriberInstance) {
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
