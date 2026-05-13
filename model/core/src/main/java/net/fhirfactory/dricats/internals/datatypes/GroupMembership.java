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
package net.fhirfactory.dricats.internals.datatypes;

import net.fhirfactory.dricats.reference.archimate.relationships.AggregationRelationshipBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class GroupMembership extends AggregationRelationshipBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
     // Member Variables
    //
    private String membershipRole;
    private String membershipStatus;

    //
    // Constructor(s)
    //

    public GroupMembership() {
        super();
    }

    //
    // Getters and Setters
    //
    public String getMembershipRole() {
        return membershipRole;
    }
    public void setMembershipRole(String membershipRole) {
        this.membershipRole = membershipRole;
    }
    public String getMembershipStatus() {
        return membershipStatus;
    }
    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("membershipRole", getMembershipRole())
                .append("membershipStatus", getMembershipStatus())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("type", getType())
                .append("source", getSource())
                .append("target", getTarget())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("properties", getProperties())
                .toString();
    }
}
