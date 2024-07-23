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
package net.fhirfactory.dricats.internals.model.core.entity.datatypes;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.core.individuals.valuesets.TeamMemberTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class TeamMember implements Serializable {
    @Serial
    private static final long serialVersionUID = -12345678900020L;

    private TeamMemberTypeEnum memberType;
    private DistributableObjectReference memberReference;
    private LocalDateTime memberJoinDate;
    private LocalDateTime memberLeaveDate;

    //
    // Constructor(s)
    //

    public TeamMember() {
        setMemberType(TeamMemberTypeEnum.TEAM_MEMBER_TYPE_STANDARD);
    }

    //
    // Bean Methods
    //

    public LocalDateTime getMemberJoinDate() {
        return memberJoinDate;
    }

    public void setMemberJoinDate(LocalDateTime memberJoinDate) {
        this.memberJoinDate = memberJoinDate;
    }

    public LocalDateTime getMemberLeaveDate() {
        return memberLeaveDate;
    }

    public void setMemberLeaveDate(LocalDateTime memberLeaveDate) {
        this.memberLeaveDate = memberLeaveDate;
    }

    public TeamMemberTypeEnum getMemberType() {
        return memberType;
    }

    public void setMemberType(TeamMemberTypeEnum memberType) {
        this.memberType = memberType;
    }

    public DistributableObjectReference getMemberReference() {
        return memberReference;
    }

    public void setMemberReference(DistributableObjectReference memberReference) {
        this.memberReference = memberReference;
    }
}
