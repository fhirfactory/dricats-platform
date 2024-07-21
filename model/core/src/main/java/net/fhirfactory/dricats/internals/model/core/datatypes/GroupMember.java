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
package net.fhirfactory.dricats.internals.model.core.datatypes;

import net.fhirfactory.dricats.internals.model.base.DistributedObjectReference;
import net.fhirfactory.dricats.internals.model.core.valuesets.GroupMemberTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class GroupMember implements Serializable {
    @Serial
    private static final long serialVersionUID = -12345678900029L;

    private GroupMemberTypeEnum memberType;
    private DistributedObjectReference memberReference;
    private LocalDateTime memberJoinDate;
    private LocalDateTime memberLeaveDate;

    //
    // Constructor(s)
    //

    public GroupMember() {
        setMemberType(GroupMemberTypeEnum.GROUP_MEMBER_TYPE_UNKNOWN);
    }

    //
    // Bean Methods
    //

    public GroupMemberTypeEnum getMemberType() {
        return memberType;
    }

    public void setMemberType(GroupMemberTypeEnum memberType) {
        this.memberType = memberType;
    }

    public DistributedObjectReference getMemberReference() {
        return memberReference;
    }

    public void setMemberReference(DistributedObjectReference memberReference) {
        this.memberReference = memberReference;
    }

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
}
