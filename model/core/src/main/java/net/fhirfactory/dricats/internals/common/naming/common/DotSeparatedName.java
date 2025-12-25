/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.common.naming.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class DotSeparatedName implements Serializable {
    //
    // housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //

    public static String DEFAULT_NAME = "UNNAMED";

    private String value;

    //
    // Constructor(s)
    //

    public DotSeparatedName() {
        value = DEFAULT_NAME;
    }

    @JsonCreator
    public DotSeparatedName(String value) {
        this.value = SerializationUtils.clone(value);
    }

    public DotSeparatedName(DotSeparatedName ori) {
        this.value = SerializationUtils.clone(ori.getValue());
    }


    //
    // Accessor(s)
    //
    @JsonValue
    public String getValue() {
        return (this.value);
    }

    public void setValue(String tokenContent) {
        this.value = new String(tokenContent);
    }

    @JsonIgnore
    public boolean isUnnamed() {
        boolean test = StringUtils.isEmpty(this.value) || DEFAULT_NAME.equals(this.value);
        return (test);
    }

    //
    // Standard Methods
    //
    @Override
    public String toString() {
        return "CommonName{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DotSeparatedName contextualName = (DotSeparatedName) o;
        return (contextualName.getValue().contentEquals(this.getValue()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}