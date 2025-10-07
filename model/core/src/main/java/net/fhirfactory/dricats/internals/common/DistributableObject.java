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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.fhirfactory.dricats.internals.security.datatypes.SecurityLabels;

public class DistributableObject extends SimpleDistributableObject implements Serializable {
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


    //
    // Constructor(s)
    //

    public DistributableObject(){
    	super();
        this.identifiers = new ArrayList<>();

    }

    public DistributableObject(QualifiedName qualifiedName) {
        super(qualifiedName);
        this.identifiers = new ArrayList<>();
    }
    
    public DistributableObject( DistributableObjectIdentifier identifier) {
        super();
        this.identifiers = new ArrayList<>();
        getIdentifiers().add(identifier);
    }

    public DistributableObject(DistributableObject ori) {
    	super(ori);
        if(getIdentifiers() == null){
            setIdentifiers(new ArrayList<>());
        }
        if(ori.getIdentifiers() != null) {
            getIdentifiers().addAll(ori.getIdentifiers());
        }
        if(ori.getSecurityLabels() != null){
            setSecurityLabels(SerializationUtils.clone(ori.getSecurityLabels()));
        }
    }

    //
    // Getters and Setters
    //

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

    public String resolveKey() {
        String key = null;
        try {
            DistributableObjectId objectId = this.getObjectID();
            if (objectId != null && objectId.getQualifiedName() != null && objectId.getQualifiedName().getCommonName().getValue() != null && !objectId.getQualifiedName().getCommonName().getValue().isEmpty()) {
                key = objectId.getQualifiedName().getCommonName().getValue();
            }
        } catch (Exception e) {
            // ignore
        }
        if (key == null) {
            try {
                CommonName cn = this.getId();
                if (cn != null && cn.getValue() != null && !cn.getValue().isEmpty()) {
                    key = cn.getValue();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        if (key == null) {
            key = UUID.randomUUID().toString();
            try {
                this.setId(new CommonName(key));
            } catch (Exception e) {
                LOG.debug("resolveKey(NotificationSubscription): unable to set generated id on item", e);
            }
        }
        return key;
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
