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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces;

import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.SubscriptionSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class IngresApplicationInterface extends WUPInterfaceBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attribute(s)
    //

    private SubscriptionSet subscriptions;
    private ObjectId representativePathwayElement;
    private List<ObjectId> producerPathwayElements;
    //
    // Constructor(s)
    //
    public IngresApplicationInterface() {
        super();
        this.producerPathwayElements = new java.util.ArrayList<>();
    }

    //
    // Getters and Setters
    //
    public SubscriptionSet getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(SubscriptionSet subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ObjectId getRepresentativePathwayElement() {
        return representativePathwayElement;
    }

    public void setRepresentativePathwayElement(ObjectId representativePathwayElement) {
        this.representativePathwayElement = representativePathwayElement;
    }

    public List<ObjectId> getProducerPathwayElements() {
        return producerPathwayElements;
    }
    public void setProducerPathwayElements(List<ObjectId> producerPathwayElements) {
        this.producerPathwayElements = producerPathwayElements;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("subscriptions", getSubscriptions())
                .append("representativePathwayElement", getRepresentativePathwayElement())
                .append("producerPathwayElements", getProducerPathwayElements())
                .append("adapters", getAdapters())
                .append("interfaceConnectivityRole", getInterfaceConnectivityRole())
                .append("metricsData", getMetricsData())
                .append("interfaceContentRole", getInterfaceContentRole())
                .append("owner", getOwner())
                .append("services", getServices())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getLocalId())
                .toString();
    }
}
