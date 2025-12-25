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
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.id.ObjectToken;
import net.fhirfactory.dricats.internals.common.naming.common.DotSeparatedName;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

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

    public CommonName(FullyDistinguishedName qualifiedName) {
        FullyDistinguishedName tempQualifiedName = new FullyDistinguishedName(qualifiedName);
        StringBuilder nameContentBuilder = new StringBuilder();
        Map<Integer, DistinguishedNameEntry> unqualifiedNameSet = tempQualifiedName.getUnqualifiedNameEntries();
        int setSize = unqualifiedNameSet.size();
        for (int counter = 0; counter < setSize; counter++) {
            RelativeDistinguishedName currentUnqualifiedName = unqualifiedNameSet.get(counter);
            nameContentBuilder.append(currentUnqualifiedName.getValue());
            if (counter < (setSize - 1)) {
                nameContentBuilder.append(".");
            }
        }
        setValue(nameContentBuilder.toString());
    }

    public CommonName(ObjectToken token) {
        FullyDistinguishedName tempQualifiedName = new FullyDistinguishedName(token);
        StringBuilder nameContentBuilder = new StringBuilder();
        Map<Integer, DistinguishedNameEntry> unqualifiedNameSet = tempQualifiedName.getUnqualifiedNameEntries();
        int setSize = unqualifiedNameSet.size();
        for (int counter = 0; counter < setSize; counter++) {
            RelativeDistinguishedName currentUnqualifiedName = unqualifiedNameSet.get(counter);
            nameContentBuilder.append(currentUnqualifiedName.getValue());
            if (counter < (setSize - 1)) {
                nameContentBuilder.append(".");
            }
        }
        setValue(nameContentBuilder.toString());
    }

    //
    // Accessor(s)
    //

    //
    // Factory Methods
    //

    public static CommonName fromIdValueString(String idValue){
        if(StringUtils.isEmpty(idValue)){
            return(null);
        }
        String[] idValueComponents = idValue.split(ObjectId.ID_QUALIFIER_NAME_SEPARATOR);
        if(idValueComponents.length < 2){
            return(null);
        }
        CommonName name = new CommonName(idValueComponents[1]);
        return(name);
    }

    //
    // Standard Methods
    //
    @Override
    public String toString() {
        return "CommonName{" +
                "value=" + getValue() +
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