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

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.reference.common.SimpleElementBase;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ApplicationComponentSummary extends SimpleElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900007L;

    //
    // Attributes
    //
    private DistributableObjectId parent;
    private List<DistributableObjectId> subComponents;
    private ApplicationComponentStatusSummary componentStatus;

    //
    // Constructor(s)
    //
    public ApplicationComponentSummary(){
        super();
    }

    public ApplicationComponentSummary( ApplicationComponent applicationComponent ){
        super();
        setMetadata(applicationComponent.getMetadata());
        setObjectID(applicationComponent.getObjectID());
        setDocumentation(applicationComponent.getDocumentation());
        setName(applicationComponent.getName());
        setObjectID(applicationComponent.getObjectID());
        setSpecialization(applicationComponent.getSpecialization());
        setElementType(applicationComponent.getElementType());
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

    public List<DistributableObjectId> getSubComponents() {
        return subComponents;
    }

    public void setSubComponents(List<DistributableObjectId> subComponents) {
        this.subComponents = subComponents;
    }

    public ApplicationComponentStatusSummary getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(ApplicationComponentStatusSummary componentStatus) {
        this.componentStatus = componentStatus;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "ApplicationComponentSummary{" +
                "parent=" + getParent() +
                ", subComponents=" + getSubComponents() +
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
