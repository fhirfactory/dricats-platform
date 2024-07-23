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
package net.fhirfactory.dricats.internals.model.core.individuals;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.model.base.DistributableObject;
import net.fhirfactory.dricats.internals.model.core.individuals.datatypes.GroupMember;
import net.fhirfactory.dricats.internals.model.core.individuals.datatypes.GroupType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group extends DistributableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900038L;
    private static final Logger LOG = LoggerFactory.getLogger(Group.class);

    //
    // Attributes
    //

    private String groupId;
    private String groupName;
    private List<GroupMember> groupMembers;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime modificationDate;
    private GroupType groupType;

    //
    // Constructor(s)
    //

    public Group(){
        setGroupMembers(new ArrayList<>());
        setCreationDate(LocalDateTime.now());
        setModificationDate(LocalDateTime.now());
        setGroupType(new GroupType());
    }

    public Group(Group ori){
        setGroupId(ori.getGroupId());
        setGroupName(ori.getGroupName());
        if(getGroupMembers() == null) {
            setGroupMembers(new ArrayList<>());
        }
        getGroupMembers().addAll(ori.getGroupMembers());
        setCreationDate(ori.getCreationDate());
        setModificationDate(ori.getModificationDate());
        setGroupType(SerializationUtils.clone(ori.getGroupType()));
    }

    //
    // Bean Methods
    //

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!super.equals(o)) return false;
        Group group = (Group) o;
        return Objects.equals(getGroupId(), group.getGroupId()) && Objects.equals(getGroupName(), group.getGroupName()) && Objects.equals(getGroupMembers(), group.getGroupMembers()) && Objects.equals(getCreationDate(), group.getCreationDate()) && Objects.equals(getModificationDate(), group.getModificationDate()) && Objects.equals(getGroupType(), group.getGroupType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getGroupId(), getGroupName(), getGroupMembers(), getCreationDate(), getModificationDate(), getGroupType());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Group{");
        sb.append("groupId='").append(getGroupId()).append('\'');
        sb.append(", groupName='").append(getGroupName()).append('\'');
        sb.append(", groupMembers=").append(getGroupMembers());
        sb.append(", creationDate=").append(getCreationDate());
        sb.append(", modificationDate=").append(getModificationDate());
        sb.append(", groupType=").append(getGroupType());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id=").append(getId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append('}');
        return sb.toString();
    }
}
