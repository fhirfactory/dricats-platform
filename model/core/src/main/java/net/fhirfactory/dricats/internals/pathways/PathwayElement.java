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

package net.fhirfactory.dricats.internals.pathways;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.reference.relationships.FlowRelationship;
import net.fhirfactory.dricats.internals.reference.relationships.valuesets.RelationshipType;
import net.fhirfactory.dricats.internals.topics.Topic;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PathwayElement extends FlowRelationship implements Serializable {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(PathwayElement.class);
    @Serial
    private static final long serialVersionUID = -12345678900001L;

    //
    // Attributes
    //
    private DistributableObjectId utilisedApplicationService;
    private List<Topic> supportedTopics;

    //
    // Constructor(s)
    //
    public PathwayElement(){
        super();
        this.supportedTopics = new ArrayList<>();
    }

    public PathwayElement(DistributableObjectId enablerComponent, String pathwayElementId, String pathwayElementName, String relationshipDescription, DistributableObjectId ingressPoint, DistributableObjectId egressPoint, List<Topic> supportedTopics){
        super();
        QualifiedName pathwayElementIdName = new QualifiedName(enablerComponent.getQualifiedName());
        UnqualifiedName pathwayElementUnqaulifiedName = new UnqualifiedName("PathwayElement", pathwayElementId);
        pathwayElementIdName.appendUnqualifiedName(pathwayElementUnqaulifiedName);
        DistributableObjectId derivedId = new DistributableObjectId(pathwayElementIdName);
        this.setName(pathwayElementName);
        this.setDocumentation(relationshipDescription);
        this.setTarget(egressPoint);
        this.setSource(ingressPoint);
        this.setFlowType("Information");
        this.setType(RelationshipType.FLOW);
        this.setSpecialization("PathwayElement");
        this.supportedTopics = new ArrayList<>();
        this.supportedTopics.addAll(supportedTopics);
    }

    //
    // Business Methods
    //


    public DistributableObjectId getUtilisedApplicationService() {
        return utilisedApplicationService;
    }

    public void setUtilisedApplicationService(DistributableObjectId utilisedApplicationService) {
        this.utilisedApplicationService = utilisedApplicationService;
    }

    public List<Topic> getSupportedTopics() {
        return supportedTopics;
    }

    public void setSupportedTopics(List<Topic> supportedTopics) {
        this.supportedTopics = supportedTopics;
    }

    //
     // Standard Methods
     //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("utilisedApplicationService", utilisedApplicationService)
                .append("supportedTopics", supportedTopics)
                .appendSuper(super.toString())
                .toString();
    }
}
