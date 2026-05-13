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
package net.fhirfactory.dricats.resources.entities;

import net.fhirfactory.dricats.internals.datatypes.Address;
import net.fhirfactory.dricats.internals.datatypes.GlobalPosition;
import net.fhirfactory.dricats.reference.archimate.layers.business.BusinessActor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Location extends BusinessActor implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String status;
    private String description;
    private String mode;
    private GlobalPosition position;
    private Address address;
    private String physicalType;

    //
    // Constructor(s)
    //

    public Location() {
        super();
        position = new GlobalPosition();
        address = new Address();
    }

    //
    // Getters and Setters
    //

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public GlobalPosition getPosition() {
        return position;
    }

    public void setPosition(GlobalPosition position) {
        this.position = position;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhysicalType() {
        return physicalType;
    }

    public void setPhysicalType(String physicalType) {
        this.physicalType = physicalType;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", getStatus())
                .append("description", getDescription())
                .append("mode", getMode())
                .append("position", getPosition())
                .append("address", getAddress())
                .append("physicalType", getPhysicalType())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("elementType", getElementType())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("addresses", getAddresses())
                .append("category", getCategory())
                .append("internal", isInternal())
                .append("roles", getRoles())
                .append("contactPoints", getContactPoints())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(getStatus(), location.getStatus()) && Objects.equals(getDescription(), location.getDescription()) && Objects.equals(getMode(), location.getMode()) && Objects.equals(getPosition(), location.getPosition()) && Objects.equals(getAddress(), location.getAddress()) && Objects.equals(getPhysicalType(), location.getPhysicalType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getDescription(), getMode(), getPosition(), getAddress(), getPhysicalType());
    }
}
