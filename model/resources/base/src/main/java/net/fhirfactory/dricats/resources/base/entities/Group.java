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
package net.fhirfactory.dricats.resources.base.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.internals.common.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.common.naming.CommonName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.StringJoiner;

public class Group extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private String groupType;
    private String description;
    private DistributableObjectIdentifier managingOrganization;
    private boolean active;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime modificationDate;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime creationDate;

    //
    // Constructor(s)
    //
    public Group(){
        super();
    }

    //
    // Getters and Setters
    //

    public String getGroupType() {
        return groupType;
    }
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public DistributableObjectIdentifier getManagingOrganization() {
        return managingOrganization;
    }
    public void setManagingOrganization(DistributableObjectIdentifier managingOrganization) {
        this.managingOrganization = managingOrganization;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", Group.class.getSimpleName() + "[", "]")
                .add("groupType='" + getGroupType() + "'")
                .add("description='" + getDescription() + "'")
                .add("managingOrganization=" + getManagingOrganization())
                .add("active=" + isActive())
                .add("modificationDate=" + getModificationDate())
                .add("creationDate=" + getCreationDate())
                .add("objectID=" + getObjectID())
                .add("securityLabels=" + getSecurityLabels())
                .add("metadata=" + getMetadata())
                .add("identifiers=" + getIdentifiers())
                .add("id=" + getId())
                .toString();
    }
}
