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
package net.fhirfactory.dricats.internals.common;

import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.security.datatypes.SecurityLabels;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.UUID;

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

    private DistributableObjectId objectID;
    private DistributableObjectMetadata metadata;
    private SecurityLabels securityLabels;
    //
    // Constructor(s)
    //

    public SimpleDistributableObject(){
    	super();
        UnqualifiedName unqualifiedName = new UnqualifiedName("Object", UUID.randomUUID().toString());
        QualifiedName qualifiedName = new QualifiedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        this.setObjectID(new DistributableObjectId(qualifiedName));
        this.setId(getObjectID().getQualifiedName().getCommonName());
        this.metadata = new DistributableObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(QualifiedName qualifiedName) {
        super();
        DistributableObjectId objectId = new DistributableObjectId(qualifiedName);
        this.setObjectID(objectId);
        this.setId(getObjectID().getQualifiedName().getCommonName());
        this.metadata = new DistributableObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(DistributableObjectIdentifier identifier) {
        super();
        this.metadata = new DistributableObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public SimpleDistributableObject(SimpleDistributableObject ori) {
    	super();
        this.securityLabels = new SecurityLabels();
        if(ori.getMetadata() != null) {
            setMetadata(SerializationUtils.clone(ori.getMetadata()));
        } else {
            setMetadata(new DistributableObjectMetadata());
        }
        if(ori.getSecurityLabels() != null) {
            setSecurityLabels(SerializationUtils.clone(ori.getSecurityLabels()));
        } else {
            setSecurityLabels(new SecurityLabels());
        }
        if(ori.getObjectID() != null) {
            setObjectID(SerializationUtils.clone(ori.getObjectID()));
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

    public DistributableObjectId getObjectID() {
        return objectID;
    }
    public void setObjectID(DistributableObjectId objectID) {
        this.objectID = objectID;
        this.setId(objectID.getQualifiedName().getCommonName());
    }

    public DistributableObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DistributableObjectMetadata metadata) {
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
        sb.append("objectID=").append(getObjectID());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id='").append(getId());
        sb.append('}');
        return sb.toString();
    }
}
