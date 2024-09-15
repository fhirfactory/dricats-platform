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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.model.base.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.model.software.interfaces.SubsystemInterface;
import net.fhirfactory.dricats.internals.model.software.valuesets.SoftwareComponentIdentifierTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.LocalDateTime;

public abstract class SoftwareSubsystem extends SoftwareComponent implements SubsystemInterface {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900093L;
    private static final Logger LOG = LoggerFactory.getLogger(SoftwareSubsystem.class);

    //
    // Constructor(s)
    //

    public SoftwareSubsystem(){
        super();
        getIdentifiers().add(specifySubsystemIdentifier());
    }

    //
    // abstract methods
    //

    abstract DistributableObjectIdentifier specifySubsystemIdentifier();

    //
    // Interface Implementation
    //

    public SoftwareSubsystem getSubsystem(){
        return(this);
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public String getSystemName() {
        DistributableObjectIdentifier identifier = getIdentifier(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_SYSTEM_NAME);
        if (identifier != null) {
            return (identifier.getIdentifierValue());
        }
        return (null);
    }

    @JsonIgnore
    public void setSystemName(String name) {
        DistributableObjectIdentifier identifier = getIdentifier(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_SYSTEM_NAME);
        if(identifier != null) {
            identifier.setIdentifierValue(name);
            identifier.getEffectiveDate().setEffectiveStartDate(LocalDateTime.now());
        } else {
            identifier = new DistributableObjectIdentifier();
            identifier.setIdentifierType(SoftwareComponentIdentifierTypeEnum.IDENTIFIER_TYPE_SYSTEM_NAME.toDistributableObjectIdentifierType());
            identifier.setIdentifierValue(name);
            addIdentifier(identifier);
        }
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SoftwareSubsystem{");
        sb.append("parentComponent=").append(getParentComponent());
        sb.append(", containedComponents=").append(getContainedComponents());
        sb.append(", componentStartupDate=").append(getComponentStartupDate());
        sb.append(", componentType=").append(getComponentType());
        sb.append(", subsystemDeploymentZone=").append(getSubsystemDeploymentZone());
        sb.append(", subsystemDeploymentGroup='").append(getSubsystemDeploymentGroup()).append('\'');
        sb.append(", subsystemDeploymentSite='").append(getSubsystemDeploymentSite()).append('\'');
        sb.append(", securityLabels=").append(getSecurityLabels());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append(", objectID='").append(getObjectID()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
