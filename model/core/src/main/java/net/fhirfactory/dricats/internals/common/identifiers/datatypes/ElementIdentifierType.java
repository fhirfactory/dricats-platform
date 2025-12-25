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
package net.fhirfactory.dricats.internals.common.identifiers.datatypes;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

public class ElementIdentifierType implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900005L;

    //
    // Attributes
    //

    private String code;
    private String value;
    private String display;
    private URI system;

    //
    // Constructor(s)
    //
    public ElementIdentifierType() {
        super();
        this.code = "COMMON_ID";
        this.value = "CommonId";
        this.display = "Common Id (Token derived from DistributableObjectId)";
        this.system = URI.create("http://fhirfactory.net/dricats/valuesets/identifier_types");
    }

    //
    // Getters and Setters
    //

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public URI getSystem() {
        return system;
    }

    public void setSystem(URI system) {
        this.system = system;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributableObjectIdentifierType{");
        sb.append("code='").append(getCode()).append('\'');
        sb.append(", value='").append(getValue()).append('\'');
        sb.append(", display='").append(getDisplay()).append('\'');
        sb.append(", system=").append(getSystem());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementIdentifierType that = (ElementIdentifierType) o;
        return Objects.equals(getCode(), that.getCode()) && Objects.equals(getValue(), that.getValue()) && Objects.equals(getSystem(), that.getSystem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getValue(), getSystem());
    }
}
