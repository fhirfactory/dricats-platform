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
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.common.id.ObjectToken;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * @author Mark A. Hunter (ACT Health)
 * @since 01-June-2020
 * <p>
 * Note that the LOG'ing level within this class is set to TRACE only, as
 * this set of activities should only be logged if we are really trying to
 * "dig amongst the weeds"!!!
 */
public class FullyDistinguishedName implements Serializable, Comparable<FullyDistinguishedName> {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(FullyDistinguishedName.class);

    //
    // Attributes
    //
    private static final String RDN_TO_STRING_ENTRY_SEPERATOR = ".";
    private static final String FDN_TO_STRING_PREFIX = "QualifiedName(";
    private static final String FDN_TO_STRING_SUFFIX = ")";
    private static final String FDN_TOKEN_ID = "QualifiedNameToken";
    private Map<Integer, DistinguishedNameEntry> unqualifiedNameSet;
    private EffectiveDate effectivePeriod;

    /**
     * Default Constructor
     */
    public FullyDistinguishedName() {
        getLogger().trace(".QualifiedName(): Default constructor invoked.");
        this.unqualifiedNameSet = new HashMap<>();
        this.effectivePeriod = new EffectiveDate();
        getLogger().trace(".QualifiedName(): this.rdnElementSet initialised.");
    }

    /**
     * The Copy Constructor: It creates a duplicate of the original FDN
     * (instantiating new containing elements).
     *
     * @param originalQualifiedName The original FDN
     */
    public FullyDistinguishedName(FullyDistinguishedName originalQualifiedName) {
        getLogger().trace(".QualifiedName( QualifiedName originalQualifiedName ): Constructor invoked, originalQualifiedName --> {}", originalQualifiedName);
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
        this.effectivePeriod = new EffectiveDate();
    }

    public FullyDistinguishedName(CommonQualifier qualifier, CommonName name) {
        this.unqualifiedNameSet = new HashMap<>();
        String[] qualifierValues = qualifier.getValue().split("\\.");
        String[] nameValues = name.getValue().split("\\.");
        for (int counter = 0; counter < qualifierValues.length; counter++) {
            DistinguishedNameEntry currentEntry = new DistinguishedNameEntry(qualifierValues[counter], nameValues[counter]);
            this.unqualifiedNameSet.put(counter, currentEntry);
        }
        this.effectivePeriod = new EffectiveDate();
    }

    /**
     * This constructor uses an FDNToken to construct a new FDN.
     *
     * @param token An FDNToken from which the FDN may be instantiated.
     */
    public FullyDistinguishedName(ObjectToken token) {
        getLogger().trace(".QualifiedName( QualifiedNameToken token ): Constructor invoked, token --> {}", token);
        if (token == null) {
            throw (new IllegalArgumentException("Empty parameter passed to Constructor"));
        }
        String tokenContent = token.getToken();
        getLogger().trace(".QualifiedName( FDNToken token ): tokenContent --> {}", tokenContent);
        String[] rdnStringEntries = tokenContent.split("><");
        if (rdnStringEntries.length <= 0) {
            throw (new IllegalArgumentException("Badly formed FDNToken passed to Constructor, cannot parse -> " + token.getToken()));
        }
        getLogger().trace(".QualifiedName(FDNToken token): We have a valid JSONObject for the FDNToken!, now extract content & process");
        this.unqualifiedNameSet = new HashMap<Integer, DistinguishedNameEntry>();
        for (int counter = 0; counter < rdnStringEntries.length; counter++) {
            getLogger().trace(".QualifiedName( FDNToken token ): Iterating through the extracted Token, attempting to extract RDN[{}]", counter);
            String currentCounterEntry = null;
            for (int loopCounter = 0; loopCounter < rdnStringEntries.length; loopCounter += 1) {
                if (rdnStringEntries[loopCounter].startsWith("<" + counter + ":")) {
                    currentCounterEntry = rdnStringEntries[loopCounter];
                    break;
                }
                if (currentCounterEntry == null) {
                    if (rdnStringEntries[loopCounter].startsWith(counter + ":")) {
                        currentCounterEntry = rdnStringEntries[loopCounter];
                        break;
                    }
                }
            }
            getLogger().trace(".QualifiedName( FDNToken token ): processing ->{}", currentCounterEntry);
            // Extract the RDN Type/Qualifier
            String rdnQualifierWorking = null;
            if (currentCounterEntry.startsWith("<" + counter + ":")) {
                rdnQualifierWorking = currentCounterEntry.replace("<" + counter + ":", "");
            } else if (currentCounterEntry.startsWith(counter + ":")) {
                rdnQualifierWorking = currentCounterEntry.replace(counter + ":", "");
            }
            int rdnQualifierEnd = rdnQualifierWorking.indexOf(">");
            String rdnQualifier = rdnQualifierWorking.substring(0, rdnQualifierEnd);
            // Extract the RDN Value
            String rdnValueWorking = null;
            if (currentCounterEntry.startsWith("<")) {
                rdnValueWorking = currentCounterEntry.substring(1, currentCounterEntry.length() - 1);
            } else {
                rdnValueWorking = currentCounterEntry;
            }
            int startPoint = rdnValueWorking.indexOf(">");
            int endPoint = rdnValueWorking.indexOf("<");
            String rdnValue = rdnValueWorking.substring(startPoint + 1, endPoint);
            getLogger().trace(".QualifiedName( FDNToken token ): creating RDN, rdnQualifier->{}, rdnValue->{}", rdnQualifier, rdnValue);
            DistinguishedNameEntry currentUnqualifiedName = new DistinguishedNameEntry(rdnQualifier, rdnValue);
            getLogger().trace(".QualifiedName( FDNToken token ): Iterating through the extracted RDNs, current RDN --> {}", currentUnqualifiedName);
            this.unqualifiedNameSet.put(counter, currentUnqualifiedName);
        }
    }

    /**
     * Compose the token string from the provided QualifiedName.
     */
    public String toObjectTokenString() {
        ObjectToken objectToken = toObjectToken();
        return(objectToken.getToken());
    }

    public ObjectToken toObjectToken(){
        ObjectToken objectToken = NamingUtilities.toObjectToken(this);
        return(objectToken);
    }

    @JsonIgnore
    protected Logger getLogger() {
        return (LOG);
    }

    /**
     * This method appends an RDN (Relative Distinguished Name) to an existing
     * FDN. This makes the RDN the "Least Significant" member.
     *
     * @param toBeAddedUnqualifiedName An RDN that should be appended (injected as the
     *                     "Least Significant" member of the FDN.
     */
    @JsonIgnore
    public FullyDistinguishedName appendUnqualifiedName(RelativeDistinguishedName toBeAddedUnqualifiedName) {
        getLogger().trace(".appendUnqualifiedName(): Entry, toBeAddedUnqualifiedName --> {}", toBeAddedUnqualifiedName);
        if (toBeAddedUnqualifiedName == null) {
            throw (new IllegalArgumentException("Empty UnqualifiedName passed to appendUnqualifiedName"));
        }
        DistinguishedNameEntry newUnqualifiedName = new DistinguishedNameEntry(toBeAddedUnqualifiedName);
        int existingSetSize = this.getRelativeDNCount();
        newUnqualifiedName.setSequenceNumber(existingSetSize);
        this.unqualifiedNameSet.put(existingSetSize, newUnqualifiedName);
        getLogger().trace(".appendUnqualifiedName(): Exit, updated QualifiedName -> {}", this);
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
    public FullyDistinguishedName getParentQualifiedName() {
        getLogger().trace(".getParentQualifiedName(): Entry");
        if (this.getRelativeDNCount() <= 1) {
            return null;
        }
        FullyDistinguishedName newParentQualifiedName = new FullyDistinguishedName();
        for (int counter = 0; counter <= (this.getRelativeDNCount() - 2); counter++) {
            RelativeDistinguishedName currentUnqualifiedName = this.unqualifiedNameSet.get(counter);
            newParentQualifiedName.appendUnqualifiedName(currentUnqualifiedName);
        }
        getLogger().trace(".getParentQualifiedName(): Exit");
        return (newParentQualifiedName);
    }

    @JsonIgnore
    public RelativeDistinguishedName getUnqualifiedName() {
        getLogger().trace(".getUnqualifiedName(): Entry");
        if (this.getRelativeDNCount() <= 0) {
            getLogger().trace(".getUnqualifiedName(): Exit, no RDNs");
            return (null);
        }
        RelativeDistinguishedName leastSignificantUnqualifiedName = this.unqualifiedNameSet.get((this.getRelativeDNCount() - 1));
        getLogger().trace(".getUnqualifiedName(): Exit, least significant UnqualifiedName --> {}", leastSignificantUnqualifiedName);
        return (leastSignificantUnqualifiedName);
    }

    @JsonIgnore
    public FullyDistinguishedName extractQualifiedNameForQualifier(String qualifier){
        getLogger().trace(".getQualifiedNameForQualifier(): Entry, qualifier --> {}", qualifier);
        FullyDistinguishedName foundName = new FullyDistinguishedName();
        for(Integer counter = 0; counter < this.unqualifiedNameSet.size(); counter++){
            RelativeDistinguishedName currentUnqualifiedName = this.unqualifiedNameSet.get(counter);
            foundName.appendUnqualifiedName(currentUnqualifiedName);
             if(currentUnqualifiedName.getQualifier().contentEquals(qualifier)){
                 getLogger().trace(".getQualifiedNameForQualifier(): Exit, foundName --> {}", foundName);
                 return foundName;
             }
        }
        getLogger().trace(".getQualifiedNameForQualifier(): Exit, no match found");
        return null;
    }

    @JsonIgnore
    public boolean isEmpty() {
        getLogger().trace(".isEmpty(): Entry");
        if (this.getRelativeDNCount() <= 0) {
            getLogger().trace(".isEmpty(): Exit, returned --> true");
            return (true);
        } else {
            getLogger().trace(".isEmpty(): Exit, returned --> false");
            return (false);
        }
    }

    public Map<Integer, DistinguishedNameEntry> getUnqualifiedNameEntries() {
        getLogger().trace(".getUnqualifiedNameEntries(): Entry/Exit");
        return (this.unqualifiedNameSet);
    }


    public void setUnqualifiedNameEntries(Map<Integer, DistinguishedNameEntry> rdnSet) {
        this.unqualifiedNameSet = rdnSet;
    }

    @JsonIgnore
    public int getRelativeDNCount() {
        getLogger().trace(".getRDNCount(): Entry/Exit");
        return (this.unqualifiedNameSet.size());
    }

    @JsonIgnore
    public CommonName getCommonName() {
        getLogger().trace(".getCommonName(): Entry");
        CommonName commonName = new CommonName(this);
        getLogger().trace(".getCommonName(): Exit, commonName->{}", commonName);
        return (commonName);
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
    public String getCommonNameValue() {
        getLogger().trace(".getCommonNameValue(): Entry");
        String id = new CommonName(this).getValue();
        getLogger().trace(".generateUnqualifiedToken(): Exit, Id --> {}", id);
        return (id);
    }

    @JsonIgnore
    public void appendQualifiedName(FullyDistinguishedName additionalQualifiedName) {
        getLogger().trace(".appendFDN(): Entry, additionalFDN --> {}", additionalQualifiedName);
        if (additionalQualifiedName == null) {
            getLogger().trace(".appendFDN(): Exit, nothing to add, additionFDN is null");
            return;
        }
        int additionalFDNSize = additionalQualifiedName.getRelativeDNCount();
        Map<Integer, DistinguishedNameEntry> additionalUnqualifiedNameSet = additionalQualifiedName.getUnqualifiedNameEntries();
        for (int counter = 0; counter < additionalFDNSize; counter++) {
            this.appendUnqualifiedName(additionalUnqualifiedNameSet.get(counter));
        }
        getLogger().trace(".appendFDN: Exit");
    }

    @JsonIgnore
    public RelativeDistinguishedName extractUnqualifiedNameWithQualifier(String qualifier) {
        getLogger().trace(".extractUnqualifiedNameWithQualifier(): Entry, qualifier --> {}", qualifier);
        for (RelativeDistinguishedName currentUnqualifiedName : this.unqualifiedNameSet.values()) {
            boolean matches = currentUnqualifiedName.getQualifier().contentEquals(qualifier);
            if (matches) {
                getLogger().trace(".extractUnqualifiedNameWithQualifier(): Exit, unqualifiedName --> {}", currentUnqualifiedName);
                return (currentUnqualifiedName);
            }
        }
        getLogger().trace(".extractUnqualifiedNameWithQualifier(): Exit, no match found");
        return (null);
    }

    //
    // Utility Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullyDistinguishedName qualifiedName = (FullyDistinguishedName) o;
        String thisUnqualifiedName = this.getCommonName().getValue();
        String otherUnqualifiedName = qualifiedName.getCommonName().getValue();
        boolean equalityTest = thisUnqualifiedName.contentEquals(otherUnqualifiedName);
        return (equalityTest);
    }

    @Override
    public int hashCode() {
        getLogger().trace(".hashCode(): Entry");
        int hashCode = 0;
        for(DistinguishedNameEntry currentDistinguishedNameEntry : this.unqualifiedNameSet.values()){
            getLogger().trace(".hashCode(): currentUnqualifiedNameEntry --> {}", currentDistinguishedNameEntry);
            hashCode = 31 * currentDistinguishedNameEntry.hashCode();
            getLogger().trace(".hashCode(): currentUnqualifiedNameEntry.hashCode() --> {}", currentDistinguishedNameEntry.hashCode());
        }
        getLogger().trace(".hashCode(): Exit, hashCode --> {}", hashCode);
        return hashCode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FullyDistinguishedName.class.getSimpleName() + "[", "]")
                .add("unqualifiedNameSet=" + unqualifiedNameSet)
                .toString();
    }

    //
    // Comparable Interface
    //
    @Override
    public int compareTo(FullyDistinguishedName o) {
        if(o == null){
            return -1;
        }
        if(this == o){
            return 0;
        }
        String token1 = this.toObjectTokenString();
        String token2 = o.toObjectTokenString();
        return token1.compareTo(token2);
    }
}
