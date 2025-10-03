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
package net.fhirfactory.dricats.internals.pathways.summaries;

import net.fhirfactory.dricats.internals.common.DistributableObject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.pubsub.messages.MessageSubscription;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessingPathwaySegmentSummary extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPathwaySegmentSummary.class);

    private DistributableObjectId sourceApplicationInterface;
    private DistributableObjectId targetApplicationInterface;
    private List<MessageSubscription> trafficType;

    //
    // Constructor(s)
    //

    public ProcessingPathwaySegmentSummary() {
        super();
        sourceApplicationInterface = null;
        targetApplicationInterface = null;
        trafficType = new ArrayList<>();
    }

    //
     // Bean Methods
    //

    public DistributableObjectId getSourceApplicationInterface() {
        return sourceApplicationInterface;
    }

    public void setSourceApplicationInterface(DistributableObjectId sourceApplicationInterface) {
        this.sourceApplicationInterface = sourceApplicationInterface;
    }

    public DistributableObjectId getTargetApplicationInterface() {
        return targetApplicationInterface;
    }

    public void setTargetApplicationInterface(DistributableObjectId targetApplicationInterface) {
        this.targetApplicationInterface = targetApplicationInterface;
    }

    public List<MessageSubscription> getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(List<MessageSubscription> trafficType) {
        this.trafficType = trafficType;
    }

    public void addTrafficType(MessageSubscription trafficType) {
        this.trafficType.add(trafficType);
    }

    public void removeTrafficType(MessageSubscription trafficType) {
        this.trafficType.remove(trafficType);
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sourceApplicationInterface", sourceApplicationInterface)
                .append("targetApplicationInterface", targetApplicationInterface)
                .append("trafficType", trafficType)
                .toString();
    }
}
