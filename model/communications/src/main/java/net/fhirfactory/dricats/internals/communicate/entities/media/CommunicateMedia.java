/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.communicate.entities.media;

import java.io.Serial;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import net.fhirfactory.dricats.internals.communicate.entities.media.datatypes.CommunicateMediaDetail;
import net.fhirfactory.dricats.internals.data.MediaObject;

public class CommunicateMedia extends MediaObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900147L;

    //
    // Attributes
    //

    private CommunicateMediaDetail mediaDetails;

    //
    // Constructor(s)
    //

    //
    // Bean Methods
    //

    public CommunicateMediaDetail getMediaDetails() {
        return mediaDetails;
    }

    public void setMediaDetails(CommunicateMediaDetail mediaDetails) {
        this.mediaDetails = mediaDetails;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mediaDetails", getMediaDetails())
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("identifier", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("mediaContent", getMediaContent())
                .append("mediaReference", getMediaReference())
                .append("mediaSize", getMediaSize())
                .append("mediaCreationDate", getMediaCreationDate())
                .append("mediaUpdateDate", getMediaUpdateDate())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommunicateMedia that = (CommunicateMedia) o;
        return Objects.equals(getMediaDetails(), that.getMediaDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMediaDetails());
    }
}
