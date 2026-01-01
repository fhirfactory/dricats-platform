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
package net.fhirfactory.dricats.internals.pubsub.messages;

import net.fhirfactory.dricats.internals.pubsub.common.FilterBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MessageFilter extends FilterBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private MessageFilterMask messageFilterMask;

    //
    // Constructor(s)
    //

    public MessageFilter(){
        super();
    }

    public MessageFilter(MessageFilterMask messageFilterMask){
        super();
        this.messageFilterMask = messageFilterMask;
    }

    //
     // Getters and Setters
    //

    public MessageFilterMask getMessageFilterMask() {
        return messageFilterMask;
    }

    public void setMessageFilterMask(MessageFilterMask messageFilterMask) {
        this.messageFilterMask = messageFilterMask;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("messageFilterMask", getMessageFilterMask())
                .append("publisherFunction", getPublisherFunction())
                .append("publisherInstance", getPublisherInstance())
                .append("identifiers", getIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getObjectId())
                .toString();
    }
}
