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

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private List<ElementIdentifier> identifiers;


    //
    // Constructor(s)
    //

    public DistributableObject(){
    	super();
        this.identifiers = new ArrayList<>();

    }

    public DistributableObject(FullyDistinguishedName qualifiedName) {
        super(qualifiedName);
        this.identifiers = new ArrayList<>();
    }
    
    public DistributableObject( ElementIdentifier identifier) {
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

    public List<ElementIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<ElementIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    @JsonIgnore
    public void addIdentifier(ElementIdentifier identifier) {
        if(getIdentifiers() == null) {
            setIdentifiers(new ArrayList<ElementIdentifier>());
        }
        getIdentifiers().add(identifier);
    }

    /**
     *
     * @return DistributableObjectId.getLocalId().getKeyValue() if available, otherwise create it....
     */
    public String resolveKey() {
        String key = null;
        try {
            key = getObjectId().getKeyValue();
        } catch (Exception e) {
            // ignore
        }
        if (key != null && key.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            getObjectId().setUpperBits(uuid.getMostSignificantBits());
            getObjectId().setLowerBits(uuid.getLeastSignificantBits());
            getObjectId().setVersion(0L);
            key = getObjectId().getKeyValue();
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
        sb.append("objectID=").append(getObjectId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", securityLabels=").append(getSecurityLabels());
        sb.append(", id='").append(getObjectId());
        sb.append('}');
        return sb.toString();
    }
}
