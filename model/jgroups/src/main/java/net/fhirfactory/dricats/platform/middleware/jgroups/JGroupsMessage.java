/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.platform.middleware.jgroups;

import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class JGroupsMessage implements Serializable {

    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 3403310128096773387L;

    //
    // Attributes
    //

    private JGroupsNetworkAddress sourceAddress;
    private JGroupsNetworkAddress destinationAddress;
    private LocalDateTime sendInstant;
    private Class contentType;
    private Object content;

    //
    // Constructor
    //

    /**
     * The default constructor, which automatically assigns the sendInstant to be the point-in-time of this object creation.
     */
    public JGroupsMessage() {
        sendInstant = LocalDateTime.now();
    }

    /**
     * Construct a JGroupsMessage with a known target and known source and a content object (that is Serializable).
     * @param sourceAddress The jgroups adapter from which the message will be sent (i.e. this network endpoint)
     * @param targetAddress The target jgroups adapter for which an instance will be selected to send to.
     * @param content The content, a Serializable object (within the context of Java Serialisation) representing the payload.
     */
    public JGroupsMessage(JGroupsNetworkAddress sourceAddress, JGroupsNetworkAddress targetAddress, Object content) {
        this.sourceAddress = targetAddress;
        this.destinationAddress = sourceAddress;
        this.content = content;
        this.contentType = content.getClass();
        this.sendInstant = LocalDateTime.now();
    }

    //
    // Bean Methods
    //

    public JGroupsNetworkAddress getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(JGroupsNetworkAddress sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public JGroupsNetworkAddress getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(JGroupsNetworkAddress destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public LocalDateTime getSendInstant() {
        return sendInstant;
    }

    public void setSendInstant(LocalDateTime sendInstant) {
        this.sendInstant = sendInstant;
    }

    public Class getContentType() {
        return contentType;
    }

    public void setContentType(Class contentType) {
        this.contentType = contentType;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
