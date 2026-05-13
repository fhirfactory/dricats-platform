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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.id.ElementInstanceId;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.common.TopologyComponent;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.technology.valuesets.NetworkSecurityZoneEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public abstract class Subsystem extends TopologyComponent implements ISubsystem {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900093L;
    private static final Logger LOG = LoggerFactory.getLogger(Subsystem.class);
    
    //
    // Attributes
    //

    //
    // Constructor(s)
    //

    public Subsystem(){
        super();
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getType());

    }

    public Subsystem(ElementReference parent, String shortName, String description, String specialization, Map<String, String> extensions){
        super(parent, shortName, description, specialization, extensions);
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getType());
    }

    //
    // abstract methods
    //

    protected abstract ElementIdentifier specifySubsystemIdentifier();

    //
    // Interface Implementation
    //

    @JsonIgnore
    public Subsystem getSubsystem(){
        return(this);
    }

    //
    // Getters and Setters
    //
    
    @JsonIgnore
	public NetworkSecurityZoneEnum getNetworkSecurityZone() {
        String securityZone = getExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_NETWORK_SECURITY_ZONE.getExtensionName());
        if(securityZone != null){
            NetworkSecurityZoneEnum securityZoneEnum = NetworkSecurityZoneEnum.fromName(securityZone);
            return(securityZoneEnum);
        }
		return NetworkSecurityZoneEnum.INTERNET;
	}

    @JsonIgnore
	public void setNetworkSecurityZone(NetworkSecurityZoneEnum networkSecurityZone) {
        if(networkSecurityZone == null){
            networkSecurityZone = NetworkSecurityZoneEnum.INTERNET;
        }
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_NETWORK_SECURITY_ZONE.getExtensionName(), networkSecurityZone.getName());
	}

	@JsonIgnore
	public Map<ElementInstanceId, SubsystemCluster> getApplicationCluster(){
		HashMap<ElementInstanceId, SubsystemCluster> applicationClusterSet = new HashMap<ElementInstanceId, SubsystemCluster>();
		// TODO add code to extract subsystem application cluster instances.
		return(applicationClusterSet);
	}

    //
    // Utility Methods
    //


    protected Logger getLogger(){
        return(LOG);
    }




}
