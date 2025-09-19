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
import java.util.StringJoiner;

public class ContactPoint implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String system;
    private String value;
    private String use;
    private Integer rank;
    private EffectiveDate period;

    //
    // Constructor(s)
    //
    public ContactPoint() {
        super();
        period = new EffectiveDate();
    }

    //
    // Getters and Setters
    //

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public EffectiveDate getPeriod() {
        return period;
    }

    public void setPeriod(EffectiveDate period) {
        this.period = period;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", ContactPoint.class.getSimpleName() + "[", "]")
                .add("system='" + getSystem() + "'")
                .add("value='" + getValue() + "'")
                .add("use='" + getUse() + "'")
                .add("rank=" + getRank())
                .add("period=" + getPeriod())
                .toString();
    }
}
