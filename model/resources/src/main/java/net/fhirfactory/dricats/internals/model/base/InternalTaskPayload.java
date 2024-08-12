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
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept;
import net.fhirfactory.dricats.internals.model.base.dataytypes.SerialisableObject;

public class InternalTaskPayload extends SerialisableObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900071L;
    private static final Logger LOG = LoggerFactory.getLogger(InternalTaskPayload.class);

    //
    // Attributes
    //

    private CodeableConcept payloadType;
    private String payloadDescription;
    private String payloadId;
    private String payloadContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime payloadCreated;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSXXX")
    private LocalDateTime payloadUpdated;

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

    public String getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(String payloadId) {
        this.payloadId = payloadId;
    }

    public String getPayloadContent() {
        return payloadContent;
    }

    public void setPayloadContent(String payloadContent) {
        this.payloadContent = payloadContent;
    }

    public LocalDateTime getPayloadCreated() {
        return payloadCreated;
    }

    public void setPayloadCreated(LocalDateTime payloadCreated) {
        this.payloadCreated = payloadCreated;
    }

    public LocalDateTime getPayloadUpdated() {
        return payloadUpdated;
    }

    public void setPayloadUpdated(LocalDateTime payloadUpdated) {
        this.payloadUpdated = payloadUpdated;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }
}
