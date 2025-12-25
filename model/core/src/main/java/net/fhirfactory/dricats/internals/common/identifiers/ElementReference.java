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
package net.fhirfactory.dricats.internals.common.identifiers;

import net.fhirfactory.dricats.internals.common.id.ObjectId;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ElementReference implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900008L;

    //
    // Attributes
    //

    private ElementIdentifier elementIdentifier;
    private ObjectId localObjectId;
    private String referenceDescription;
    private String objectType;
    private String objectSpecialisation;

    //
    // Constructor(s)
    //

    public ElementReference() {
        this.elementIdentifier = null;
        this.localObjectId = null;
        this.referenceDescription = null;
        this.objectType = null;
    }

    public ElementReference(ElementIdentifier targetObjectIdentifier, String referenceDescription, String objectType) {
        this.elementIdentifier = targetObjectIdentifier;
        this.localObjectId = null;
        this.referenceDescription = referenceDescription;
        this.objectType = objectType;
    }

    public ElementReference(ObjectId targetObjectId, String referenceDescription, String objectType) {
        this.elementIdentifier = null;
        this.localObjectId = targetObjectId;
        this.referenceDescription = referenceDescription;
        this.objectType = objectType;
    }

    //
    // Bean Methods
    //

    public ElementIdentifier getElementIdentifier() {
        return this.elementIdentifier;
    }

    public void setElementIdentifier(ElementIdentifier targetObjectIdentifier) {
        this.elementIdentifier = targetObjectIdentifier;
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = referenceDescription;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectSpecialisation() {
        return objectSpecialisation;
    }

    public void setObjectSpecialisation(String objectSpecialisation) {
        this.objectSpecialisation = objectSpecialisation;
    }

    public ObjectId getLocalObjectId() {
        return localObjectId;
    }
    public void setLocalObjectId(ObjectId localObjectId) {
        this.localObjectId = localObjectId;
    }

    //
    // Utility Methods
    //

    // toString()

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributableObjectReference{");
        sb.append("targetObjectIdentifier=").append(getElementIdentifier());
        sb.append(", referenceDescription='").append(getReferenceDescription()).append('\'');
        sb.append(", targetObjectType='").append(getObjectType()).append('\'');
        sb.append(", objectSpecialisation='").append(getObjectSpecialisation()).append('\'');
        sb.append(", objectId=").append(getLocalObjectId());
        sb.append('}');
        return sb.toString();
    }

    // equals()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementReference that = (ElementReference) o;
        return Objects.equals(getElementIdentifier(), that.getElementIdentifier()) && Objects.equals(getObjectType(),that.getObjectType()) && Objects.equals(getLocalObjectId(), that.getLocalObjectId());
    }

    // hashCode()

    @Override
    public int hashCode() {
        return Objects.hash(getElementIdentifier(), getReferenceDescription(), getObjectType(), getLocalObjectId());
    }
}
