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
package net.fhirfactory.dricats.internals.model.software;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentConfigurationBase;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentId;
import net.fhirfactory.dricats.internals.model.software.datatypes.SoftwareComponentType;
import net.fhirfactory.dricats.internals.model.software.interfaces.SubsystemInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract public class SoftwareComponent implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900091L;
    private static final Logger LOG = LoggerFactory.getLogger(SoftwareComponent.class);

    //
    // Attributes
    //

    private SoftwareComponentId componentId;
    private SoftwareComponentId parentComponent;
    private List<SoftwareComponentId> containedComponents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime componentStartupDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime componentInstantiationDate;
    private SubsystemInterface subsystem;
    private SoftwareComponentConfigurationBase configuration;
    private String configurationName;

    //
    // Constructor(s)
    //

    public SoftwareComponent() {
        super();
        this.containedComponents = new ArrayList<>();
        this.componentInstantiationDate = LocalDateTime.now();
        this.componentId = null;
        this.configurationName = null;
    }

    public SoftwareComponent(SubsystemInterface subsystem, SoftwareComponentId componentId, SoftwareComponentConfigurationBase configuration, String configurationName, SoftwareComponentId parentComponent ) {
        super();
        this.subsystem = subsystem;
        this.configuration = configuration;
        this.configurationName = configurationName;
        this.parentComponent = parentComponent;
        this.componentInstantiationDate = LocalDateTime.now();
        this.componentId = componentId;
        this.componentStartupDate = LocalDateTime.now();
    }

    //
    // Bean Methods
    //

    @JsonIgnore
    public ProcessingPlant getSubsystem(){
        return(subsystem.getSubsystem());
    }

    public SoftwareComponentId getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(SoftwareComponentId parentComponent) {
        this.parentComponent = parentComponent;
    }

    public List<SoftwareComponentId> getContainedComponents() {
        return containedComponents;
    }

    public void setContainedComponents(List<SoftwareComponentId> containedComponents) {
        this.containedComponents = containedComponents;
    }

    public LocalDateTime getComponentStartupDate() {
        return componentStartupDate;
    }

    public void setComponentStartupDate(LocalDateTime componentStartupDate) {
        this.componentStartupDate = componentStartupDate;
    }

    public LocalDateTime getComponentInstantiationDate() {
        return componentInstantiationDate;
    }

    public void setComponentInstantiationDate(LocalDateTime componentInstantiationDate) {
        this.componentInstantiationDate = componentInstantiationDate;
    }

    public SoftwareComponentId getComponentId() {
        return componentId;
    }

    public void setComponentId(SoftwareComponentId componentId) {
        this.componentId = componentId;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

}
