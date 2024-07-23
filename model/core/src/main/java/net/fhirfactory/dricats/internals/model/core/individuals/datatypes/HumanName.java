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
package net.fhirfactory.dricats.internals.model.core.individuals.datatypes;

import net.fhirfactory.dricats.internals.model.base.dataytypes.EffectiveDate;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HumanName implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900016L;

    //
    // Attributes
    //

    private String givenName;
    private String familyName;
    private String prefix;
    private String suffix;
    private Map<Integer, String> middleNames;
    private EffectiveDate effectiveDate;

    //
    // Constructor(s)
    //

    public HumanName() {
        setMiddleNames(new HashMap<>());
    }

    //
    // Bean Methods
    //

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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

    public Map<Integer, String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(Map<Integer, String> middleNames) {
        this.middleNames = middleNames;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HumanName{");
        sb.append("givenName='").append(getGivenName()).append('\'');
        sb.append(", familyName='").append(getFamilyName()).append('\'');
        sb.append(", prefix='").append(getPrefix()).append('\'');
        sb.append(", suffix='").append(getSuffix()).append('\'');
        sb.append(", middleNames=").append(getMiddleNames());
        sb.append(", effectiveDate=").append(getEffectiveDate());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HumanName humanName = (HumanName) o;
        return Objects.equals(getGivenName(), humanName.getGivenName()) && Objects.equals(getFamilyName(), humanName.getFamilyName()) && Objects.equals(getPrefix(), humanName.getPrefix()) && Objects.equals(getSuffix(), humanName.getSuffix()) && Objects.equals(getMiddleNames(), humanName.getMiddleNames()) && Objects.equals(getEffectiveDate(), humanName.getEffectiveDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGivenName(), getFamilyName(), getPrefix(), getSuffix(), getMiddleNames(), getEffectiveDate());
    }
}
