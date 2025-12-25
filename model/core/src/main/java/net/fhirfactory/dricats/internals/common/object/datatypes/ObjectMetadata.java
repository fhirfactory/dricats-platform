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
package net.fhirfactory.dricats.internals.common.object.datatypes;

import java.io.Serial;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.common.object.SerialisableObject;
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ObjectMetadata extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //

    private URI sourceSystem;
    private String sourceSystemName;
    private EffectiveDate effectiveDate;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime creationDate;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT, timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime lastUpdateDate;
    private Map<URI, ObjectType> localObjectTypes;
    private Map<URI, ObjectId> localObjectIds;

    //
     // Constructor(s)
    //

    public ObjectMetadata() {
        super();
        this.effectiveDate = new EffectiveDate();
        this.creationDate = LocalDateTime.now();
        this.lastUpdateDate = LocalDateTime.now();
        this.localObjectIds = new HashMap<>();
        this.localObjectTypes = new HashMap<>();
    }

    //
    // Bean Methods
    //

    public String getSourceSystemName() {
        return sourceSystemName;
    }
    public void setSourceSystemName(String sourceSystemName) {
        this.sourceSystemName = sourceSystemName;
    }

    public URI getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(URI sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Map<URI, ObjectType> getLocalObjectTypes() {
        return localObjectTypes;
    }

    public void setLocalObjectTypes(Map<URI, ObjectType> localObjectTypes) {
        this.localObjectTypes = localObjectTypes;
    }

    public Map<URI, ObjectId> getLocalObjectIds() {
        return localObjectIds;
    }

    public void setLocalObjectIds(Map<URI, ObjectId> localObjectIds) {
        this.localObjectIds = localObjectIds;
    }

    //
     // Standard Methods
     //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sourceSystem", sourceSystem)
                .append("sourceSystemName", sourceSystemName)
                .append("effectiveDate", effectiveDate)
                .append("creationDate", creationDate)
                .append("lastUpdateDate", lastUpdateDate)
                .append("localObjectIds", localObjectIds)
                .append("localObjectTypes", localObjectTypes)
                .toString();
    }
}
