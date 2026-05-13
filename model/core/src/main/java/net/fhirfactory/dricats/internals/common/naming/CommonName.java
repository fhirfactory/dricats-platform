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
package net.fhirfactory.dricats.internals.common.naming;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.fhirfactory.dricats.internals.common.naming.common.DotSeparatedName;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CommonName extends DotSeparatedName implements Serializable {
    //
    // housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Constructor(s)
    //

    public CommonName() {
        super();
    }

    @JsonCreator
    public CommonName(String value) {
        super(value);
    }

    public CommonName(CommonName ori) {
        super(ori);
    }

    public CommonName(DistinguishedName qualifiedName) {
        StringBuilder nameBuilder = new StringBuilder();
        Map<Integer, DistinguishedNameEntry> unqualifiedNameSet = qualifiedName.getUnqualifiedNameEntries();
        int setSize = unqualifiedNameSet.size();
        for (int counter = 0; counter < setSize; counter++) {
            RelativeDistinguishedName currentUnqualifiedName = unqualifiedNameSet.get(counter);
            nameBuilder.append(currentUnqualifiedName.getValue());
            if (counter < (setSize - 1)) {
                nameBuilder.append(DEFAULT_SEPARATOR);
            }
        }
        setName(nameBuilder.toString());
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", getName())
                .append("nameMap", getNameMap())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}