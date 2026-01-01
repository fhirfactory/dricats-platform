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

import net.fhirfactory.dricats.internals.common.object.SerialisableObject;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Topic extends SerialisableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900003L;
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Topic.class);

    //
    // Attributes
    //
    private FullyDistinguishedName topicName;
    private String topicDescription;

    //
    // Constructor(s)
    //

    public Topic() {
        super();
    }

    public Topic(FullyDistinguishedName topicName){
        setObjectId(new ObjectId(topicName));
        topicDescription = "No description available";
        setTopicName(topicName);
    }

    public Topic(FullyDistinguishedName topicName, String description){
        setObjectId(new ObjectId(topicName));
        topicDescription = description;
        setTopicName(topicName);
    }

    //
    // Getters and Setters
    //

    public FullyDistinguishedName getTopicName() {
        return topicName;
    }

    public void setTopicName(FullyDistinguishedName topicName) {
        this.topicName = topicName;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("topicName", topicName)
                .append("topicDescription", topicDescription)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(topicName, topic.topicName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(topicName);
    }
}
