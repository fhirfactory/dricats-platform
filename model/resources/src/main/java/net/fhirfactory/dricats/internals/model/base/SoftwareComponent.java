/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.internals.model.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoftwareComponent extends DistributableObject{
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900091L;
    private static final Logger LOG = LoggerFactory.getLogger(SoftwareComponent.class);

    //
    // Attributes
    //

    private List<DistributableObjectReference> endpoints;
    private DistributableObjectReference parentComponent;
    private List<DistributableObjectReference> containedComponents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime componentStartupDate;

    //
    // Constructor(s)
    //

    public SoftwareComponent() {
        this.endpoints = new ArrayList<>();
        this.containedComponents = new ArrayList<>();
    }

    //
    // Bean Methods
    //

    public List<DistributableObjectReference> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<DistributableObjectReference> endpoints) {
        this.endpoints = endpoints;
    }

    public DistributableObjectReference getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(DistributableObjectReference parentComponent) {
        this.parentComponent = parentComponent;
    }

    public List<DistributableObjectReference> getContainedComponents() {
        return containedComponents;
    }

    public void setContainedComponents(List<DistributableObjectReference> containedComponents) {
        this.containedComponents = containedComponents;
    }

    public LocalDateTime getComponentStartupDate() {
        return componentStartupDate;
    }

    public void setComponentStartupDate(LocalDateTime componentStartupDate) {
        this.componentStartupDate = componentStartupDate;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SoftwareComponent{");
        sb.append("endpoints=").append(getEndpoints());
        sb.append(", parentComponent=").append(getParentComponent());
        sb.append(", containedComponents=").append(getContainedComponents());
        sb.append(", componentStartupDate=").append(getComponentStartupDate());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id=").append(getId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        SoftwareComponent that = (SoftwareComponent) o;
        return Objects.equals(getEndpoints(), that.getEndpoints()) && Objects.equals(getParentComponent(), that.getParentComponent()) && Objects.equals(getContainedComponents(), that.getContainedComponents()) && Objects.equals(getComponentStartupDate(), that.getComponentStartupDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getParentComponent(), getContainedComponents(), getComponentStartupDate());
    }
}
