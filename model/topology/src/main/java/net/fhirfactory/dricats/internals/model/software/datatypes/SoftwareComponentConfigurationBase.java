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
package net.fhirfactory.dricats.internals.model.software.datatypes;

import net.fhirfactory.dricats.internals.model.networking.valuesets.NetworkSecurityZoneEnum;

import java.io.Serial;
import java.io.Serializable;

public class SoftwareComponentConfigurationBase implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900091L;

    //
    // Attributes
    //

    private String subsystemDeploymentGroup;
    private String subsystemDeploymentSite;
    private SoftwareComponentType componentType;
    private NetworkSecurityZoneEnum componentNetworkSecurityZone;
    private String componentName;

    //
    // Getters and Setters
    //

    public String getSubsystemDeploymentGroup() {
        return subsystemDeploymentGroup;
    }

    public void setSubsystemDeploymentGroup(String subsystemDeploymentGroup) {
        this.subsystemDeploymentGroup = subsystemDeploymentGroup;
    }

    public String getSubsystemDeploymentSite() {
        return subsystemDeploymentSite;
    }

    public void setSubsystemDeploymentSite(String subsystemDeploymentSite) {
        this.subsystemDeploymentSite = subsystemDeploymentSite;
    }

    public SoftwareComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(SoftwareComponentType componentType) {
        this.componentType = componentType;
    }

    public NetworkSecurityZoneEnum getComponentNetworkSecurityZone() {
        return componentNetworkSecurityZone;
    }

    public void setComponentNetworkSecurityZone(NetworkSecurityZoneEnum componentNetworkSecurityZone) {
        this.componentNetworkSecurityZone = componentNetworkSecurityZone;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
