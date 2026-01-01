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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.adapters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.common.JSONMapperUtility;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.configuration.segments.ports.internal.JGroupsInterfaceConfigurationObject;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPAdapterBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class JGroupsAdapter extends WUPAdapterBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900051L;

    //
    // Constants
    //

    //
    // Attributes
    //

    @JsonIgnore
    private final JSONMapperUtility mapperUtility;

    //
    // Constructor(s)
    //

    public JGroupsAdapter(){
        super();
        mapperUtility = new JSONMapperUtility();
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.JGROUPS_MESSAGING_ADAPTER.name());
    }

    public JGroupsAdapter(String name, String documentation, String interfaceSpecialisation){
        super(name,documentation);
        mapperUtility = new JSONMapperUtility();
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.JGROUPS_MESSAGING_ADAPTER.name());
    }

    public JGroupsAdapter(ElementReference parentId, String name, String documentation, JGroupsInterfaceConfigurationObject configurationObject){
        super(name,documentation);
        mapperUtility = new JSONMapperUtility();
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.JGROUPS_MESSAGING_ADAPTER.name());
        setParent(parentId);
        setConfigurationObject(configurationObject);
    }

    //
    // Bean Methods
    //

    @JsonIgnore
    protected JSONMapperUtility getMapperUtility() {
        return mapperUtility;
    }

    @JsonIgnore
    public JGroupsInterfaceConfigurationObject getConfigurationObject() {
        String configurationObjectString = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_JGROUPS_CONFIGURATION_OBJECT.getExtensionName());
        if(configurationObjectString != null){
            try {
                JGroupsInterfaceConfigurationObject configurationObject = mapperUtility.getJSONMapper().readValue(configurationObjectString, JGroupsInterfaceConfigurationObject.class);
                return configurationObject;
            } catch (Exception e) {
                getLogger().error("JGroupsInterface.getConfigurationObject(): Exception caught: {}", e.getMessage());
                return null;
            }
        }
        return null;
    }

    @JsonIgnore
    public void setConfigurationObject(JGroupsInterfaceConfigurationObject configurationObject) {
        if(configurationObject != null){
            try{
                String configurationObjectString = mapperUtility.getJSONMapper().writeValueAsString(configurationObject);
                setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_JGROUPS_CONFIGURATION_OBJECT.getExtensionName(), configurationObjectString);
            } catch (Exception e) {
                getLogger().error("JGroupsInterface.setConfigurationObject(): Exception caught: {}", e.getMessage());
            }
        }
    }

    //
    // Utility Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mapperUtility", getMapperUtility())
                .append("parent", getParent())
                .append("metricsData", getMetricsData())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("identifiers", getIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getObjectId())
                .toString();
    }
}
