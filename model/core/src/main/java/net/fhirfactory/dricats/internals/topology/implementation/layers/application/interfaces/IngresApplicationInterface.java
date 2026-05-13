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

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.SubscriptionSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ElementReference representativePathwayElement;
    private List<ElementReference> producerPathwayElements;
    //
    // Constructor(s)
    //
    public IngresApplicationInterface() {
        super();
        this.producerPathwayElements = new java.util.ArrayList<>();
        setSpecialization(InterfaceComponentTypeEnum.GENERIC_RECEIVER_INTERFACE.getName());
    }

    public IngresApplicationInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum specialisation, Map<String, String> configurationParameters){
        super(parent, name, documentation, specialisation, configurationParameters);
        this.producerPathwayElements = new ArrayList<>();
    }

    //
    // Getters and Setters
    //
    public SubscriptionSet getSubscriptions() {
        if(this.subscriptions == null){
            this.subscriptions = new SubscriptionSet();
        }
        return subscriptions;
    }

    public void setSubscriptions(SubscriptionSet subscriptions) {
        this.subscriptions = subscriptions;
    }

    public ElementReference getRepresentativePathwayElement() {
        return representativePathwayElement;
    }

    public void setRepresentativePathwayElement(ElementReference representativePathwayElement) {
        this.representativePathwayElement = representativePathwayElement;
    }

    public List<ElementReference> getProducerPathwayElements() {
        if(this.producerPathwayElements == null){
            this.producerPathwayElements = new ArrayList<>();
        }
        return producerPathwayElements;
    }
    public void setProducerPathwayElements(List<ElementReference> producerPathwayElements) {
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
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("elementType", getElementType())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("owner", getOwner())
                .append("services", getServices())
                .append("supportedDataObjects", getSupportedDataObjects())
                .append("componentStatus", getComponentStatus())
                .append("adapters", getAdapters())
                .append("metricsData", getMetricsData())
                .toString();
    }
}
