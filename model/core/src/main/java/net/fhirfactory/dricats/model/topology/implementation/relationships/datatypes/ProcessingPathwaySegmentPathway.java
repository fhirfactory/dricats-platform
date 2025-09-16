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

package net.fhirfactory.dricats.model.topology.implementation.relationships.datatypes;

import net.fhirfactory.dricats.model.common.DistributableObjectId;
import net.fhirfactory.dricats.model.reference.relationships.FlowRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class ProcessingPathwaySegmentPathway extends FlowRelationship implements Serializable {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPathwaySegmentPathway.class);
    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //
    private DistributableObjectId sourceApplicationInterface;
    private DistributableObjectId targetApplicationInterface;
    private DistributableObjectId utilisedApplicationService;

    //
    // Constructor(s)
    //
    public ProcessingPathwaySegmentPathway(String relationshipID, String relationshipName, String relationshipDescription){
        super();
        DistributableObjectId derivedId = new DistributableObjectId(relationshipID);
        this.setName(relationshipName);
        this.setDocumentation(relationshipDescription);
    }

    //
    // Business Methods
    //

    public DistributableObjectId getSourceApplicationInterface() {
        return sourceApplicationInterface;
    }

    public void setSourceApplicationInterface(DistributableObjectId sourceApplicationInterface) {
        this.sourceApplicationInterface = sourceApplicationInterface;
        this.setSource(sourceApplicationInterface);
    }

    public DistributableObjectId getTargetApplicationInterface() {
        return targetApplicationInterface;
    }

    public void setTargetApplicationInterfaces(DistributableObjectId targetApplicationInterface) {
        this.targetApplicationInterface = targetApplicationInterface;
        this.setTarget(targetApplicationInterface);
    }

    public DistributableObjectId getUtilisedApplicationService() {
        return utilisedApplicationService;
    }

    public void setUtilisedApplicationService(DistributableObjectId utilisedApplicationService) {
        this.utilisedApplicationService = utilisedApplicationService;
    }

}
