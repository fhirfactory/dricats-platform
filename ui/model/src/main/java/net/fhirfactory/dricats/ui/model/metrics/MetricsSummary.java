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

package net.fhirfactory.dricats.ui.model.metrics;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.object.SimpleDistributableObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MetricsSummary extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //
    private Map<String, String> metrics;
    private DistributableObjectId metricsOwner;

    //
    // Constructor(s)
    //
    public MetricsSummary(){
        super();
        this.metrics = new HashMap<>();
        this.metricsOwner = null;
    }

    public MetricsSummary(DistributableObjectId metricsOwner){
        super();
        this.metrics = new HashMap<>();
        this.metricsOwner = metricsOwner;
    }

    //
    // Accessor(s) / Mutator(s)
    //
    public Map<String, String> getMetrics() {
        return metrics;
    }
    public void setMetrics(Map<String, String> metrics) {
        this.metrics = metrics;
    }

    public DistributableObjectId getMetricsOwner() {
        return metricsOwner;
    }
    public void setMetricsOwner(DistributableObjectId metricsOwner) {
        this.metricsOwner = metricsOwner;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("metricsOwner", getMetricsOwner())
                .append("metrics", getMetrics())
                .append("securityLabels", getSecurityLabels())
                .append("objectID", getObjectID())
                .append("metadata", getMetadata())
                .append("id", getLocalId())
                .toString();
    }
}
