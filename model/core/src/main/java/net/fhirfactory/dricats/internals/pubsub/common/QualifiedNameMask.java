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
package net.fhirfactory.dricats.internals.pubsub.common;

import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class QualifiedNameMask implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(QualifiedNameMask.class);

    //
    // Attributes
    //

    private DistinguishedName mask;
    private Boolean includeContained;

    protected Logger getLogger(){
        return LOG;
    }

    //
    // Constructors
    //
    public QualifiedNameMask() {
        this.includeContained = false;
        getLogger().trace(".QualifiedNameMask(): Default constructor invoked.");
    }

    public QualifiedNameMask(DistinguishedName mask) {
        this.mask = mask;
        this.includeContained = false;
        getLogger().trace(".QualifiedNameMask(QualifiedName): Constructor invoked, mask -> {}", mask);
    }

    public QualifiedNameMask(DistinguishedName mask, Boolean includeContained) {
        this.mask = mask;
        this.includeContained = includeContained;
        getLogger().trace(".QualifiedNameMask(QualifiedName, Boolean): Constructor invoked, mask -> {}, includeContained -> {}", mask, includeContained);
    }

    //
    // Getters and Setters
    //
    public DistinguishedName getMask() {
        getLogger().trace(".getMask(): Entry/Exit, returning mask -> {}", mask);
        return mask;
    }

    public void setMask(DistinguishedName mask) {
        getLogger().trace(".setMask(): Entry, mask -> {}", mask);
        this.mask = mask;
        getLogger().trace(".setMask(): Exit");
    }

    public Boolean getIncludeContained() {
        return includeContained;
    }

    public void setIncludeContained(Boolean includeContained) {
        this.includeContained = includeContained;
    }

    //
    // Business Methods
    //
    /**
     * Parses the test QualifiedName and validates that it matches this mask.
     * A match occurs when:
     * - The number and order of UnqualifiedName entries is the same.
     * - Each qualifier matches exactly.
     * - For each value: if the mask value contains a regular expression, the test value must match it;
     *   otherwise, the values must be exactly equal. This method treats the mask value as a regex pattern
     *   for matching via Pattern.matches; simple literals will also match exactly.
     *
     * @param testName the QualifiedName to test
     * @return true if testName matches the mask; false otherwise
     */
    public boolean filter(DistinguishedName testName) {
        getLogger().trace(".filter(QualifiedName): Entry, delegating to filter(testName, false). testName -> {}", testName);
        boolean result = filter(testName, getIncludeContained());
        getLogger().trace(".filter(QualifiedName): Exit, result -> {}", result);
        return result;
    }

    /**
     * Extended filter supporting includeContained.
     * If includeContained is true and the testName has MORE entries than the mask,
     * only the first mask-length entries are compared. If they match, this returns true.
     * Otherwise, the lengths must be equal and all entries must match.
     *
     * @param testName the QualifiedName to test
     * @param includeContained whether to allow testName to contain mask as a prefix (by entries)
     * @return true if matches according to the rules
     */
    public boolean filter(DistinguishedName testName, boolean includeContained) {
        getLogger().trace(".filter(QualifiedName, boolean): Entry, mask -> {}, testName -> {}, includeContained -> {}", mask, testName, includeContained);
        if (mask == null || testName == null) {
            getLogger().trace(".filter(QualifiedName, boolean): Either mask or testName is null (mask == null? {}, testName == null? {}). Returning false.", (mask==null), (testName==null));
            return false;
        }
        int mCount = mask.getRelativeDNCount();
        int tCount = testName.getRelativeDNCount();
        getLogger().trace(".filter(QualifiedName, boolean): mCount -> {}, tCount -> {}", mCount, tCount);

        if (tCount == mCount) {
            getLogger().trace(".filter(QualifiedName, boolean): Counts equal, performing exact comparison up to {} entries.", mCount);
            boolean res = entriesMatchUpTo(mask, testName, mCount);
            getLogger().trace(".filter(QualifiedName, boolean): Exact-length comparison result -> {}", res);
            return res;
        }
        if (includeContained && tCount > mCount) {
            getLogger().trace(".filter(QualifiedName, boolean): includeContained == true and test has more entries; comparing first {} entries.", mCount);
            boolean res = entriesMatchUpTo(mask, testName, mCount);
            getLogger().trace(".filter(QualifiedName, boolean): Prefix comparison result -> {}", res);
            return res;
        }
        getLogger().trace(".filter(QualifiedName, boolean): Lengths incompatible for matching (includeContained == {}), returning false.", includeContained);
        return false;
    }

    private boolean entriesMatchUpTo(DistinguishedName maskQN, DistinguishedName testQN, int count) {
        getLogger().trace(".entriesMatchUpTo(): Entry, count -> {}, maskQN -> {}, testQN -> {}", count, maskQN, testQN);
        Map<Integer, DistinguishedNameEntry> mEntries = maskQN.getUnqualifiedNameEntries();
        Map<Integer, DistinguishedNameEntry> tEntries = testQN.getUnqualifiedNameEntries();
        for (int i = 0; i < count; i++) {
            DistinguishedNameEntry m = mEntries.get(i);
            DistinguishedNameEntry t = tEntries.get(i);
            if (m == null || t == null) {
                getLogger().trace(".entriesMatchUpTo(): At index {}, one of the entries is null (m == null? {}, t == null? {}), returning false.", i, (m==null), (t==null));
                return false;
            }
            if (!Objects.equals(m.getQualifier(), t.getQualifier())) {
                getLogger().trace(".entriesMatchUpTo(): At index {}, qualifier mismatch (mask:'{}' vs test:'{}'), returning false.", i, m.getQualifier(), t.getQualifier());
                return false;
            }
            String pattern = m.getValue();
            String value = t.getValue();
            if (pattern == null) {
                if (value != null) {
                    getLogger().trace(".entriesMatchUpTo(): At index {}, mask value is null but test value is not (test:'{}'), returning false.", i, value);
                    return false;
                }
            } else {
                if (value == null) {
                    getLogger().trace(".entriesMatchUpTo(): At index {}, test value is null but mask pattern is not (pattern:'{}'), returning false.", i, pattern);
                    return false;
                }
                boolean matched = Pattern.matches(pattern, value);
                if (!matched) {
                    getLogger().trace(".entriesMatchUpTo(): At index {}, value does not match pattern (pattern:'{}', value:'{}'), returning false.", i, pattern, value);
                    return false;
                } else {
                    getLogger().trace(".entriesMatchUpTo(): At index {}, value matched pattern (pattern:'{}', value:'{}').", i, pattern, value);
                }
            }
        }
        getLogger().trace(".entriesMatchUpTo(): All {} entries matched, returning true.", count);
        return true;
    }

    public String prettyPrint(){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<mask.getRelativeDNCount(); i++){
            DistinguishedNameEntry currentUnqualifiedName = mask.getUnqualifiedNameEntries().get(i);
            sb.append(currentUnqualifiedName.getQualifier()).append("->").append(currentUnqualifiedName.getValue()).append("\n");
        }
        sb.append("Include Contained: ").append(getIncludeContained());
        return sb.toString();
    }

    //
     // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mask", getMask())
                .append("includeContained", getIncludeContained())
                .toString();
    }
}
