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
package net.fhirfactory.dricats.model.common;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import net.fhirfactory.dricats.model.common.naming.QualifiedName;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.dricats.model.security.datatypes.SecurityLabels;

public class DistributableObject extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900002L;
    private static final Logger LOG = LoggerFactory.getLogger(DistributableObject.class);

    //
    // Attributes
    //

    private List<DistributableObjectIdentifier> identifiers;
    private DistributableObjectId objectID;
    private DistributableObjectMetadata metadata;
    private SecurityLabels securityLabels;

    //
    // Constructor(s)
    //

    public DistributableObject(){
    	super();
        this.identifiers = new ArrayList<>();
        this.metadata = new DistributableObjectMetadata();
        this.securityLabels = new SecurityLabels();
    }

    public DistributableObject(QualifiedName qualifiedName) {
        super();
        this.identifiers = new ArrayList<>();
        DistributableObjectId objectId = new DistributableObjectId(qualifiedName);
        this.setObjectID(objectId);
        this.metadata = new DistributableObjectMetadata();
        setId(getObjectID().getQualifiedName().getCommonName());
        this.securityLabels = new SecurityLabels();
    }
    
    public DistributableObject( DistributableObjectIdentifier identifier) {
        super();
        this.identifiers = new ArrayList<>();
        this.metadata = new DistributableObjectMetadata();
        this.securityLabels = new SecurityLabels();
        getIdentifiers().add(identifier);
    }

    public DistributableObject(DistributableObject ori) {
    	super();
        if(getIdentifiers() == null){
            setIdentifiers(new ArrayList<>());
        }
        if(ori.getIdentifiers() != null) {
            getIdentifiers().addAll(ori.getIdentifiers());
        }
        if(ori.getMetadata() != null) {
            setMetadata(SerializationUtils.clone(ori.getMetadata()));
        } else {
            setMetadata(new DistributableObjectMetadata());
        }
        if(ori.getSecurityLabels() != null){
            setSecurityLabels(SerializationUtils.clone(ori.getSecurityLabels()));
        }
    }

    //
    // Getters and Setters
    //

    public DistributableObjectId getObjectID() {
        return objectID;
    }
    public void setObjectID(DistributableObjectId objectID) {
        this.objectID = objectID;
    }

    public SecurityLabels getSecurityLabels() {
        return securityLabels;
    }

    public void setSecurityLabels(SecurityLabels securityLabels) {
        this.securityLabels = securityLabels;
    }

    public DistributableObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DistributableObjectMetadata metadata) {
        this.metadata = metadata;
    }

    public List<DistributableObjectIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<DistributableObjectIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    @JsonIgnore
    public void addIdentifier(DistributableObjectIdentifier identifier) {
        if(getIdentifiers() == null) {
            setIdentifiers(new ArrayList<DistributableObjectIdentifier>());
        }
        getIdentifiers().add(identifier);
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributableObject{");
        sb.append("objectID=").append(getObjectID());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", securityLabels=").append(getSecurityLabels());
        sb.append(", id='").append(getId());
        sb.append('}');
        return sb.toString();
    }
}
