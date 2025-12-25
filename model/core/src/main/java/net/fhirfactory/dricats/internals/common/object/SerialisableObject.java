/*
 * Copyright (c) 2024 Mark A. Hunter
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

public class SerialisableObject implements Serializable{
	//
	// Housekeeping
	//

    @Serial
    private static final long serialVersionUID = 4428609418582057973L;
	
    //
    // Attributes
    //

    private ObjectId localId;

    //
    // Constructor(s)
    //

    public SerialisableObject() {
        super();
        ObjectId idValue = new ObjectId();
        idValue.setName(new CommonName(UUID.randomUUID().toString()));
        idValue.setQualifier(new CommonQualifier("Object"));
        setLocalId(idValue);
    }

    public SerialisableObject(ObjectId idValue){
        super();
        setLocalId(idValue);
    }

    //
    // Bean Methods
    //

    public ObjectId getLocalId() {
        return localId;
    }

    public void setLocalId(ObjectId localId) {
        this.localId = localId;
    }

    @JsonIgnore
    public CommonName getCommonName(){
        return(getLocalId().getName());
    }

    @JsonIgnore
    public void setCommonName(CommonName commonName){
        getLocalId().setName(commonName);
    }

    public Long getSerialVersionUID() {
    	return(serialVersionUID);
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", SerialisableObject.class.getSimpleName() + "[", "]")
                .add("id=" + getLocalId())
                .toString();
    }
}
