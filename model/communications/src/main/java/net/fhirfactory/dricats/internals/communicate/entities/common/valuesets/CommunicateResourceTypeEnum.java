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
package net.fhirfactory.dricats.internals.communicate.entities.common.valuesets;

public enum CommunicateResourceTypeEnum {
    COMMUNICATE_TEAM("communicate.resource.team", "CommunicateTeam"),
    COMMUNICATE_GROUP("communicate.resource.group", "CommunicateGroup"),
    COMMUNICATE_LOCATION("communicate.resource.location", "CommunicateLocation"),
    COMMUNICATE_MEDIA("communicate.resource.media", "CommunicateMedia"),
    COMMUNICATE_MESSAGE("communicate.resource.message", "CommunicateMessage"),
    COMMUNICATE_ORGANIZATION("communicate.resource.organization", "CommunicateOrganization"),
    COMMUNICATE_ROOM("communicate.resource.room", "CommunicateRoom"),
    COMMUNICATE_SESSION("communicate.resource.session", "CommunicateSession"),
    COMMUNICATE_USER("communicate.resource.room.code_responder", "CommunicateUser");

    private String resourceTypeID;
    private String resourceName;

    private CommunicateResourceTypeEnum(String resourceTypeID, String resourceTypeName) {
        this.resourceTypeID = resourceTypeID;
        this.resourceName = resourceTypeName;
    }

    public static String getDataParcelDefiner() {
        return ("FHIRFactory");
    }

    public static String getDataParcelCategory() {
        return ("Collaboration");
    }

    public static String getDataParcelSubCategory() {
        return ("Communicate");
    }

    public static CommunicateResourceTypeEnum fromResourceName(String resourceName) {
        for (CommunicateResourceTypeEnum value : CommunicateResourceTypeEnum.values()) {
            if (value.getResourceName().contentEquals(resourceName)) {
                return (value);
            }
        }
        return (null);
    }

    public String getResourceTypeID() {
        return (this.resourceTypeID);
    }

    public String getResourceName() {
        return (resourceName);
    }

}
