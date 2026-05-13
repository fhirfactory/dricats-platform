/*
 * Copyright (c) 2020 Mark A. Hunter
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * @author Mark A. Hunter (ACT Health)
 * @since 01-June-2020
 * <p>
 */
public class DistinguishedName implements Serializable, Comparable<DistinguishedName> {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private static final String RDN_TO_STRING_ENTRY_SEPARATOR = ".";
    private Map<Integer, DistinguishedNameEntry> unqualifiedNameSet;

    /**
     * Default Constructor
     */
    public DistinguishedName() {
        this.unqualifiedNameSet = new HashMap<>();
    }

    /**
     * The Copy Constructor: It creates a duplicate of the original FDN
     * (instantiating new containing elements).
     *
     * @param originalQualifiedName The original FDN
     */
    public DistinguishedName(DistinguishedName originalQualifiedName) {
        if (originalQualifiedName == null) {
            throw (new IllegalArgumentException("Empty originalQualifiedName passed to copy Constructor"));
        }
        // Essentially, we iterate through the HashMap from the OriginalFDN,
        // create new RDN's from the content, and append these RDN's (in the
        // appropriate order) into the new FDN.
        this.unqualifiedNameSet = new HashMap<>();
        Map<Integer, DistinguishedNameEntry> otherUnqualifiedNameSet = originalQualifiedName.getUnqualifiedNameEntries();
        if (otherUnqualifiedNameSet.size() != originalQualifiedName.getRelativeDNCount()) {
            throw (new IllegalArgumentException("Malformed originalQualifiedName passed to copy Constructor"));
        }
        for (int counter = 0; counter < originalQualifiedName.getRelativeDNCount(); counter++) {
            DistinguishedNameEntry currentUnqualifiedName = otherUnqualifiedNameSet.get(counter);
            DistinguishedNameEntry clonedUnqualifiedName = SerializationUtils.clone(currentUnqualifiedName);
            this.unqualifiedNameSet.put(counter, clonedUnqualifiedName);
        }
    }

    public DistinguishedName(CommonQualifier qualifier, CommonName name) {
        this.unqualifiedNameSet = new HashMap<>();
        if(qualifier.getNameMap().size() != name.getNameMap().size()){
            throw new IllegalArgumentException("Malformed qualifier/name passed to Constructor");
        }
        for (int counter = 0; counter < qualifier.getNameMap().size(); counter++) {
            DistinguishedNameEntry currentEntry = new DistinguishedNameEntry(qualifier.getNameMap().get(counter), name.getNameMap().get(counter));
            this.unqualifiedNameSet.put(counter, currentEntry);
        }
    }

    /**
     * This method appends an RDN (Relative Distinguished Name) to an existing
     * FDN. This makes the RDN the "Least Significant" member.
     *
     * @param toBeAddedUnqualifiedName An RDN that should be appended (injected as the
     *                     "Least Significant" member of the FDN.
     */
    @JsonIgnore
    public DistinguishedName appendUnqualifiedName(RelativeDistinguishedName toBeAddedUnqualifiedName) {
        if (toBeAddedUnqualifiedName == null) {
            throw (new IllegalArgumentException("Empty UnqualifiedName passed to appendUnqualifiedName"));
        }
        DistinguishedNameEntry newUnqualifiedName = new DistinguishedNameEntry(toBeAddedUnqualifiedName);
        int existingSetSize = this.getRelativeDNCount();
        newUnqualifiedName.setSequenceNumber(existingSetSize);
        this.unqualifiedNameSet.put(existingSetSize, newUnqualifiedName);
        return(this);
    }

    /**
     * FDNs are used to support hierarchical/containment models - where a Parent
     * FDN it total contained within the child FDN. For exmaple, for the
     * following FDN: FDN = (Campus=CHS).(Building=Bulding10),(Floor=3)
     * <p>
     * then the Campus element is the Parent of the Building element. Note that
     * the Floor=? element is the "Least Significant" element.
     *
     * @return Returns the "Parent" FDN of this FDN. The "Parent" FDN is one
     * that has the current "Least Significant" member removed from it.
     */
    @JsonIgnore
    public DistinguishedName getParentQualifiedName() {
        if (this.getRelativeDNCount() <= 1) {
            return null;
        }
        DistinguishedName newParentQualifiedName = new DistinguishedName();
        for (int counter = 0; counter <= (this.getRelativeDNCount() - 2); counter++) {
            RelativeDistinguishedName currentUnqualifiedName = this.unqualifiedNameSet.get(counter);
            newParentQualifiedName.appendUnqualifiedName(currentUnqualifiedName);
        }
        return (newParentQualifiedName);
    }

    @JsonIgnore
    public RelativeDistinguishedName getUnqualifiedName() {
        if (this.getRelativeDNCount() <= 0) {
            return (null);
        }
        RelativeDistinguishedName leastSignificantUnqualifiedName = this.unqualifiedNameSet.get((this.getRelativeDNCount() - 1));
        return (leastSignificantUnqualifiedName);
    }

    @JsonIgnore
    public DistinguishedName extractQualifiedNameForQualifier(String qualifier){
        DistinguishedName foundName = new DistinguishedName();
        for(Integer counter = 0; counter < this.unqualifiedNameSet.size(); counter++){
            RelativeDistinguishedName currentUnqualifiedName = this.unqualifiedNameSet.get(counter);
            foundName.appendUnqualifiedName(currentUnqualifiedName);
             if(currentUnqualifiedName.getQualifier().contentEquals(qualifier)){
                 return foundName;
             }
        }
        return null;
    }

    @JsonIgnore
    public boolean isEmpty() {
        if (this.getRelativeDNCount() <= 0) {
            return (true);
        } else {
            return (false);
        }
    }

    public Map<Integer, DistinguishedNameEntry> getUnqualifiedNameEntries() {
        return (this.unqualifiedNameSet);
    }


    public void setUnqualifiedNameEntries(Map<Integer, DistinguishedNameEntry> rdnSet) {
        this.unqualifiedNameSet = rdnSet;
    }

    @JsonIgnore
    public int getRelativeDNCount() {
        return (this.unqualifiedNameSet.size());
    }

    @JsonIgnore
    public CommonName getCommonName() {
        CommonName commonName = new CommonName(this);
        return (commonName);
    }

    @JsonIgnore
    public CommonQualifier getCommonQualifier() {
        CommonQualifier commonQualifier = new CommonQualifier(this);
        return (commonQualifier);
    }

    @JsonIgnore
    private String pseudoXMLAttribute(int order, String attributeName, String attributeValue) {
        StringBuilder xmlAttributeBuilder = new StringBuilder();
        xmlAttributeBuilder.append("<");
        xmlAttributeBuilder.append(order);
        xmlAttributeBuilder.append(":");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        xmlAttributeBuilder.append(attributeValue);
        xmlAttributeBuilder.append("</");
        xmlAttributeBuilder.append(order);
        xmlAttributeBuilder.append(":");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        return (xmlAttributeBuilder.toString());
    }

    @JsonIgnore
    public void appendQualifiedName(DistinguishedName additionalQualifiedName) {
        if (additionalQualifiedName == null) {
            return;
        }
        int additionalFDNSize = additionalQualifiedName.getRelativeDNCount();
        Map<Integer, DistinguishedNameEntry> additionalUnqualifiedNameSet = additionalQualifiedName.getUnqualifiedNameEntries();
        for (int counter = 0; counter < additionalFDNSize; counter++) {
            this.appendUnqualifiedName(additionalUnqualifiedNameSet.get(counter));
        }
    }

    @JsonIgnore
    public RelativeDistinguishedName extractUnqualifiedNameWithQualifier(String qualifier) {
        for (RelativeDistinguishedName currentUnqualifiedName : this.unqualifiedNameSet.values()) {
            boolean matches = currentUnqualifiedName.getQualifier().contentEquals(qualifier);
            if (matches) {
                return (currentUnqualifiedName);
            }
        }
        return (null);
    }

    @JsonIgnore
    public String getTokenString() {
        CommonName commonName = getCommonName();
        CommonQualifier commonQualifier = getCommonQualifier();
        String token = commonQualifier.getName() + "=" + commonName.getName();
        return (token);
    }

    //
    // Utility Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistinguishedName qualifiedName = (DistinguishedName) o;
        String thisName = this.getCommonName().getName();
        String otherName = qualifiedName.getCommonName().getName();
        String thisQualifier = this.getCommonQualifier().getName();
        String otherQualifier = qualifiedName.getCommonQualifier().getName();
        boolean equalityTest = thisName.equals(otherName) && thisQualifier.equals(otherQualifier);
        return (equalityTest);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for(DistinguishedNameEntry currentDistinguishedNameEntry : this.unqualifiedNameSet.values()){
            hashCode = 31 * currentDistinguishedNameEntry.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DistinguishedName.class.getSimpleName() + "[", "]")
                .add("unqualifiedNameSet=" + unqualifiedNameSet)
                .toString();
    }

    //
    // Comparable Interface
    //
    @Override
    public int compareTo(DistinguishedName o) {
        if(o == null){
            return -1;
        }
        if(this == o){
            return 0;
        }
        String token1 = this.getCommonQualifier().getName() + "=" + this.getCommonName().getName();
        String token2 = o.getCommonQualifier().getName() + "=" + o.getCommonName().getName();
        return token1.compareTo(token2);
    }
}
