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
package net.fhirfactory.dricats.model.data;

import net.fhirfactory.dricats.model.common.SerialisableObject;

import java.io.Serial;
import java.io.Serializable;

public class PayloadSecurityStatus extends SerialisableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900004L;

    //
    // Attributes
    //
    private String securityStatus;
    private String securityStatusDescription;

    //
    // Constructor(s)
    //

    public PayloadSecurityStatus() {
        super();
    }

    //
    // Bean Methods
    //
    public String getSecurityStatus() {
        return securityStatus;
    }

    public void setSecurityStatus(String securityStatus) {
        this.securityStatus = securityStatus;
    }

    public String getSecurityStatusDescription() {
        return securityStatusDescription;
    }

    public void setSecurityStatusDescription(String securityStatusDescription) {
        this.securityStatusDescription = securityStatusDescription;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return "PayloadSecurityStatus{" +
                "securityStatus='" + securityStatus + '\'' +
                ", securityStatusDescription='" + securityStatusDescription + '\'' +
                ", id = '" + getId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayloadSecurityStatus that = (PayloadSecurityStatus) o;
        return securityStatus.equals(that.securityStatus);
    }

    @Override
    public int hashCode() {
        return securityStatus.hashCode();
    }

}
