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
package net.fhirfactory.dricats.internals.oam.topology;

import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentStatusSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.InterfaceImplementationBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EgressInterfaceComponentSummary extends InterfaceComponentSummary implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(EgressInterfaceComponentSummary.class);

    //
    // Attributes
    //

    private List<ContentFilter> contentFilters;

    //
    // Constructor(s)
    //

    public EgressInterfaceComponentSummary(){
        super();
        contentFilters = new ArrayList<>();

    }

    public EgressInterfaceComponentSummary(ApplicationComponent applicationComponent ){
        super();
        setMetadata(applicationComponent.getMetadata());
        setObjectID(applicationComponent.getObjectID());
        setDocumentation(applicationComponent.getDocumentation());
        setName(applicationComponent.getName());
        setObjectID(applicationComponent.getObjectID());
        setSpecialization(applicationComponent.getSpecialization());
        setElementType(applicationComponent.getElementType());
        setComponentStatus(new ApplicationComponentStatusSummary());
        getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        getComponentStatus().setComponentStatus(applicationComponent.getMetricsData().getComponentStatus());
        getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        getComponentStatus().setStartupInstant(applicationComponent.getMetadata().getCreationDate());
        contentFilters = new ArrayList<>();
    }

    public EgressInterfaceComponentSummary(InterfaceImplementationBase interfaceImplementation ){
        super();
        setMetadata(interfaceImplementation.getMetadata());
        setObjectID(interfaceImplementation.getObjectID());
        setDocumentation(interfaceImplementation.getDocumentation());
        setName(interfaceImplementation.getName());
        setObjectID(interfaceImplementation.getObjectID());
        setSpecialization(interfaceImplementation.getSpecialization());
        setElementType(interfaceImplementation.getElementType());
        setComponentStatus(new ApplicationComponentStatusSummary());
        getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        getComponentStatus().setComponentStatus(interfaceImplementation.getMetricsData().getComponentStatus());
        getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        getComponentStatus().setStartupInstant(interfaceImplementation.getMetadata().getCreationDate());
        contentFilters = new ArrayList<>();
    }

    //
     // Bean Methods
    //
    public List<ContentFilter> getContentFilters() {
        return contentFilters;
    }

    public void setContentFilters(List<ContentFilter> contentFilters) {
        this.contentFilters = contentFilters;
    }

    //
    // Business Methods
    //


    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("subscriptionFilters", getContentFilters())
                .append("parent", getParent())
                .append("componentStatus", getComponentStatus())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getProperties())
                .append("objectID", getObjectID())
                .append("metadata", getMetadata())
                .append("id", getId())
                .toString();
    }
}
