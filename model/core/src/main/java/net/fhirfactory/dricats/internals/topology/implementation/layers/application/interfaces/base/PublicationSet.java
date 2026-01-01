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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base;

import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageFilter;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationFunction;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PublicationSet extends ApplicationFunction implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900050L;

    //
    // Attributes
    //

    private List<ContentFilter> contentFilters;
    private List<MessageFilter> messageFilters;

    //
    // Constructor(s)
    //

    public PublicationSet() {
        super();
        this.contentFilters = new ArrayList<>();
    }

    //
    // Bean Methods
    //

    public List<ContentFilter> getContentFilters() {
        return contentFilters;
    }

    public void setContentFilters(List<ContentFilter> contentFilters) {
        this.contentFilters = contentFilters;
    }

    public List<MessageFilter> getMessageFilters() {
        return messageFilters;
    }
    public void setMessageFilters(List<MessageFilter> messageFilters) {
        this.messageFilters = messageFilters;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("contentFilters", getContentFilters())
                .append("messageFilters", getMessageFilters())
                .append("applicationServices", getApplicationServices())
                .append("applicationDataObjects", getApplicationDataObjects())
                .append("owner", getOwner())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getObjectId())
                .toString();
    }
}
