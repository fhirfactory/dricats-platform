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
package net.fhirfactory.dricats.resources.base.identity;

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.resources.base.entities.datatypes.Address;
import net.fhirfactory.dricats.resources.base.entities.datatypes.ContactPoint;
import net.fhirfactory.dricats.resources.base.identity.datatypes.HumanName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class Person extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String gender;
    private List<HumanName> name;
    private LocalDateTime birthDate;
    private List<Address> address;
    private List<ContactPoint> contactPoints;
    private String status;

    //
    // Constructor(s)
    //

    public Person() {
        super();
        name = new java.util.ArrayList<>();
        address = new java.util.ArrayList<>();
        contactPoints = new java.util.ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<HumanName> getName() {
        return name;
    }

    public void setName(List<HumanName> name) {
        this.name = name;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    public List<ContactPoint> getContactPoints() {
        return contactPoints;
    }

    public void setContactPoints(List<ContactPoint> contactPoints) {
        this.contactPoints = contactPoints;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("gender='" + getGender() + "'")
                .add("name=" + getName())
                .add("birthDate=" + getBirthDate())
                .add("address=" + getAddress())
                .add("contactPoints=" + getContactPoints())
                .add("status='" + getStatus() + "'")
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
        Person person = (Person) o;
        return Objects.equals(getName(), person.getName()) && Objects.equals(getBirthDate(), person.getBirthDate()) && Objects.equals(getAddress(), person.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBirthDate(), getAddress());
    }
}
