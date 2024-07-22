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

import net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class MessagePayload implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900057L;
    private static final Logger LOG = LoggerFactory.getLogger(MessagePayload.class);

    //
    // Attributes
    //

    private CodeableConcept payloadType;
    private String payloadDescription;
    private String payload;

    //
    // Constructor(s)
    //

    //
    // Bean Methods
    //

    public CodeableConcept getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(CodeableConcept payloadType) {
        this.payloadType = payloadType;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }

    public void setPayloadDescription(String payloadDescription) {
        this.payloadDescription = payloadDescription;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
