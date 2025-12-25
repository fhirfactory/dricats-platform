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
package net.fhirfactory.dricats.internals.topology.implementation.common.valuesets;

public enum TopologyExtensionTypeValueSet {
    EXTENSION_NETWORK_SECURITY_GROUP("NetworkSecurityGroup","Network Security Group"),
    EXTENSION_NETWORK_SECURITY_ZONE("NetworkSecurityZone","Network Security Zone"),
    EXTENSION_SUBSYSTEM_CLUSTER_TYPE("SubsystemClusterType","Subsystem Cluster Type"),
    EXTENSION_ADAPTER_TYPE("AdapterType","Adapter Type"),
    EXTENSION_ENDPOINT_URI("EndpointURI","Endpoint URI"),
    EXTENSION_JGROUPS_CONFIGURATION_OBJECT("JGroupsConfigurationObject","JGroups Configuration Object"),
    EXTENSION_INTERFACE_CONNECTIVITY_ROLE("InterfaceConnectivityRole","Interface Connectivity Role"),
    EXTENSION_INTERFACE_CONTENT_ROLE("InterfaceContentRole","Interface Content Role");


    private final String extensionName;
    private final String extensionDescription;

    private TopologyExtensionTypeValueSet(String name, String description){
        this.extensionName = name;
        this.extensionDescription = description;
    }

    public String getExtensionDescription() {
        return extensionDescription;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public TopologyExtensionTypeValueSet getEnum(String name){
        for(TopologyExtensionTypeValueSet enumValue: TopologyExtensionTypeValueSet.values()){
            if(enumValue.getExtensionName().equals(name)){
                return enumValue;
            }
        }
        return null;
    }
}
