/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.internals.model.base.dataytypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Base64;
import java.util.Objects;

public class MediaContent implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900006L;

    //
    // Attributes
    //

    private Base64 mediaPayload;
    private Long mediaPayloadChecksum;

    //
    // Constructor(s)
    //


    //
    // Bean Methods
    //

    public Base64 getMediaPayload() {
        return mediaPayload;
    }

    public void setMediaPayload(Base64 mediaPayload) {
        this.mediaPayload = mediaPayload;
    }

    public Long getMediaPayloadChecksum() {
        return mediaPayloadChecksum;
    }

    public void setMediaPayloadChecksum(Long mediaPayloadChecksum) {
        this.mediaPayloadChecksum = mediaPayloadChecksum;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MediaContent{");
        sb.append("mediaPayload=").append(getMediaPayload());
        sb.append(", mediaPayloadChecksum=").append(getMediaPayloadChecksum());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaContent that = (MediaContent) o;
        return Objects.equals(getMediaPayload(), that.getMediaPayload()) && Objects.equals(getMediaPayloadChecksum(), that.getMediaPayloadChecksum());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMediaPayload(), getMediaPayloadChecksum());
    }
}