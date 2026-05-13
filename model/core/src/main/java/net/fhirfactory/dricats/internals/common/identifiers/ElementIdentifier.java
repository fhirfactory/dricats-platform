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
package net.fhirfactory.dricats.internals.common.identifiers;

import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;

import java.io.Serial;
import java.io.Serializable;

public class
ElementIdentifier implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //

    private DistinguishedName identifierValue;
    private EffectiveDate effectiveDate;

    //
    // Constructor(s)
    //
    public ElementIdentifier() {
        super();
        this.identifierValue = new DistinguishedName();
        this.effectiveDate = new EffectiveDate();
    }

    public ElementIdentifier(DistinguishedName identifierValue) {
        super();
        this.identifierValue = identifierValue;
        this.effectiveDate = new EffectiveDate();
    }

    public ElementIdentifier(DistinguishedName identifierValue, EffectiveDate effectiveDate) {
        super();
        this.identifierValue = identifierValue;
        this.effectiveDate = effectiveDate;
    }

    public ElementIdentifier(ElementIdentifier ori) {
        super();
        this.identifierValue = ori.getIdentifierValue();
        this.effectiveDate = new EffectiveDate();
        this.effectiveDate.setEffectiveEndDate(ori.getEffectiveDate().getEffectiveEndDate());
        this.effectiveDate.setEffectiveStartDate(ori.getEffectiveDate().getEffectiveStartDate());
    }

    //
    // Bean Methods
    //

    public DistinguishedName getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(DistinguishedName identifierValue) {
        this.identifierValue = identifierValue;
    }

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return "DistributableObjectIdentifier{" +
                "identifierValue=" + identifierValue +
                ", effectiveDate=" + effectiveDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementIdentifier that = (ElementIdentifier) o;
        if(identifierValue == null && that.identifierValue == null){
            return(true);
        }
        if(identifierValue == null || that.identifierValue == null){
            return(false);
        }
        boolean effectiveDateEquals = effectiveDate.equals(that.effectiveDate);
        return identifierValue.equals(that.identifierValue) && effectiveDateEquals;
    }
}
