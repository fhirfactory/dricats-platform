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
package net.fhirfactory.dricats.internals.common;

import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry;
import org.slf4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

abstract public class SubscriptionSupport extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    protected DistributableObjectId subscriber;

    //
    // Getters and Setters
    //
    abstract protected Logger getLogger();

    public DistributableObjectId getSubscriber() {
        return subscriber;
    }
    public void setSubscriber(DistributableObjectId subscriber) {
        this.subscriber = subscriber;
    }

    //
    // Constructor(s)
    //
    public SubscriptionSupport() {
    }

    public SubscriptionSupport(DistributableObjectId subscriber) {
        this.subscriber = subscriber;
    }

    //
    // Business Methods
    //
    protected boolean matchDistributableId(DistributableObjectId pattern, DistributableObjectId value) {
        if (pattern == null) {
            return true;
        }
        if (value == null) {
            return false;
        }
        QualifiedName patternQN = pattern.getQualifiedName();
        QualifiedName valueQN = value.getQualifiedName();
        if (patternQN == null || valueQN == null) {
            return false;
        }
        if (patternQN.getRelativeDNCount() != valueQN.getRelativeDNCount()) {
            return false;
        }
        Map<Integer, UnqualifiedNameEntry> pEntries = patternQN.getUnqualifiedNameEntries();
        Map<Integer, UnqualifiedNameEntry> vEntries = valueQN.getUnqualifiedNameEntries();
        for (int i = 0; i < patternQN.getRelativeDNCount(); i++) {
            UnqualifiedNameEntry p = pEntries.get(i);
            UnqualifiedNameEntry v = vEntries.get(i);
            if (p == null || v == null) {
                return false;
            }
            if (!Objects.equals(p.getQualifier(), v.getQualifier())) {
                return false;
            }
            String pv = p.getValue();
            String vv = v.getValue();
            if (pv == null) {
                if (vv != null) {
                    return false;
                }
            } else if (pv.contains("*")) {
                if (!wildcardMatch(pv, vv)) {
                    return false;
                }
            } else {
                if (!Objects.equals(pv, vv)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean wildcardMatch(String pattern, String value) {
        getLogger().debug(".wildcardMatch(): Entry, pattern -> {}, value -> {}", pattern, value);
        if (value == null) {
            getLogger().debug(".wildcardMatch(): Exit, value == null, returning false");
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : pattern.toCharArray()) {
            if (c == '*') {
                sb.append(".*");
            } else if (".[]{}()\\+^$|?".indexOf(c) >= 0) {
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        String regex = "^" + sb + "$";
        boolean result = value.matches(regex);
        getLogger().debug(".wildcardMatch(): Exit, returning {}", result);
        return result;
    }
}
