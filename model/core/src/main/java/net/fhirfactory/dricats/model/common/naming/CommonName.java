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
package net.fhirfactory.dricats.model.common.naming;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CommonName implements Serializable {
    //
    // housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //

    private String value;

    //
    // Constructor(s)
    //

    public CommonName() {
        value = "";
    }

    public CommonName(String tokenContent) {
        this.value = SerializationUtils.clone(tokenContent);
    }

    public CommonName(CommonName originalToken) {
        this.value = SerializationUtils.clone(originalToken.getValue());
    }

    public CommonName(QualifiedName qualifiedName) {
        QualifiedName tempQualifiedName = new QualifiedName(qualifiedName);
        StringBuilder nameContentBuilder = new StringBuilder();
        Map<Integer, UnqualifiedNameEntry> unqualifiedNameSet = tempQualifiedName.getUnqualifiedNameEntries();
        int setSize = unqualifiedNameSet.size();
        for (int counter = 0; counter < setSize; counter++) {
            UnqualifiedName currentUnqualifiedName = unqualifiedNameSet.get(counter);
            nameContentBuilder.append(currentUnqualifiedName.getValue());
            if (counter < (setSize - 1)) {
                nameContentBuilder.append(".");
            }
        }
        value = nameContentBuilder.toString();
    }

    //
    // Accessor(s)
    //
    public String getValue() {
        return (this.value);
    }

    public void setValue(String tokenContent) {
        this.value = new String(tokenContent);
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
        CommonName commonName = (CommonName) o;
        return (commonName.getValue().contentEquals(this.getValue()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}