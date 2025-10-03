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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageProcessingPathwaySummary extends ProcessingPathwaySegmentSummary implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessingPathwaySummary.class);

    //
    // Attributes
    //

    private Map<Integer, ProcessingPathwaySegmentSummary> pathwaySequencedSegments;

    //
    // Constructor(s)
    //

    public MessageProcessingPathwaySummary() {
        super();
        pathwaySequencedSegments = new HashMap<>();
    }

    //
     // Bean Methods
    //

    public Map<Integer, ProcessingPathwaySegmentSummary> getPathwaySequencedSegments() {
        return pathwaySequencedSegments;
    }

    public void setPathwaySequencedSegments(Map<Integer, ProcessingPathwaySegmentSummary> pathwaySequencedSegments) {
        this.pathwaySequencedSegments = pathwaySequencedSegments;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pathwaySequencedSegments", pathwaySequencedSegments)
                .appendSuper(super.toString())
                .toString();
    }
}
