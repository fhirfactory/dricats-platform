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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationInterfaceMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentStatus;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationInterface;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WUPInterfaceBase extends ApplicationInterface implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(WUPInterfaceBase.class);

    //
     // Constants
    //
    public final static String PROPERTY_URL = "PROPERTY_URL";


    //
    // Member Variables
    //

    private ApplicationInterfaceMetricsData metricsData;
    private List<ElementReference> adapters;
    private ApplicationComponentStatus componentStatus;


    //
    // Constructor(s)
    //

    public WUPInterfaceBase() {
        super();
        setMetricsData(new ApplicationInterfaceMetricsData());
        setInterfaceContentRole( InterfaceContentRoleEnum.SUBSCRIBER);
        this.adapters = new ArrayList<>();
        this.componentStatus = new ApplicationComponentStatus();
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE.getType());
        getLogger().trace("ApplicationInterface(): constructed");
    }

    public WUPInterfaceBase(String name, String documentation, ApplicationComponentSpecialisationEnum interfaceSpecialisation) {
        super(name, documentation, interfaceSpecialisation.getCode());
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.componentStatus = new ApplicationComponentStatus();
        setInterfaceContentRole( InterfaceContentRoleEnum.SUBSCRIBER);
        this.adapters = new ArrayList<>();
        getLogger().trace("ApplicationInterface(name, documentation, interfaceSpecialisation): constructed");
    }

    public WUPInterfaceBase(ElementReference parent, String name, String documentation, ApplicationComponentSpecialisationEnum interfaceSpecialisation) {
        super(parent, name, documentation, interfaceSpecialisation.getCode());
        setMetricsData(new ApplicationInterfaceMetricsData());
        setInterfaceContentRole( InterfaceContentRoleEnum.SUBSCRIBER);
        this.adapters = new ArrayList<>();
        this.componentStatus = new ApplicationComponentStatus();
        getLogger().trace("ApplicationInterface(parent, name, documentation, interfaceSpecialisation): constructed");
    }

    public WUPInterfaceBase(ElementReference parent, String name, String documentation, ApplicationComponentSpecialisationEnum interfaceSpecialisation, URI uri) {
        super(parent, name, documentation, interfaceSpecialisation.getCode(), uri);
        // Ensure the provided URI is actually stored on this interface
        if (uri != null) {
            setURI(uri);
        }
        setInterfaceContentRole( InterfaceContentRoleEnum.SUBSCRIBER);
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.componentStatus = new ApplicationComponentStatus();
        this.adapters = new ArrayList<>();
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    public WUPInterfaceBase(
            ElementReference parent,
            String name,
            String documentation,
            URI endpointURI,
            ApplicationComponentSpecialisationEnum interfaceSpecialisation ) {
        super(parent, name, documentation,interfaceSpecialisation.getCode() );
        setURI(endpointURI);
        this.componentStatus = new ApplicationComponentStatus();
        setInterfaceContentRole( InterfaceContentRoleEnum.SUBSCRIBER);
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.adapters = new ArrayList<>();
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
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

    protected Logger getLogger(){ return LOG; }

    public List<ElementReference> getAdapters() {
        return adapters;
    }

    public void setAdapters(List<ElementReference> adapters) {
        if( getAdapters() == null){
            this.adapters = new ArrayList<>();
        } else {
            this.adapters.clear();
        }
        getAdapters().addAll(adapters);
    }

    @JsonIgnore
    public InterfaceConnectivityRoleEnum getInterfaceConnectivityRole() {
        String connectivityRole = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_INTERFACE_CONNECTIVITY_ROLE.getExtensionName());
        if(connectivityRole != null){
            InterfaceConnectivityRoleEnum connectivityRoleEnum = InterfaceConnectivityRoleEnum.getRoleByName(connectivityRole);
            return (connectivityRoleEnum);
        }
        return(InterfaceConnectivityRoleEnum.UNKNOWN);
    }

    @JsonIgnore
    public void setInterfaceConnectivityRole(InterfaceConnectivityRoleEnum interfaceConnectivityRole) {
        if(interfaceConnectivityRole == null){
            interfaceConnectivityRole = InterfaceConnectivityRoleEnum.UNKNOWN;
        }
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_INTERFACE_CONNECTIVITY_ROLE.getExtensionName(), interfaceConnectivityRole.getRoleName());
    }

    public ApplicationInterfaceMetricsData getMetricsData() {
        return metricsData;
    }

    public void setMetricsData(ApplicationInterfaceMetricsData metricsData) {
        this.metricsData = metricsData;
    }

    @JsonIgnore
    public InterfaceContentRoleEnum getInterfaceContentRole() {
        String contentRole = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_INTERFACE_CONTENT_ROLE.getExtensionName());
        if(contentRole != null){
            InterfaceContentRoleEnum contentRoleEnum = InterfaceContentRoleEnum.getContentRoleByName(contentRole);
            return (contentRoleEnum);
        }
        return(InterfaceContentRoleEnum.UNKNOWN);
    }

    @JsonIgnore
    public void setInterfaceContentRole(InterfaceContentRoleEnum interfaceRole) {
        if(interfaceRole == null){
            interfaceRole = InterfaceContentRoleEnum.UNKNOWN;
        }
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_INTERFACE_CONTENT_ROLE.getExtensionName(), interfaceRole.getContentRoleName());
    }

    @JsonIgnore
    public void setURI(URI endpointURI) {
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ENDPOINT_URI.getExtensionName(), endpointURI.toString());
    }

    @JsonIgnore
    public URI getURI() {
        String endpointURIString = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ENDPOINT_URI.getExtensionName());
        if(endpointURIString != null){
            return(URI.create(endpointURIString));
        }
        return(null);
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("metricsData", getMetricsData())
                .append("owner", getOwner())
                .append("services", getServices())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getObjectId())
                .append("interfaceContentRole", getInterfaceContentRole())
                .append("interfaceConnectivityRole", getInterfaceConnectivityRole())
                .append("adapters", getAdapters())
                .append("componentStatus", getComponentStatus())
                .toString();
    }

    //
     // Private Class
    //

    public enum InterfaceContentRoleEnum {
        SUBSCRIBER("Subscriber","Subscriber"),
        PUBLISHER("Publisher","Publisher"),
        UNKNOWN("Unknown","Unknown");

        private String contentRoleName;
        private String contentRoleDescription;

        private InterfaceContentRoleEnum(String name, String description){
            this.contentRoleName = name;
            this.contentRoleDescription = description;
        }

        public String getContentRoleName(){
            return(contentRoleName);
        }
        public String getContentRoleDescription(){
            return(contentRoleDescription);
        }
        public static InterfaceContentRoleEnum getContentRoleByName(String name){
            for(InterfaceContentRoleEnum role : InterfaceContentRoleEnum.values()){
                if(role.getContentRoleName().equals(name)){
                    return(role);
                }
            }
            return(null);
        }
    }

    public enum InterfaceConnectivityRoleEnum {
        EXTERNAL_SERVER("ExternalServer","External Server"),
        INTERNAL_SERVER("InternalServer","Internal Server"),
        INTERNAL_CLIENT("InternalClient","Internal Client"),
        EXTERNAL_CLIENT("ExternalClient","External Client"),
        INTERNAL_PEER("InternalPeer","Internal Peer"),
        EXTERNAL_PEER("ExternalPeer","External Peer"),
        UNKNOWN("Unknown","Unknown");

        private String roleName;
        private String roleDescription;

        private InterfaceConnectivityRoleEnum(String name, String description){
            this.roleName = name;
            this.roleDescription = description;
        }

        public String getRoleName(){
            return(roleName);
        }
        public String getRoleDescription(){
            return(roleDescription);
        }

        public static InterfaceConnectivityRoleEnum getRoleByName(String name){
            for(InterfaceConnectivityRoleEnum role : InterfaceConnectivityRoleEnum.values()){
                if(role.getRoleName().equals(name)){
                    return(role);
                }
            }
            return(UNKNOWN);
        }
    }
}
