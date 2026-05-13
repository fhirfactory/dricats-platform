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
package net.fhirfactory.dricats.resources.actors;

import net.fhirfactory.dricats.internals.datatypes.ContactPoint;
import net.fhirfactory.dricats.internals.datatypes.TeamMembership;
import net.fhirfactory.dricats.reference.archimate.layers.business.BusinessCollaboration;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team extends BusinessCollaboration implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
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
        this.type = ori.type;
        this.status = ori.status;
        this.contact = SerializationUtils.clone(ori.contact);
        this.members = new ArrayList<>();
        this.members.addAll(ori.members);
    }

    //
    // Getters and Setters
    //

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
        return new ToStringBuilder(this)
                .append("type", getType())
                .append("status", getStatus())
                .append("contact", getContact())
                .append("members", getMembers())
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
                .append("participants", getParticipants())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        boolean isEqual = super.equals(o) &&
                Objects.equals(getType(), team.getType()) &&
                Objects.equals(getStatus(), team.getStatus()) &&
                Objects.equals(getContact(), team.getContact());


        return  isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getShortName(), getType(), getStatus());
    }
}
