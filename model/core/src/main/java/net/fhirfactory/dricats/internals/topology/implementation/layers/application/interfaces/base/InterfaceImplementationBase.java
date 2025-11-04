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
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationInterfaceMetricsData;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationInterface;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;

public class InterfaceImplementationBase extends ApplicationInterface implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceImplementationBase.class);

    //
     // Constants
    //
    public final static String PROPERTY_URL = "PROPERTY_URL";


    //
    // Member Variables
    //

    private ApplicationInterfaceMetricsData metricsData;
    private EgressInterfaceRole egressInterfaceRole;
    private IngressInterfaceRole ingressInterfaceRole;
    private InterfaceRoleEnum interfaceRole;


    //
    // Constructor(s)
    //

    public InterfaceImplementationBase() {
        super();
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.interfaceRole = InterfaceRoleEnum.BOTH;
        getLogger().trace("ApplicationInterface(): constructed");
    }

    public InterfaceImplementationBase(String name, String documentation, String interfaceSpecialisation) {
        super(name, documentation, interfaceSpecialisation);
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.interfaceRole = InterfaceRoleEnum.BOTH;
        getLogger().trace("ApplicationInterface(name, documentation, interfaceSpecialisation): constructed");
    }

    public InterfaceImplementationBase(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation) {
        super(parent, name, documentation, interfaceSpecialisation);
        setMetricsData(new ApplicationInterfaceMetricsData());
        this.interfaceRole = InterfaceRoleEnum.BOTH;
        getLogger().trace("ApplicationInterface(parent, name, documentation, interfaceSpecialisation): constructed");
    }

    public InterfaceImplementationBase(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, URI uri) {
        super(parent, name, documentation, interfaceSpecialisation, uri);
        // Ensure the provided URI is actually stored on this interface
        if (uri != null) {
            setURI(uri);
        }
        this.interfaceRole = InterfaceRoleEnum.BOTH;
        setMetricsData(new ApplicationInterfaceMetricsData());
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    public InterfaceImplementationBase(
            DistributableObjectId parent,
            String name,
            String documentation,
            URI endpointURI,
            String interfaceSpecialisation ) {
        super(parent, name, documentation, interfaceSpecialisation);
        setURI(endpointURI);
        this.interfaceRole = InterfaceRoleEnum.BOTH;
        setMetricsData(new ApplicationInterfaceMetricsData());
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    //
    // Bean Methods
    //


    public ApplicationInterfaceMetricsData getMetricsData() {
        return metricsData;
    }

    public void setMetricsData(ApplicationInterfaceMetricsData metricsData) {
        this.metricsData = metricsData;
    }

    @JsonIgnore
    public void setURI(URI uri){
        String uriString = uri.toString();
        getProperties().put(PROPERTY_URL, uriString);
    }

    @JsonIgnore
    public URI getURI(){
        String uriString = getProperties().get(PROPERTY_URL);
        if (uriString == null) {
            return null;
        }
        URI uri = URI.create(uriString);
        return (uri);
    }

    public EgressInterfaceRole getEgressInterfaceRole() {
        return egressInterfaceRole;
    }

    public void setEgressInterfaceRole(EgressInterfaceRole egressInterfaceRole) {
        this.egressInterfaceRole = egressInterfaceRole;
    }

    public IngressInterfaceRole getIngressInterfaceRole() {
        return ingressInterfaceRole;
    }

    public void setIngressInterfaceRole(IngressInterfaceRole ingressInterfaceRole) {
        this.ingressInterfaceRole = ingressInterfaceRole;
    }

    public InterfaceRoleEnum getInterfaceRole() {
        return interfaceRole;
    }
    public void setInterfaceRole(InterfaceRoleEnum interfaceRole) {
        this.interfaceRole = interfaceRole;
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
                .append("properties", getProperties())
                .append("securityLabels", getSecurityLabels())
                .append("objectID", getObjectID())
                .append("metadata", getMetadata())
                .append("id", getId())
                .append("uri", getURI())
                .append("egressInterfaceRole", getEgressInterfaceRole())
                .append("ingressInterfaceRole", getIngressInterfaceRole())
                .append("interfaceRole", getInterfaceRole())
                .toString();
    }

    //
     // Private Class
    //

    public enum InterfaceRoleEnum {
        INGRESS,
        EGRESS,
        BOTH
    }
}
