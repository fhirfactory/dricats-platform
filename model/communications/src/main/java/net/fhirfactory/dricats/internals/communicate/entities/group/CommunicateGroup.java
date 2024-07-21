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
package net.fhirfactory.dricats.internals.communicate.entities.group;

import net.fhirfactory.dricats.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.dricats.internals.communicate.entities.user.CommunicateUser;
import net.fhirfactory.dricats.internals.model.core.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.concurrent.ConcurrentHashMap;

public class CommunicateGroup extends Group {
    @Serial
    private static final long serialVersionUID = -12345678900045L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateGroup.class);

    private ConcurrentHashMap<String, CommunicateRoomReference> allRooms;
    private CommunicateUser surrogateCommunicateUser;

    //
    // Constructor(s)
    //

    public CommunicateGroup() {
        super();
        this.allRooms = new ConcurrentHashMap<>();
        this.surrogateCommunicateUser = null;
    }

    //
    // Bean Methods
    //

    public ConcurrentHashMap<String, CommunicateRoomReference> getAllRooms() {
        return allRooms;
    }

    public void setAllRooms(ConcurrentHashMap<String, CommunicateRoomReference> allRooms) {
        this.allRooms = allRooms;
    }

    public CommunicateUser getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUser surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    //
    // Utility Methods
    //


    @Override
    protected Logger getLogger() {
        return (LOG);
    }
}
