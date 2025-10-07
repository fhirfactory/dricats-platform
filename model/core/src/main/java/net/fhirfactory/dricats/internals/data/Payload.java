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
package net.fhirfactory.dricats.internals.data;

import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationDataObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Payload extends ApplicationDataObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900003L;
    private static final Logger LOG = LoggerFactory.getLogger(Payload.class);

    //
    // Attributes
    //
    private PayloadSecurityStatus payloadSecurityStatus;
    private String payloadContent;

    //
    // Constructor(s)
    //

    public Payload() {
        super();
    }

    //
    // Bean Methods
    //

    public PayloadSecurityStatus getPayloadSecurityStatus() {
        return payloadSecurityStatus;
    }

    public void setPayloadSecurityStatus(PayloadSecurityStatus payloadSecurityStatus) {
        this.payloadSecurityStatus = payloadSecurityStatus;
    }

    public String getPayloadContent() {
        return payloadContent;
    }

    public void setPayloadContent(String payloadContent) {
        this.payloadContent = payloadContent;
    }

    //
    // Utility Methods
    //

    @Override
    protected Logger getLogger(){
        return(LOG);
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("payloadSecurityStatus", payloadSecurityStatus)
                 .append("payloadContent", payloadContent)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Payload payload = (Payload) o;
        return Objects.equals(payloadSecurityStatus, payload.payloadSecurityStatus) && Objects.equals(payloadContent, payload.payloadContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), payloadSecurityStatus, payloadContent);
    }
}
