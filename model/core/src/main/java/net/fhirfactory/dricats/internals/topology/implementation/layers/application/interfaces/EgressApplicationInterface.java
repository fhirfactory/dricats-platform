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
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.PublicationSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationInterface;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EgressApplicationInterface extends WUPInterfaceBase implements Serializable {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
     // Attributes
    //

    private PublicationSet publishedContent;
    private ElementReference representativePathwayElement;
    private List<ElementReference> consumerPathwayElements;

    //
     // Constructor(s)
    //

    public EgressApplicationInterface(){
        super();
        this.consumerPathwayElements = new ArrayList<>();
        setSpecialization(InterfaceComponentTypeEnum.GENERIC_SENDER_INTERFACE.getName());
    }

    public EgressApplicationInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum specialisation, Map<String, String> configurationParameters){
        super(parent, name, documentation, specialisation, configurationParameters);
        this.consumerPathwayElements = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public PublicationSet getPublishedContent() {
        return publishedContent;
    }

    public void setPublishedContent(PublicationSet publishedContent) {
        this.publishedContent = publishedContent;
    }

    public List<ElementReference> getConsumerPathwayElements() {
        return consumerPathwayElements;
    }

    public void setConsumerPathwayElements(List<ElementReference> consumerPathwayElements) {
        this.consumerPathwayElements = consumerPathwayElements;
    }

    public ElementReference getRepresentativePathwayElement() {
        return representativePathwayElement;
    }
    public void setRepresentativePathwayElement(ElementReference representativePathwayElement) {
        this.representativePathwayElement = representativePathwayElement;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("publishedContent", getPublishedContent())
                .append("representativePathwayElement", getRepresentativePathwayElement())
                .append("consumerPathwayElements", getConsumerPathwayElements())
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
