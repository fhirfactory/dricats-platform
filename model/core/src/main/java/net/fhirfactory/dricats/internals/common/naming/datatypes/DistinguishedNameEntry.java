/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.common.naming.datatypes;


import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;

import java.io.Serial;
import java.util.Objects;

public class DistinguishedNameEntry extends RelativeDistinguishedName {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //
    private Integer sequenceNumber;

    //
    // Constructor(s)
    //

    public DistinguishedNameEntry() {
        super();
        this.sequenceNumber = null;
    }

    public DistinguishedNameEntry(String rdnQualifier, String rdnValue) {
        super(rdnQualifier, rdnValue);
        this.sequenceNumber = 0;
    }

    public DistinguishedNameEntry(RelativeDistinguishedName toBeAddedUnqualifiedName) {
        super(toBeAddedUnqualifiedName);
        this.sequenceNumber = 0;
    }


    //
    // Accessor(s)
    //

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    //
    // Standard Methods
    //
    @Override
    public String toString() {
        return "UnqualifiedNameEntry{" +
                "sequenceNumber=" + sequenceNumber +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DistinguishedNameEntry that = (DistinguishedNameEntry) o;
        return Objects.equals(getSequenceNumber(), that.getSequenceNumber()) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getSequenceNumber()) + super.hashCode();
    }
}
