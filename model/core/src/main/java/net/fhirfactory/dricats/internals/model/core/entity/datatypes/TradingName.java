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
package net.fhirfactory.dricats.internals.model.core.entity.datatypes;

import net.fhirfactory.dricats.internals.model.base.dataytypes.EffectiveDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class TradingName implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900018L;

    //
    // Attributes
    //

    private String name;
    private EffectiveDate effectiveDate;

    //
    // Bean Methods
    //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TradingName{");
        sb.append("name='").append(getName()).append('\'');
        sb.append(", effectiveDate=").append(getEffectiveDate());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradingName that = (TradingName) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getEffectiveDate(), that.getEffectiveDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEffectiveDate());
    }
}
