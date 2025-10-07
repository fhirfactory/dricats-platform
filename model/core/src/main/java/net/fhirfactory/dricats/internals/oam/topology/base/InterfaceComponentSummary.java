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
package net.fhirfactory.dricats.internals.oam.topology.base;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.InterfaceImplementationBase;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

abstract public class InterfaceComponentSummary extends SimpleElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900007L;

    //
    // Attributes
    //
    private DistributableObjectId parent;
    private ApplicationComponentStatusSummary componentStatus;

    //
    // Constructor(s)
    //
    public InterfaceComponentSummary(){
        super();
        componentStatus = new ApplicationComponentStatusSummary();
    }

    public InterfaceComponentSummary(ApplicationComponent applicationComponent ){
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
    }

    public InterfaceComponentSummary(InterfaceImplementationBase interfaceImplementation ){
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
    }

    //
    // Getters and Setters
    //

    public DistributableObjectId getParent() {
        return parent;
    }

    public void setParent(DistributableObjectId parent) {
        this.parent = parent;
    }

    public ApplicationComponentStatusSummary getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(ApplicationComponentStatusSummary componentStatus) {
        this.componentStatus = componentStatus;
    }

    //
     // Resolve a Key
    //

    public String resolveKey() {
        String key = null;
        try {
            DistributableObjectId objectId = this.getObjectID();
            if (objectId != null && objectId.getQualifiedName() != null && objectId.getQualifiedName().getCommonName().getValue() != null && !objectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
                key = objectId.getQualifiedName().getCommonName().getValue();
            }
        } catch (Exception e) {
            // ignore
        }
        return key;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "InterfaceComponentSummary{" +
                "parent=" + getParent() +
                ", elementType=" + getElementType() +
                ", name='" + getName() + '\'' +
                ", documentation='" + getDocumentation() + '\'' +
                ", specialization='" + getSpecialization() + '\'' +
                ", properties=" + getProperties() +
                ", objectID=" + getObjectID() +
                ", metadata=" + getMetadata() +
                ", id=" + getId() +
                ", componentStatus=" + getComponentStatus() +
                '}';
    }
}
