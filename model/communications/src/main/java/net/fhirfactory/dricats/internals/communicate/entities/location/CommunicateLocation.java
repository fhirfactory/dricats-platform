/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.communicate.entities.location;

import net.fhirfactory.dricats.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import net.fhirfactory.dricats.resources.entities.Location;


import java.io.Serial;
import java.util.Objects;

public class CommunicateLocation extends Location {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900042L;

    //
    // Attributes
    //

    private CommunicateUserReference surrogateCommunicateUser;

    //
    // Constructor(s)
    //

    public CommunicateLocation() {
        super();
        this.surrogateCommunicateUser = null;
    }

    //
    // Bean Methods
    //

    public CommunicateUserReference getSurrogateCommunicateUser() {
        return surrogateCommunicateUser;
    }

    public void setSurrogateCommunicateUser(CommunicateUserReference surrogateCommunicateUser) {
        this.surrogateCommunicateUser = surrogateCommunicateUser;
    }

    //
    // Utility Methods
    //




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommunicateLocation that = (CommunicateLocation) o;
        return Objects.equals(getSurrogateCommunicateUser(), that.getSurrogateCommunicateUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSurrogateCommunicateUser());
    }
}
