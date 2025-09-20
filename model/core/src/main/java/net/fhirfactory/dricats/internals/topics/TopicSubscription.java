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
package net.fhirfactory.dricats.internals.topics;

import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class TopicSubscription implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TopicSubscription.class);

    //
    // Attributes
    //
    private QualifiedName subscriptionMask;
    private Boolean includeSubtopics;

    //
    // Constructor(s)
    //
    public TopicSubscription() {
        super();
    }

    public TopicSubscription(QualifiedName subscriptionMask) {
        this.subscriptionMask = subscriptionMask;
        this.includeSubtopics = false;
    }

    public TopicSubscription(QualifiedName subscriptionMask, Boolean includeSubtopics) {
        this.subscriptionMask = subscriptionMask;
        this.includeSubtopics = includeSubtopics;
    }

    //
    // Getters and Setters
    //
    public QualifiedName getSubscriptionMask() {
        return subscriptionMask;
    }

    public void setSubscriptionMask(QualifiedName subscriptionMask) {
        this.subscriptionMask = subscriptionMask;
    }

    public Boolean getIncludeSubtopics() {
        return includeSubtopics;
    }

    public void setIncludeSubtopics(Boolean includeSubtopics) {
        this.includeSubtopics = includeSubtopics;
    }

    protected Logger getLogger(){
        return LOG;
    }

    //
    // Business Methods
    //
    /**
     * Determines if the given Topic matches this subscription pattern.
     * The subscription is defined by a QualifiedName where each UnqualifiedName value
     * may contain '*' wildcards. '*' matches any sequence of characters (including empty).
     * Qualifiers must match exactly (or against wildcard). The number/order of entries must be equal
     * unless the includeSubtopics flag is set to true - in which case the number of entries in the
     * test topic can be greater than the number of entries in the subscription mask.
     */
    public boolean filterTopic(Topic testTopic){
        getLogger().debug(".filterTopic(): Entry, testTopic -> {}", testTopic);
        if(testTopic == null){
            return false;
        }
        if(getSubscriptionMask() == null){
            // if no restriction set, do not match anything by default
            return false;
        }
        QualifiedName testQN = testTopic.getTopicName();
        QualifiedName patternQN = getSubscriptionMask();
        if(testQN == null){
            getLogger().debug(".filterTopic(): Exit, testTopic contains null QualifiedName, returning false");
            return false;
        }
        // Require same number of UnqualifiedName parts or allows subtopics
        boolean sameNumberOfParts = (testQN.getRelativeDNCount() == patternQN.getRelativeDNCount());
        boolean allowsubtopics = getIncludeSubtopics() && (testQN.getRelativeDNCount() >= patternQN.getRelativeDNCount());
        if(!(sameNumberOfParts && allowsubtopics)){
            getLogger().debug(".filterTopic(): Exit, testTopic does contain same number of entries as subscription mask, returning false");
            return false;
        }

        Map<Integer, UnqualifiedNameEntry> testEntries = testQN.getUnqualifiedNameEntries();
        Map<Integer, UnqualifiedNameEntry> patternEntries = patternQN.getUnqualifiedNameEntries();
        for(int i=0; i<patternQN.getRelativeDNCount(); i++){
            UnqualifiedNameEntry p = patternEntries.get(i);
            UnqualifiedNameEntry t = testEntries.get(i);
            if(p == null || t == null){
                getLogger().debug(".filterTopic(): Exit, testTopic contains null UnqualifiedNameEntry, returning false");
                return false;
            }
            // Qualifier must match exactly
            if(!Objects.equals(p.getQualifier(), t.getQualifier())){
                getLogger().debug(".filterTopic(): Exit, testTopic contains different qualifier than subscription mask, returning false");
                return false;
            }
            String patternValue = p.getValue();
            String testValue = t.getValue();
            if(patternValue == null){
                if(testValue != null){
                    getLogger().debug(".filterTopic(): Exit, testTopic contains null value for qualifier, returning false");
                    return false;
                }
            } else {
                if(patternValue.contains("*")){
                    if(!wildcardMatch(patternValue, testValue)){
                        getLogger().debug(".filterTopic(): Exit, testTopic contains value that does not match wildcard, returning false");
                        return false;
                    }
                } else {
                    if(!Objects.equals(patternValue, testValue)){
                        getLogger().debug(".filterTopic(): Exit, testTopic contains value that does not match, returning false");
                        return false;
                    }
                }
            }
        }
        getLogger().debug(".filterTopic(): Exit, returning true");
        return true;
    }

    private boolean wildcardMatch(String pattern, String value){
        getLogger().debug(".wildcardMatch(): Entry, pattern -> {}, value -> {}", pattern, value);
        if(value == null){
            getLogger().debug(".wildcardMatch(): Exit, value == null, returning false");
            return false;
        }
        // Escape regex meta characters except '*'
        StringBuilder sb = new StringBuilder();
        for(char c : pattern.toCharArray()){
            if(c == '*'){
                sb.append(".*");
            } else if(".[]{}()\\+^$|?".indexOf(c) >= 0){
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        String regex = "^" + sb + "$";
        getLogger().debug(".wildcardMatch(): Exit, returning true");
        return value.matches(regex);
    }

    //
    // Standard Methods
    //
    @Override
    public String toString() {
        return("TopicSubscription{" +
                "subscriptionMask=" + subscriptionMask +
                ", includeSubtopics=" + includeSubtopics +
                '}');
    }

}
