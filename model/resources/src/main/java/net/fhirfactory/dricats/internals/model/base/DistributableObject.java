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
package net.fhirfactory.dricats.internals.model.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DistributableObject implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900002L;
    private static final Logger LOG = LoggerFactory.getLogger(DistributableObject.class);

    //
    // Attributes
    //

    private DistributableObjectId id;
    private List<DistributableObjectIdentifier> identifiers;
    private DistributableObjectMetadata metadata;

    //
    // Constructor(s)
    //

    public DistributableObject(){
        this.identifiers = new ArrayList<>();
        this.metadata = new DistributableObjectMetadata();
        this.id = null;
    }

    public DistributableObject(DistributableObject ori) {
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
        if(ori.getId() != null) {
            setId(SerializationUtils.clone(ori.getId()));
        }
    }

    //
    // Getters and Setters
    //

    public DistributableObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(DistributableObjectMetadata metadata) {
        this.metadata = metadata;
    }

    public DistributableObjectId getId() {
        return id;
    }

    public void setId(DistributableObjectId id) {
        this.id = id;
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
}
