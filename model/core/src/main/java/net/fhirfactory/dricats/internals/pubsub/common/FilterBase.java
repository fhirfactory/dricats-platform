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

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.object.ManagedObject;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationFunction;

import java.io.Serial;
import java.io.Serializable;

public class FilterBase extends ManagedObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private ApplicationFunction publisherFunction;
    private ElementReference publisherInstance;

    //
    // Constructor(s)
    //

    public FilterBase() {
        super();
    }

    public FilterBase(ElementReference publisher, ApplicationFunction publisherFunction) {
        super();
        this.publisherInstance = publisher;
        this.publisherFunction = publisherFunction;
    }

    //
    // Bean Methods
    //

    public ApplicationFunction getPublisherFunction() {
        return publisherFunction;
    }
    public void setPublisherFunction(ApplicationFunction publisherFunction) {
        this.publisherFunction = publisherFunction;
    }
    public ElementReference getPublisherInstance() {
        return publisherInstance;
    }
    public void setPublisherInstance(ElementReference publisherInstance) {
        this.publisherInstance = publisherInstance;
    }
}
