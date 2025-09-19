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
package net.fhirfactory.dricats.resources.base.entities;

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.resources.base.entities.datatypes.Address;
import net.fhirfactory.dricats.resources.base.entities.datatypes.ContactPoint;
import net.fhirfactory.dricats.resources.base.entities.datatypes.TradingName;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Organisation extends DistributableObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private List<TradingName> tradingNames;
    private Address address;
    private List<ContactPoint> contactPoints;

    //
    // Constructor(s)
    //

    public Organisation() {
        super();
        contactPoints = new ArrayList<>();
        tradingNames = new ArrayList<>();
    }

    //
     // Bean Methods
    //

    public List<TradingName> getTradingNames() {
        return tradingNames;
    }
    public void setTradingNames(List<TradingName> tradingNames) {
        this.tradingNames = tradingNames;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new StringJoiner(", ", Organisation.class.getSimpleName() + "[", "]")
                .add("tradingNames=" + getTradingNames())
                .add("address=" + getAddress())
                .add("contactPoints=" + getContactPoints())
                .add("objectID=" + getObjectID())
                .add("securityLabels=" + getSecurityLabels())
                .add("metadata=" + getMetadata())
                .add("identifiers=" + getIdentifiers())
                .add("id=" + getId())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Organisation that = (Organisation) o;
        return Objects.equals(tradingNames, that.tradingNames) && Objects.equals(address, that.address) && Objects.equals(contactPoints, that.contactPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTradingNames(), getAddress(), getContactPoints());
    }
}
