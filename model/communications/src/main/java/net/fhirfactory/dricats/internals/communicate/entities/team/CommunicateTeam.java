/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.communicate.entities.team;

import net.fhirfactory.dricats.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.dricats.internals.communicate.entities.session.CommunicateSession;
import net.fhirfactory.dricats.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.dricats.internals.model.core.entity.Team;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class CommunicateTeam extends Team {
    @Serial
    private static final long serialVersionUID = -12345678900039L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateTeam.class);
    private CommunicateRoomReference centralRoom;
    private CommunicateUserReference surrogateCommunicateUser;
    private List<CommunicateSession> practitionerConnections;

    //
    // Constructor(s)
    //

    public CommunicateTeam() {
        this.centralRoom = null;
        this.surrogateCommunicateUser = null;
        this.practitionerConnections = new ArrayList<>();
    }

    public CommunicateTeam(CommunicateTeam ori) {
        super(ori);
        this.centralRoom = SerializationUtils.clone(ori.getCentralRoom());
        this.surrogateCommunicateUser = ori.getSurrogateCommunicateUser();
        this.practitionerConnections = new ArrayList<>();
        this.practitionerConnections.addAll(ori.getPractitionerSessions());
    }

    //
    // Bean Methods
    //

    public CommunicateRoomReference getCentralRoom() {
        return centralRoom;
    }

    public void setCentralRoom(CommunicateRoomReference centralRoom) {
        this.centralRoom = centralRoom;
    }

    public List<CommunicateSession> getPractitionerSessions() {
        return (this.practitionerConnections);
    }

    public void setPractitionerSessions(List<CommunicateSession> sideRooms) {
        this.practitionerConnections = new ArrayList<>();
        this.practitionerConnections.addAll(sideRooms);
    }

    public CommunicateUserReference getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUserReference surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }
}
