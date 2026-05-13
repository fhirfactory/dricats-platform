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
package net.fhirfactory.dricats.internals.data;

import jakarta.ws.rs.core.MediaType;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicSubscription;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PayloadSubscription implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PayloadSubscription.class);

    //
    // Attributes
    //

    private List<TopicSubscription> allowableTopicSubscriptions;
    private List<PayloadSecurityStatus> allowableSecurityStatusSet;
    private List<MediaType> allowableMediaTypes;
    private List<ElementReference> allowableSources;

    //
    // Constructor(s)
    //

    public PayloadSubscription() {
        super();
        allowableTopicSubscriptions = new ArrayList<>();
        allowableSecurityStatusSet = new ArrayList<>();
        allowableMediaTypes = new ArrayList<>();
        allowableSources = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    public List<TopicSubscription> getAllowableTopicSubscriptions() {
        return allowableTopicSubscriptions;
    }

    public void setAllowableTopicSubscriptions(List<TopicSubscription> allowableTopicSubscriptions) {
        this.allowableTopicSubscriptions = allowableTopicSubscriptions;
    }

    public List<PayloadSecurityStatus> getAllowableSecurityStatusSet() {
        return allowableSecurityStatusSet;
    }

    public void setAllowableSecurityStatusSet(List<PayloadSecurityStatus> allowableSecurityStatusSet) {
        this.allowableSecurityStatusSet = allowableSecurityStatusSet;
    }

    public List<MediaType> getAllowableMediaTypes() {
        return allowableMediaTypes;
    }

    public void setAllowableMediaTypes(List<MediaType> allowableMediaTypes) {
        this.allowableMediaTypes = allowableMediaTypes;
    }

    public List<ElementReference> getAllowableSources() {
        return allowableSources;
    }

    public void setAllowableSources(List<ElementReference> allowableSources) {
        this.allowableSources = allowableSources;
    }

    protected Logger getLogger(){
        return(LOG);
    }

    //
    // Business Methods
    //

    public boolean filterTopic(Topic testTopic) {
        getLogger().debug(".filterTopic(): Entry, testTopic -> {}", testTopic);
        getLogger().debug(".filterTopic(): [Applying Filter] Start");
        for(TopicSubscription currentTopicSubscription : getAllowableTopicSubscriptions()){
            boolean filterPass = currentTopicSubscription.filterTopic(testTopic);
            if(filterPass){
                getLogger().debug(".filterTopic(): [Applying Filter] Pass");
                getLogger().debug(".filterTopic(): Exit, returning --> true");
                return(true);
            }
        }
        getLogger().debug(".filterTopic(): [Applying Filter] Fail");
        getLogger().debug(".filterTopic(): Exit, returning --> false");
        return(false);
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", PayloadSubscription.class.getSimpleName() + "[", "]")
                .add("allowableTopicSubscriptions=" + getAllowableTopicSubscriptions())
                .add("allowableSecurityStatusSet=" + getAllowableSecurityStatusSet())
                .add("allowableMediaTypes=" + getAllowableMediaTypes())
                .add("allowableSources=" + getAllowableSources())
                .toString();
    }
}
