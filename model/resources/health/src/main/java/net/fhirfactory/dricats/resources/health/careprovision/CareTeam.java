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
package net.fhirfactory.dricats.resources.health.careprovision;

import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.resources.actors.Team;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;

public class CareTeam extends Team implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private String status;
    private String reasonCode;
    private String reasonReference;
    private String category;
    private ElementReference subject;

    //
    // Constructor(s)
    //

    public CareTeam() {
        super();
        status = "";
        reasonCode = "";
        reasonReference = "";
        category = "";
        subject = new ElementReference();
    }

    //
    // Getters and Setters
    //

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonReference() {
        return reasonReference;
    }

    public void setReasonReference(String reasonReference) {
        this.reasonReference = reasonReference;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ElementReference getSubject() {
        return subject;
    }

    public void setSubject(ElementReference subject) {
        this.subject = subject;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", CareTeam.class.getSimpleName() + "[", "]")
                .add("status='" + getStatus() + "'")
                .add("reasonCode='" + getReasonCode() + "'")
                .add("reasonReference='" + getReasonReference() + "'")
                .add("category='" + getCategory() + "'")
                .add("subject=" + getSubject())
                .add("name='" + getName() + "'")
                .add("type='" + getType() + "'")
                .add("contact=" + getContact())
                .add("members=" + getMembers())
                .add("objectID=" + getObjectID())
                .add("securityLabels=" + getSecurityLabels())
                .add("metadata=" + getMetadata())
                .add("id=" + getLocalId())
                .toString();
    }


}
