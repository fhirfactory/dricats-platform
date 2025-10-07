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

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
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
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponent.class);

    // ArchiMate attributes specific to Application Component (by reference IDs)
    // - interfaces: references to ApplicationInterface objects
    // - parent: reference to parent ApplicationComponent
    // - subComponents: references to child ApplicationComponents

    private List<DistributableObjectId> interfaces;
    private DistributableObjectId parent;
    private List<DistributableObjectId> subComponents;
    private ApplicationComponentMetricsData metricsData;
    private List<DistributableObjectId> supportedApplicationFunctions;
    private List<DistributableObjectId> supportedApplicationServices;

    //
    // Constructor(s)
    //
    public ApplicationComponent() {
        super();
        this.interfaces = new ArrayList<>();
        this.subComponents = new ArrayList<>();
        this.supportedApplicationFunctions = new ArrayList<>();
        this.supportedApplicationServices = new ArrayList<>();
        metricsData = new ApplicationComponentMetricsData();
        this.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        getLogger().trace("ApplicationComponent(): constructed");
    }

    //
    // Bean Methods
    //

    public List<DistributableObjectId> getSupportedApplicationFunctions() {
        return supportedApplicationFunctions;
    }

    public void setSupportedApplicationFunctions(List<DistributableObjectId> supportedApplicationFunctions) {
        this.supportedApplicationFunctions = supportedApplicationFunctions;
    }

    public List<DistributableObjectId> getSupportedApplicationServices() {
        return supportedApplicationServices;
    }

    public void setSupportedApplicationServices(List<DistributableObjectId> supportedApplicationServices) {
        this.supportedApplicationServices = supportedApplicationServices;
    }

    protected Logger getLogger(){
        return LOG;
    }

    public List<DistributableObjectId> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<DistributableObjectId> interfaces) {
        this.interfaces = interfaces == null ? new ArrayList<>() : interfaces;
    }

    public void addInterface(DistributableObjectId appInterfaceId) {
        if (appInterfaceId == null) { return; }
        if (this.interfaces == null) { this.interfaces = new ArrayList<>(); }
        this.interfaces.add(appInterfaceId);
    }

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
        this.subComponents = subComponents == null ? new ArrayList<>() : subComponents;
    }

    public void addSubComponent(DistributableObjectId childId) {
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
                .appendSuper(super.toString())
                .toString();
    }
}
