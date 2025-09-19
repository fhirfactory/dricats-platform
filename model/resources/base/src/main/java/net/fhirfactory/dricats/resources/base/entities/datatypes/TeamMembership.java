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
package net.fhirfactory.dricats.resources.base.entities.datatypes;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.datatypes.EffectiveDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

public class TeamMembership implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private DistributableObjectId member;
    private DistributableObjectId team;
    private String membershipRole;
    private String membershipStatus;
    private EffectiveDate period;

    //
     // Constructor(s)
    //
    public TeamMembership() {
        super();
        period = new EffectiveDate();
    }

    //
    // Getters and Setters
    //

    public DistributableObjectId getMember() {
        return member;
    }

    public void setMember(DistributableObjectId member) {
        this.member = member;
    }

    public DistributableObjectId getTeam() {
        return team;
    }

    public void setTeam(DistributableObjectId team) {
        this.team = team;
    }

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

    public EffectiveDate getPeriod() {
        return period;
    }

    public void setPeriod(EffectiveDate period) {
        this.period = period;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", TeamMembership.class.getSimpleName() + "[", "]")
                .add("member=" + getMember())
                .add("team=" + getTeam())
                .add("membershipRole='" + getMembershipRole() + "'")
                .add("membershipStatus='" + getMembershipStatus() + "'")
                .add("period=" + getPeriod())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TeamMembership that = (TeamMembership) o;
        return Objects.equals(getMember(), that.getMember()) && Objects.equals(getTeam(), that.getTeam()) && Objects.equals(getMembershipStatus(), that.getMembershipStatus()) && Objects.equals(getPeriod(), that.getPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMember(), getTeam(), getMembershipStatus(), getPeriod());
    }
}
