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
package net.fhirfactory.dricats.internals.common.datatypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class HumanName implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private String use;
    private String text;
    private String family;
    private Map<Integer, String> givenNames;
    private String prefix;
    private String suffix;
    private EffectiveDate period;

    //
    // Constructor(s)
    //
    public HumanName() {
        super();
        period = new EffectiveDate();
        givenNames = new HashMap<>();
    }

    //
    // Getters and Setters
    //

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Map<Integer, String> getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(Map<Integer, String> givenNames) {
        this.givenNames = givenNames;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
        return new StringJoiner(", ", HumanName.class.getSimpleName() + "[", "]")
                .add("use='" + getUse() + "'")
                .add("text='" + getText() + "'")
                .add("family='" + getFamily() + "'")
                .add("givenNames=" + getGivenNames())
                .add("prefix='" + getPrefix() + "'")
                .add("suffix='" + getSuffix() + "'")
                .add("period=" + getPeriod())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HumanName humanName = (HumanName) o;
        return Objects.equals(getUse(), humanName.getUse()) && Objects.equals(getText(), humanName.getText()) && Objects.equals(getFamily(), humanName.getFamily()) && Objects.equals(getGivenNames(), humanName.getGivenNames()) && Objects.equals(getPrefix(), humanName.getPrefix()) && Objects.equals(getSuffix(), humanName.getSuffix()) && Objects.equals(getPeriod(), humanName.getPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUse(), getText(), getFamily(), getGivenNames(), getPrefix(), getSuffix(), getPeriod());
    }
}
