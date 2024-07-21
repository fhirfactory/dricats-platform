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

import net.fhirfactory.dricats.internals.model.core.valuesets.TeamTypeEnum;

import java.io.Serial;
import java.io.Serializable;

public class TeamType implements Serializable {
    @Serial
    private static final long serialVersionUID = -12345678900024L;

    private TeamTypeEnum teamType;
    private String typeName;
    private String typeDescription;

    //
    // Constructor(s)
    //

    public TeamType(){
        setTeamType(TeamTypeEnum.TEAM_TYPE_UNKNOWN);
    }

    //
    // Bean Methods
    //

    public TeamTypeEnum getTeamType() {
        return teamType;
    }

    public void setTeamType(TeamTypeEnum teamType) {
        this.teamType = teamType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }
}
