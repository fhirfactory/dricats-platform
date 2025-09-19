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
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class Address implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String use;
    private String type;
    private String text;
    private Map<Integer, String> lines;
    private String city;
    private String district;
    private String state;
    private String postalCode;
    private String country;
    private String period;
    private EffectiveDate effectiveDate;

    //
    // Constructor(s)
    //
    public Address() {
        super();
        lines = new java.util.TreeMap<>();
        effectiveDate = new EffectiveDate();
    }

    //
    // Getters and Setters
    //

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<Integer, String> getLines() {
        return lines;
    }

    public void setLines(Map<Integer, String> lines) {
        this.lines = lines;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getType() {
        return type;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]")
                .add("use='" + getUse() + "'")
                .add("type='" + getType() + "'")
                .add("text='" + getText() + "'")
                .add("lines=" + getLines())
                .add("city='" + getCity() + "'")
                .add("district='" + getDistrict() + "'")
                .add("state='" + getState() + "'")
                .add("postalCode='" + getPostalCode() + "'")
                .add("country='" + getCountry() + "'")
                .add("period='" + getPeriod() + "'")
                .add("effectiveDate=" + getEffectiveDate())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getUse(), address.getUse()) && Objects.equals(getType(), address.getType()) && Objects.equals(getText(), address.getText()) && Objects.equals(getLines(), address.getLines()) && Objects.equals(getCity(), address.getCity()) && Objects.equals(getDistrict(), address.getDistrict()) && Objects.equals(getState(), address.getState()) && Objects.equals(getPostalCode(), address.getPostalCode()) && Objects.equals(getCountry(), address.getCountry()) && Objects.equals(getPeriod(), address.getPeriod()) && Objects.equals(getEffectiveDate(), address.getEffectiveDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUse(), getType(), getText(), getLines(), getCity(), getDistrict(), getState(), getPostalCode(), getCountry(), getPeriod(), getEffectiveDate());
    }
}
