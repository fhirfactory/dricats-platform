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

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.resources.base.entities.datatypes.ContactPoint;
import net.fhirfactory.dricats.resources.base.entities.datatypes.TeamMembership;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Team extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String name;
    private String description;
    private String type;
    private String status;
    private ContactPoint contact;
    private List<TeamMembership> members;

    //
    // Constructor(s)
    //
    public Team() {
        super();
        members = new java.util.ArrayList<>();
    }

    public Team(Team ori) {
        super(ori);
        this.name = ori.name;
        this.description = ori.description;
        this.type = ori.type;
        this.status = ori.status;
        this.contact = SerializationUtils.clone(ori.contact);
        this.members = new ArrayList<>();
        this.members.addAll(ori.members);
    }

    //
    // Getters and Setters
    //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ContactPoint getContact() {
        return contact;
    }

    public void setContact(ContactPoint contact) {
        this.contact = contact;
    }

    public List<TeamMembership> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMembership> members) {
        this.members = members;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", Team.class.getSimpleName() + "[", "]")
                .add("name='" + getName() + "'")
                .add("description='" + getDescription() + "'")
                .add("type='" + getType() + "'")
                .add("status='" + getStatus() + "'")
                .add("contact=" + getContact())
                .add("members=" + getMembers())
                .add("objectID=" + getObjectID())
                .add("securityLabels=" + getSecurityLabels())
                .add("metadata=" + getMetadata())
                .add("identifiers=" + getIdentifiers())
                .add("id=" + getId())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(getName(), team.getName()) && Objects.equals(getType(), team.getType()) && Objects.equals(getStatus(), team.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType(), getStatus());
    }
}
