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
package net.fhirfactory.dricats.resources.base.entities.datatypes;

import net.fhirfactory.dricats.internals.common.datatypes.EffectiveDate;

import java.io.Serial;
import java.io.Serializable;

public class TradingName implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //
    private String value;
    private EffectiveDate effectiveDate;

    //
    // Constructor(s)
    //

    public TradingName() {
        value = "";
        effectiveDate = new EffectiveDate();
    }

    //
    // Bean Methods
    //
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return("TradingName [value=" + value + ", effectiveDate=" + effectiveDate + "]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradingName that = (TradingName) o;
        return value.equals(that.value) && effectiveDate.equals(that.effectiveDate);
    }

    @Override
    public int hashCode() {
        return value.hashCode() + effectiveDate.hashCode();
    }
}
