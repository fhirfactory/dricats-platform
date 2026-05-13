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
package net.fhirfactory.dricats.internals.events.notifications;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.Payload;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.util.UUID;

public class NotificationPayload extends Payload {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900061L;

    //
    // Attributes
    //

    public static final String ELEMENT_SPECIALIZATION = "NotificationPayload";

    //
    // Constructor(s)
    //

    public NotificationPayload() {
        super();
        RelativeDistinguishedName unqualifiedName = new RelativeDistinguishedName(ELEMENT_SPECIALIZATION, UUID.randomUUID().toString());
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(unqualifiedName);
        ElementIdentifier identifier = new ElementIdentifier(qualifiedName);
        setIdentifier(identifier);
        setSpecialization(ELEMENT_SPECIALIZATION);
        setElementType(ElementTypeEnum.APPLICATION_DATA_OBJECT);
        setShortName(ELEMENT_SPECIALIZATION+"->"+unqualifiedName.getUnqualifiedValue());
        setDocumentation(getShortName());
    }

    //
    // Bean Methods
    //


    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("localObjectId", getElementInstanceId())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("elementType", getElementType())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("accessingFunctions", getAccessingFunctions())
                .append("accessingServices", getAccessingServices())
                .append("dataFormat", getDataFormat())
                .append("dataTopic", getDataTopic())
                .append("payloadSecurityStatus", getPayloadSecurityStatus())
                .append("payloadContent", getPayloadContent())
                .toString();
    }
}
