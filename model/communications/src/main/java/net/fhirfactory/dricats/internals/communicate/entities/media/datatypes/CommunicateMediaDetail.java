/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.communicate.entities.media.datatypes;

import net.fhirfactory.dricats.internals.communicate.entities.location.CommunicateLocation;
import net.fhirfactory.dricats.internals.communicate.entities.media.valuesets.CommunicateMediaTypeEnum;
import net.fhirfactory.dricats.internals.communicate.entities.rooms.datatypes.CommunicateRoomReference;
import net.fhirfactory.dricats.internals.communicate.entities.user.datatypes.CommunicateUserReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class CommunicateMediaDetail implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900142L;
    private static final Logger LOG = LoggerFactory.getLogger(CommunicateLocation.class);

    //
    // Attributes
    //

    private URL mediaURL;
    private String friendlyName;
    private HashMap<String, String> mediaMetadata;
    private CommunicateRoomReference sourceRoom;
    private CommunicateUserReference sourceUser;
    private CommunicateMediaTypeEnum mediaType;

    //
    // Constructor(s)
    //

    public CommunicateMediaDetail() {
        mediaMetadata = new HashMap<String, String>();
    }

    //
    // Bean Methods
    //

    public URL getMediaURL() {
        return mediaURL;
    }

    public void setMediaURL(URL mediaURL) {
        this.mediaURL = mediaURL;
    }

    public CommunicateMediaTypeEnum getMediaType() {
        return mediaType;
    }

    public void setMediaType(CommunicateMediaTypeEnum mediaType) {
        this.mediaType = mediaType;
    }

    public CommunicateRoomReference getSourceRoom() {
        return sourceRoom;
    }

    public void setSourceRoom(CommunicateRoomReference sourceRoom) {
        this.sourceRoom = sourceRoom;
    }

    public CommunicateUserReference getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(CommunicateUserReference sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public HashMap<String, String> getMediaMetadata() {
        return mediaMetadata;
    }

    public void setMediaMetadata(HashMap<String, String> mediaMetadata) {
        this.mediaMetadata = mediaMetadata;
    }

    //
    // Utility methods
    //

    protected Logger getLogger() {
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CommunicateMediaDetail{");
        sb.append("mediaURL=").append(getMediaURL());
        sb.append(", friendlyName='").append(getFriendlyName()).append('\'');
        sb.append(", mediaMetadata=").append(getMediaMetadata());
        sb.append(", sourceRoom=").append(getSourceRoom());
        sb.append(", sourceUser=").append(getSourceUser());
        sb.append(", mediaType=").append(getMediaType());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunicateMediaDetail that = (CommunicateMediaDetail) o;
        return Objects.equals(getMediaURL(), that.getMediaURL()) && Objects.equals(getFriendlyName(), that.getFriendlyName()) && Objects.equals(getMediaMetadata(), that.getMediaMetadata()) && Objects.equals(getSourceRoom(), that.getSourceRoom()) && Objects.equals(getSourceUser(), that.getSourceUser()) && getMediaType() == that.getMediaType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMediaURL(), getFriendlyName(), getMediaMetadata(), getSourceRoom(), getSourceUser(), getMediaType());
    }
}
