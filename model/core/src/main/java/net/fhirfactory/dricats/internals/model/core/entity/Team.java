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
package net.fhirfactory.dricats.internals.model.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.internals.model.base.DistributableObject;
import net.fhirfactory.dricats.internals.model.core.entity.datatypes.TeamFunction;
import net.fhirfactory.dricats.internals.model.core.entity.datatypes.TeamMember;
import net.fhirfactory.dricats.internals.model.core.entity.datatypes.TeamType;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team extends DistributableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900019L;
    private static final Logger LOG = LoggerFactory.getLogger(Team.class);

    //
    // Attributes
    //

    private String teamId;
    private String teamName;
    private List<TeamMember> members;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX", timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime modificationDate;
    private TeamFunction teamFunction;
    private TeamType teamType;

    //
    // Constructor(s)
    //

    public Team(){
        setMembers(new ArrayList<>());
        setCreationDate(LocalDateTime.now());
        setModificationDate(LocalDateTime.now());
        setTeamFunction(new TeamFunction());
        setTeamType(new TeamType());
    }

    public Team(Team ori){
        setTeamId(ori.getTeamId());
        setTeamName(ori.getTeamName());
        setMembers(new ArrayList<>());
        getMembers().addAll(ori.getMembers());
        setCreationDate(ori.getCreationDate());
        setModificationDate(ori.getModificationDate());
        setTeamFunction(SerializationUtils.clone(ori.getTeamFunction()));
        setTeamType(SerializationUtils.clone(ori.getTeamType()));
    }

    //
    // Bean Methods
    //

    public TeamFunction getTeamFunction() {
        return teamFunction;
    }

    public void setTeamFunction(TeamFunction teamFunction) {
        this.teamFunction = teamFunction;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
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

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Team{");
        sb.append("teamId='").append(getTeamId()).append('\'');
        sb.append(", teamName='").append(getTeamName()).append('\'');
        sb.append(", members=").append(getMembers());
        sb.append(", creationDate=").append(getCreationDate());
        sb.append(", modificationDate=").append(getModificationDate());
        sb.append(", teamFunction=").append(getTeamFunction());
        sb.append(", teamType=").append(getTeamType());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id=").append(getId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Team team = (Team) o;
        return Objects.equals(getTeamId(), team.getTeamId()) && Objects.equals(getTeamName(), team.getTeamName()) && Objects.equals(getMembers(), team.getMembers()) && Objects.equals(getCreationDate(), team.getCreationDate()) && Objects.equals(getModificationDate(), team.getModificationDate()) && Objects.equals(getTeamFunction(), team.getTeamFunction()) && Objects.equals(getTeamType(), team.getTeamType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTeamId(), getTeamName(), getMembers(), getCreationDate(), getModificationDate(), getTeamFunction(), getTeamType());
    }
}
