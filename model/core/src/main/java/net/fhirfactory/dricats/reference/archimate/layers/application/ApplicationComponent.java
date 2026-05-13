/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatus;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an Application Component in the Archimate application layer.
 * An Application Component represents an encapsulation of application functionality
 * aligned to the implementation structure.
 */
public class ApplicationComponent extends ElementBase {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678910101L;

    // ArchiMate attributes specific to Application Component (by reference IDs)
    // - interfaces: references to ApplicationInterface objects
    // - parent: reference to parent ApplicationComponent
    // - subComponents: references to child ApplicationComponents

    private List<ElementReference> interfaces;
    private ElementReference parent;
    private List<ElementReference> subComponents;
    private ApplicationComponentMetricsData metricsData;
    private ApplicationComponentStatus componentStatus;
    private List<ElementReference> supportedApplicationFunctions;
    private List<ElementReference> supportedApplicationServices;

    //
    // Constructor(s)
    //
    public ApplicationComponent() {
        super();
        this.interfaces = new ArrayList<>();
        this.subComponents = new ArrayList<>();
        this.supportedApplicationFunctions = new ArrayList<>();
        this.supportedApplicationServices = new ArrayList<>();
        this.metricsData = new ApplicationComponentMetricsData();
        this.componentStatus = new ApplicationComponentStatus();
        this.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
    }


    public ApplicationComponent(ElementReference parent, String name, String documentation, String specialization, Map<String, String> extensions) {
        super( parent, name, documentation, specialization, extensions, ElementTypeEnum.APPLICATION_COMPONENT);
        this.interfaces = new ArrayList<>();
        this.subComponents = new ArrayList<>();
        this.supportedApplicationFunctions = new ArrayList<>();
        this.supportedApplicationServices = new ArrayList<>();
        setParent(parent);
        this.metricsData = new ApplicationComponentMetricsData();
        this.componentStatus = new ApplicationComponentStatus();
    }

    //
    // Bean Methods
    //

    public ApplicationComponentStatus getComponentStatus() {
        return componentStatus;
    }

    public void setComponentStatus(ApplicationComponentStatus componentStatus) {
        this.componentStatus = componentStatus;
    }

    public List<ElementReference> getSupportedApplicationFunctions() {
        if( this.supportedApplicationFunctions == null){
            this.supportedApplicationFunctions = new ArrayList<>();
        }
        return supportedApplicationFunctions;
    }

    public void setSupportedApplicationFunctions(List<ElementReference> supportedApplicationFunctions) {
        if(this.supportedApplicationFunctions == null){
            this.supportedApplicationFunctions = new ArrayList<>();
        } else {
            this.supportedApplicationFunctions.clear();
        }
        if(supportedApplicationFunctions != null){
            this.supportedApplicationFunctions.addAll(supportedApplicationFunctions);
        }
    }

    public List<ElementReference> getSupportedApplicationServices() {
        if( this.supportedApplicationServices == null){
            this.supportedApplicationServices = new ArrayList<>();
        }
        return supportedApplicationServices;
    }

    public void setSupportedApplicationServices(List<ElementReference> supportedApplicationServices) {
        if(this.supportedApplicationServices == null){
            this.supportedApplicationServices = new ArrayList<>();
        } else {
            this.supportedApplicationServices.clear();
        }
        if(supportedApplicationServices != null){
            this.supportedApplicationServices.addAll(supportedApplicationServices);
        }
    }

    public List<ElementReference> getInterfaces() {
        if( this.interfaces == null){
            interfaces = new ArrayList<>();
        }
        return interfaces;
    }

    public void setInterfaces(List<ElementReference> interfaces) {
        if(this.interfaces == null){
            this.interfaces = new ArrayList<>();
        } else {
            this.interfaces.clear();
        }
        if(interfaces != null){
            this.interfaces.addAll(interfaces);
        }
    }

    public void addInterface(ElementReference appInterfaceId) {
        if (appInterfaceId == null) { return; }
        if (this.interfaces == null) { this.interfaces = new ArrayList<>(); }
        this.interfaces.add(appInterfaceId);
    }

    public ElementReference getParent() {
        return parent;
    }

    public void setParent(ElementReference parent) {
        this.parent = parent;
    }

    public List<ElementReference> getSubComponents() {
        if( subComponents == null){
            subComponents = new ArrayList<>();
        }
        return subComponents;
    }

    public void setSubComponents(List<ElementReference> subComponents) {
        if( this.subComponents == null){
            this.subComponents = new ArrayList<>();
        } else {
            this.subComponents.clear();
        }
        if(subComponents != null){
            this.subComponents.addAll(subComponents);
        }
    }

    public void addSubComponent(ElementReference childId) {
        if (childId == null) { return; }
        if (this.subComponents == null) { this.subComponents = new ArrayList<>(); }
        this.subComponents.add(childId);
    }

    public ApplicationComponentMetricsData getMetricsData() {
        return metricsData;
    }

    public void setMetricsData(ApplicationComponentMetricsData metricsData) {
        this.metricsData = metricsData;
    }

    //
    // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationComponent that = (ApplicationComponent) o;
        return Objects.equals(interfaces, that.interfaces) &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(subComponents, that.subComponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), interfaces, parent, subComponents);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interfaces", interfaces)
                .append("parent", parent)
                .append("subComponents", subComponents)
                .append("metricsData", metricsData)
                .append("supportedApplicationFunctions", supportedApplicationFunctions)
                .append("supportedApplicationServices", supportedApplicationServices)
                .append("componentStatus", componentStatus)
                .appendSuper(super.toString())
                .toString();
    }
}
