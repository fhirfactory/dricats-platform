/*
 * Copyright (c) 2024 Mark A. Hunter
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

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class DistributableObjectReference implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900008L;

    //
    // Attributes
    //

    private DistributableObjectIdentifier targetObjectIdentifier;
    private String referenceDescription;
    private String targetObjectType;

    //
    // Constructor(s)
    //

    public DistributableObjectReference() {
        targetObjectIdentifier = null;
        referenceDescription = null;
        targetObjectType = null;
    }

    public DistributableObjectReference(DistributableObjectIdentifier targetObjectIdentifier, String referenceDescription, String targetObjectType) {
        this.targetObjectIdentifier = targetObjectIdentifier;
        this.referenceDescription = referenceDescription;
        this.targetObjectType = targetObjectType;
    }

    //
    // Bean Methods
    //

    public DistributableObjectIdentifier getTargetObjectIdentifier() {
        return targetObjectIdentifier;
    }

    public void setTargetObjectIdentifier(DistributableObjectIdentifier targetObjectIdentifier) {
        this.targetObjectIdentifier = targetObjectIdentifier;
    }

    public String getReferenceDescription() {
        return referenceDescription;
    }

    public void setReferenceDescription(String referenceDescription) {
        this.referenceDescription = referenceDescription;
    }

    public String getTargetObjectType() {
        return targetObjectType;
    }

    public void setTargetObjectType(String targetObjectType) {
        this.targetObjectType = targetObjectType;
    }

    //
    // Utility Methods
    //

    // toString()

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DistributableObjectReference{");
        sb.append("targetObjectIdentifier=").append(getTargetObjectIdentifier());
        sb.append(", referenceDescription='").append(getReferenceDescription()).append('\'');
        sb.append(", targetObjectType='").append(getTargetObjectType()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    // equals()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistributableObjectReference that = (DistributableObjectReference) o;
        return Objects.equals(getTargetObjectIdentifier(), that.getTargetObjectIdentifier()) && Objects.equals(getTargetObjectType(), that.getTargetObjectType());
    }

    // hashCode()

    @Override
    public int hashCode() {
        return Objects.hash(getTargetObjectIdentifier(), getReferenceDescription(), getTargetObjectType());
    }
}
