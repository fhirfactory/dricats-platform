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
package net.fhirfactory.dricats.model.topology.implementation.common.valuesets;

import java.net.URI;

import net.fhirfactory.dricats.model.common.DistributableObjectIdentifierType;

public enum TopologyComponentIdentifierTypeEnum {
    IDENTIFIER_TYPE_SOFTWARE_COMPONENT_ID("SoftwareComponentIdentifier", "dricats.identifiers.applications-component.id", "https://fhirfactory.net/CodeSystems/IdentifierTypes"),
    IDENTIFIER_TYPE_INFRASTRUCTURE_COMPONENT_ID("InfrastructureComponentIdentifier", "dricats.identifiers.infrastructure-component.id", "https://fhirfactory.net/CodeSystems/IdentifierTypes"),
    IDENTIFIER_TYPE_SYSTEM_NAME("SystemNameIdentifier", "dricats.identifiers.system.name", "https://fhirfactory.net/CodeSystems/IdentifierTypes"),
    IDENTIFIER_TYPE_JGROUPS_CHANNEL_NAME("ChannelNameIdentifier", "dricats.identifiers.channel.name", "https://fhirfactory.net/CodeSystems/IdentifierTypes");

    private final String identifierTypeCode;
    private final String identifierTypeDisplay;
    private final URI identifierTypeSystem;

    private TopologyComponentIdentifierTypeEnum(String display, String code, String system){
        this.identifierTypeCode = code;
        this.identifierTypeDisplay = display;
        this.identifierTypeSystem = URI.create(system);
    }

    public String getIdentifierTypeCode() {
        return identifierTypeCode;
    }

    public String getIdentifierTypeDisplay() {
        return identifierTypeDisplay;
    }

    public URI getIdentifierTypeSystem() {
        return identifierTypeSystem;
    }

    public DistributableObjectIdentifierType toDistributableObjectIdentifierType() {
        DistributableObjectIdentifierType distributableObjectIdentifierType = new DistributableObjectIdentifierType();
        distributableObjectIdentifierType.setCode(identifierTypeCode);
        distributableObjectIdentifierType.setDisplay(identifierTypeDisplay);
        distributableObjectIdentifierType.setSystem(identifierTypeSystem);
        return(distributableObjectIdentifierType);
    }
}
