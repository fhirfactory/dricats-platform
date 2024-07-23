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
package net.fhirfactory.dricats.internals.model.base;

import net.fhirfactory.dricats.internals.model.base.dataytypes.MediaContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

public class MediaObject extends DistributableObject{
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900131L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageObject.class);

    //
    // Attributes
    //

    private MediaContent mediaContent;
    private String mediaReference;
    private Integer mediaSize;
    private LocalDateTime mediaCreationDate;
    private LocalDateTime mediaUpdateDate;

    //
    // Constructor(s)
    //

    //
    // Bean Methods
    //

    public MediaContent getMediaContent() {
        return mediaContent;
    }

    public void setMediaContent(MediaContent mediaContent) {
        this.mediaContent = mediaContent;
    }

    public String getMediaReference() {
        return mediaReference;
    }

    public void setMediaReference(String mediaReference) {
        this.mediaReference = mediaReference;
    }

    public Integer getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(Integer mediaSize) {
        this.mediaSize = mediaSize;
    }

    public LocalDateTime getMediaCreationDate() {
        return mediaCreationDate;
    }

    public void setMediaCreationDate(LocalDateTime mediaCreationDate) {
        this.mediaCreationDate = mediaCreationDate;
    }

    public LocalDateTime getMediaUpdateDate() {
        return mediaUpdateDate;
    }

    public void setMediaUpdateDate(LocalDateTime mediaUpdateDate) {
        this.mediaUpdateDate = mediaUpdateDate;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MediaObject{");
        sb.append("mediaContent='").append(getMediaContent()).append('\'');
        sb.append(", mediaReference='").append(getMediaReference()).append('\'');
        sb.append(", mediaSize=").append(getMediaSize());
        sb.append(", mediaCreationDate=").append(getMediaCreationDate());
        sb.append(", mediaUpdateDate=").append(getMediaUpdateDate());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id=").append(getId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaObject that = (MediaObject) o;
        return Objects.equals(getMediaContent(), that.getMediaContent()) && Objects.equals(getMediaReference(), that.getMediaReference()) && Objects.equals(getMediaSize(), that.getMediaSize()) && Objects.equals(getMediaCreationDate(), that.getMediaCreationDate()) && Objects.equals(getMediaUpdateDate(), that.getMediaUpdateDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMediaContent(), getMediaReference(), getMediaSize(), getMediaCreationDate(), getMediaUpdateDate());
    }
}
