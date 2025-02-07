/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.communicate.entities.user.datatypes;

import net.fhirfactory.dricats.internals.model.base.DistributableObjectReference;
import net.fhirfactory.dricats.internals.model.core.individuals.datatypes.UserID;

public class CommunicateSurrogateResourceReference {
    private DistributableObjectReference realResourceID;
    private String realResourceType;
    private UserID surrogateUserID;

    public DistributableObjectReference getRealResourceID() {
        return realResourceID;
    }

    public void setRealResourceID(DistributableObjectReference realResourceID) {
        this.realResourceID = realResourceID;
    }

    public String getRealResourceType() {
        return realResourceType;
    }

    public void setRealResourceType(String realResourceType) {
        this.realResourceType = realResourceType;
    }

    public UserID getSurrogateUserID() {
        return surrogateUserID;
    }

    public void setSurrogateUserID(UserID surrogateUserID) {
        this.surrogateUserID = surrogateUserID;
    }

    @Override
    public String toString() {
        return "CommunicateSurrogateResourceReference{" +
                "realResourceID=" + realResourceID +
                ", realResourceType=" + realResourceType +
                ", surrogateUserID=" + surrogateUserID +
                '}';
    }
}
