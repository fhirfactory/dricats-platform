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

import net.fhirfactory.dricats.internals.common.id.ElementInstanceId;

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
    private ElementInstanceId elementInstanceId;
    private String referenceDescription;
    private String elementType;
    private String elementSpecialisation;

    //
    // Constructor(s)
    //

    public ElementReference() {
        this.elementIdentifier = null;
        this.referenceDescription = null;
        this.elementType = null;
        this.elementSpecialisation = null;
        this.elementInstanceId = null;
    }

    public ElementReference(ElementIdentifier targetElementIdentifier, String referenceDescription, String elementType) {
        this();
        this.elementIdentifier = targetElementIdentifier;
        this.referenceDescription = referenceDescription;
        this.elementType = elementType;
    }

    public ElementReference(ElementIdentifier targetElementIdentifier, ElementInstanceId elementInstanceId, String referenceDescription, String elementType) {
        this();
        this.elementIdentifier = targetElementIdentifier;
        this.referenceDescription = referenceDescription;
        this.elementType = elementType;
        this.elementInstanceId = elementInstanceId;
    }

    //
    // Bean Methods
    //


    public ElementInstanceId getElementInstanceId() {
        return elementInstanceId;
    }

    public void setElementInstanceId(ElementInstanceId elementInstanceId) {
        this.elementInstanceId = elementInstanceId;
    }

    public ElementIdentifier getElementIdentifier() {
        return this.elementIdentifier;
    }

    public void setElementIdentifier(ElementIdentifier targetElementIdentifier) {
        this.elementIdentifier = targetElementIdentifier;
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = referenceDescription;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getElementSpecialisation() {
        return elementSpecialisation;
    }

    public void setElementSpecialisation(String elementSpecialisation) {
        this.elementSpecialisation = elementSpecialisation;
    }

    //
    // Utility Methods
    //

    // toString()

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributableObjectReference{");
        sb.append("elementIdentifier=").append(getElementIdentifier());
        sb.append(", elementInstanceId=").append(getElementInstanceId());
        sb.append(", referenceDescription='").append(getReferenceDescription()).append('\'');
        sb.append(", elementType='").append(getElementType()).append('\'');
        sb.append(", elementSpecialisation='").append(getElementSpecialisation()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    // equals()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementReference that = (ElementReference) o;
        return Objects.equals(getElementIdentifier(), that.getElementIdentifier()) && Objects.equals(getElementType(),that.getElementType());
    }

    // hashCode()

    @Override
    public int hashCode() {
        return Objects.hash(getElementIdentifier(), getReferenceDescription(), getElementType());
    }
}
