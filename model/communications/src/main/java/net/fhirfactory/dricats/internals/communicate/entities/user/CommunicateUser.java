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
package net.fhirfactory.dricats.internals.communicate.entities.user;

import java.io.Serial;
import java.util.StringJoiner;

import net.fhirfactory.dricats.resources.base.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.communicate.entities.user.datatypes.CommunicateSurrogateResourceReference;
import net.fhirfactory.dricats.internals.communicate.entities.user.valuesets.CommunicateUserTypeEnum;

public class CommunicateUser extends User {
	//
	// Housekeeping
	//
	
	@Serial
    private static final long serialVersionUID = -8603393243724832479L;
	private static final Logger LOG = LoggerFactory.getLogger(CommunicateUser.class);
	
	//
	// Attributes
	//
	
	private CommunicateSurrogateResourceReference representedResource;
    private boolean surrogate;
    private boolean administrator;
    private boolean deactivated;
    private boolean shadowBanned;
    private String avatarURL;
    private CommunicateUserTypeEnum communicateUserType;
    private String communicateUserToken;

    //
    // Constructor(s)
    //
    
    public CommunicateUser() {
        super();
        this.communicateUserType = null;
        this.representedResource = null;
    }

    //
    // Business Methods
    //
    
    //
    // Bean Methods
    //
    
    public CommunicateUserTypeEnum getCommunicateUserType() {
        return communicateUserType;
    }

    public void setCommunicateUserType(CommunicateUserTypeEnum communicateUserType) {
        this.communicateUserType = communicateUserType;
    }

    public CommunicateSurrogateResourceReference getRepresentedResource() {
        return representedResource;
    }

    public void setRepresentedResource(CommunicateSurrogateResourceReference representedResource) {
        this.representedResource = representedResource;
    }

    public String getCommunicateUserToken() {
        return communicateUserToken;
    }

    public void setCommunicateUserToken(String communicateUserToken) {
        this.communicateUserToken = communicateUserToken;
    }

    public boolean isSurrogate() {
        return surrogate;
    }

    public void setSurrogate(boolean surrogate) {
        this.surrogate = surrogate;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public boolean isShadowBanned() {
        return shadowBanned;
    }

    public void setShadowBanned(boolean shadowBanned) {
        this.shadowBanned = shadowBanned;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }
    
    //
    // Utility Methods
    //
    
    @Override
    protected Logger getLogger() {
    	return(LOG);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CommunicateUser.class.getSimpleName() + "[", "]")
                .add("representedResource=" + getRepresentedResource())
                .add("surrogate=" + isSurrogate())
                .add("administrator=" + isAdministrator())
                .add("deactivated=" + isDeactivated())
                .add("shadowBanned=" + isShadowBanned())
                .add("avatarURL='" + getAvatarURL() + "'")
                .add("communicateUserType=" + getCommunicateUserType())
                .add("communicateUserToken='" + getCommunicateUserToken() + "'")
                .add("logins=" + getLogins())
                .add("status='" + getStatus() + "'")
                .add("logins=" + getLogins())
                .add("status='" + getStatus() + "'")
                .add("gender='" + getGender() + "'")
                .add("name=" + getName())
                .add("birthDate=" + getBirthDate())
                .add("address=" + getAddress())
                .add("contactPoints=" + getContactPoints())
                .add("status='" + getStatus() + "'")
                .add("objectID=" + getObjectID())
                .add("securityLabels=" + getSecurityLabels())
                .add("metadata=" + getMetadata())
                .add("identifiers=" + getIdentifiers())
                .add("id=" + getId())
                .toString();
    }
}
