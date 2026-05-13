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

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.common.object.ManagedObject;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationFunction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class SubscriptionBase extends ManagedObject implements Serializable {
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
        setSubscriberInstance(subscriber);
        DistinguishedName subscriptionName = subscriber.getElementIdentifier().getIdentifierValue();
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(subscriptionEventType, UUID.randomUUID().toString());
        subscriptionName.appendUnqualifiedName(unqualifiedName);
        ElementIdentifier subscriptionIdentifier = new ElementIdentifier(subscriptionName);
        setIdentifier(subscriptionIdentifier);
        setShortName(subscriptionEventType + "->" + unqualifiedName.getUnqualifiedValue());
        setSubscriberFunction(subscriberFunction);
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
