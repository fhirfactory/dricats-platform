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
package net.fhirfactory.dricats.internals.common.object;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.object.datatypes.ObjectMetadata;
import net.fhirfactory.dricats.internals.security.datatypes.SecurityLabels;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class SimpleDistributableObject extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900002L;
    private static final Logger LOG = LoggerFactory.getLogger(SimpleDistributableObject.class);

    //
    // Attributes
    //

    private ObjectMetadata metadata;
    private SecurityLabels securityLabels;
    //
    // Constructor(s)
    //

    public SimpleDistributableObject(){
    	super();
        this.metadata = new ObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(FullyDistinguishedName qualifiedName) {
        super();
        ObjectId objectId = new ObjectId(qualifiedName);
        this.setObjectId(objectId);
        this.metadata = new ObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(ElementIdentifier identifier) {
        super();
        this.metadata = new ObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(SimpleDistributableObject ori) {
    	super();
        this.securityLabels = new SecurityLabels();
        if(ori.getMetadata() != null) {
            setMetadata(SerializationUtils.clone(ori.getMetadata()));
        } else {
            setMetadata(new ObjectMetadata());
        }
        if(ori.getSecurityLabels() != null) {
            setSecurityLabels(SerializationUtils.clone(ori.getSecurityLabels()));
        } else {
            setSecurityLabels(new SecurityLabels());
        }
        if(ori.getObjectId() != null) {
            setObjectId(SerializationUtils.clone(ori.getObjectId()));
        }
    }

    //
    // Getters and Setters
    //

    public SecurityLabels getSecurityLabels() {
        return securityLabels;
    }

    public void setSecurityLabels(SecurityLabels securityLabels) {
        this.securityLabels = securityLabels;
    }

    public ObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleDistributableObject{");
        sb.append("objectID=").append(getObjectId());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id='").append(getObjectId());
        sb.append('}');
        return sb.toString();
    }
}
