/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.ui.uitest.testdata;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.topics.Topic;
import net.fhirfactory.dricats.ui.serverside.caches.pathways.UIPathwayCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.PathwayResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PathwayTestResourceSetBuilder {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(PathwayResourceHandler.class);

    //
    // Attributes
    //

    private Map<String, TestComponent> enablerComponentMap = new HashMap<>();


    @Inject
    private UIPathwayCacheService pathwayCacheService;

    //
    // Constructor(s)
    //
    public PathwayTestResourceSetBuilder(){
        LOG.debug(".constructor(): Entry");
        LOG.debug(".constructor(): Exit");
    }

    //
    // Post Construct (for dependency injection)
    //
    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        try {
            // createTestPathways();
            pathwayCacheService.setInitialised(true);
        } catch (Exception e){
            LOG.warn(".initialise(): Failed to pre-populate test pathways: {}", e.getMessage());
        }
        LOG.debug(".initialise(): Exit");
    }

    //
     // Inner Classes
     //

    private class TestComponent{
        private DistributableObjectId componentId;
        private String description;
        private DistributableObjectId ingressPoint;
        private DistributableObjectId egressPoint;
        private List<Topic> supportedTopics;

        private TestComponent(String componentName, String description, List<Topic> supportedTopics){
            FullyDistinguishedName componentQN = new FullyDistinguishedName();
            componentQN.appendUnqualifiedName(new RelativeDistinguishedName("Solution", "TestSolution1"));
            componentQN.appendUnqualifiedName(new RelativeDistinguishedName("Subsystem", "TestSubsystem1"));
            componentQN.appendUnqualifiedName(new RelativeDistinguishedName("ApplicationInstance", "TestApplication1"));
            componentQN.appendUnqualifiedName(new RelativeDistinguishedName("Component", componentName));
            this.componentId = new DistributableObjectId(componentQN);
            this.description = description;
            FullyDistinguishedName ingressQN = new FullyDistinguishedName(componentQN);
            ingressQN.appendUnqualifiedName(new RelativeDistinguishedName("IngressPoint", "TestIngressPoint1"));
            this.ingressPoint = new DistributableObjectId(ingressQN);
            FullyDistinguishedName egressQN = new FullyDistinguishedName(componentQN);
            egressQN.appendUnqualifiedName(new RelativeDistinguishedName("EgressPoint", "TestEgressPoint1"));
            this.egressPoint = new DistributableObjectId(egressQN);
            this.supportedTopics = new ArrayList<>();
            this.supportedTopics.addAll(supportedTopics);
        }
    }

    //
    // Getters and Setters
    //

    protected UIPathwayCacheService getPathwayCacheService(){
        return pathwayCacheService;
    }

    protected Logger getLogger() {
        return LOG;
    }

    //
    // Business Methods
    //

    /**
     * Create and register some simple test pathways in the cache.
     */
/*    public void createTestPathways(){
        LOG.info("Creating sample MessageProcessingPathway data for UI testing");
        // LIMS --> PAS
        Pathway pathologyResults = buildPathway("PathologyResults");
        pathologyResults.setRouteSelectionCriteria(PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM);
        pathologyResults.getPossiblePathwayRoutes().put(1, buildPathwayRoute(pathologyResults.getObjectID(), PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_ROUND_ROBIN, "ADT_A01_Ingress", "ADT_Route_A", "ADT_Validate"));
        pathologyResults.getPossiblePathwayRoutes().put(2, buildPathwayRoute(pathologyResults.getObjectID(), PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_ALL, "ADT_Normalize", "ADT_Persist", "ADT_Notify"));
        getPathwayCacheService().addPathway(pathologyResults);

        // Referral Processing pathway
        Pathway referralProcessing = buildPathway("ReferralProcessing");
        referralProcessing.getPossiblePathwayRoutes().put(1, buildPathwayRoute(referralProcessing.getObjectID(), PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM, "REF_Ingress", "REF_Parse", "REF_Validate"));
        referralProcessing.getPossiblePathwayRoutes().put(2, buildPathwayRoute(referralProcessing.getObjectID(), PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_ROUND_ROBIN, "REF_Enrich", "REF_Map", "REF_Persist"));
        referralProcessing.getPossiblePathwayRoutes().put(3, buildPathwayRoute(referralProcessing.getObjectID(), PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_ALL, "REF_Publish", "REF_Notify", "REF_Audit"));
        getPathwayCacheService().addPathway(referralProcessing);

        // Lab Order → Result pathway
        Pathway labOrderToResult = buildPathway("LabOrderToResult");
        labOrderToResult.getPossiblePathwayRoutes().put(1, buildPathwayRoute(labOrderToResult.getObjectID(),PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_ROUND_ROBIN, "LAB_Order_Ingress", "LAB_Order_Validate", "LAB_Order_Persist"));
        labOrderToResult.getPossiblePathwayRoutes().put(2, buildPathwayRoute(labOrderToResult.getObjectID(),PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM, "LAB_Result_Ingress", "LAB_Result_Map", "LAB_Result_Publish"));
        getPathwayCacheService().addPathway(labOrderToResult);

        LOG.info("Added {} test pathways to cache", getPathwayCacheService().getPathways().size());
    }


    private DistributableObjectId buildPathwayRoute(DistributableObjectId pathwayId, PathwayRouteSelectionCriteriaEnum pattern, String a, String b, String c){
        // Create segment with deterministic ID
        PathwayRoute pathwayRoute = new PathwayRoute();
        // Distribution pattern moved to Pathway (routeSelectionCriteria); no per-route pattern in refactored model
        QualifiedName segQN = new QualifiedName(pathwayId.getQualifiedName());
        segQN.appendUnqualifiedName(new UnqualifiedName("Segment", a+"-"+b+"-"+c));
        pathwayRoute.setObjectID(new DistributableObjectId(segQN));

        // Build three exemplar flow elements
        PathwayElement p1 = new PathwayElement("rel:"+a+"->"+b, a+" to "+b, "Flow from "+a+" to "+b);
        p1.setSource(new DistributableObjectId("if:"+a));
        p1.setTarget(new DistributableObjectId("if:"+b));
        p1.setUtilisedApplicationService(new DistributableObjectId("svc:"+a.toLowerCase()));
        QualifiedName e1QN = new QualifiedName(); e1QN.appendUnqualifiedName(new UnqualifiedName("Flow", a+"-"+b));
        p1.setObjectID(new DistributableObjectId(e1QN));
        getPathwayCacheService().addPathwayElement(p1);

        PathwayElement p2 = new PathwayElement("rel:"+b+"->"+c, b+" to "+c, "Flow from "+b+" to "+c);
        p2.setSource(new DistributableObjectId("if:"+b));
        p2.setTarget(new DistributableObjectId("if:"+c));
        p2.setUtilisedApplicationService(new DistributableObjectId("svc:"+b.toLowerCase()));
        QualifiedName e2QN = new QualifiedName(); e2QN.appendUnqualifiedName(new UnqualifiedName("Flow", b+"-"+c));
        p2.setObjectID(new DistributableObjectId(e2QN));
        getPathwayCacheService().addPathwayElement(p2);

        PathwayElement p3 = new PathwayElement("rel:"+a+"->"+c, a+" to "+c+" (alt)", "Alternate pathwayRouteSegment from "+a+" to "+c);
        p3.setSource(new DistributableObjectId("if:"+a));
        p3.setTarget(new DistributableObjectId("if:"+c));
        p3.setUtilisedApplicationService(new DistributableObjectId("svc:"+c.toLowerCase()));
        QualifiedName e3QN = new QualifiedName(); e3QN.appendUnqualifiedName(new UnqualifiedName("Flow", a+"-"+c));
        p3.setObjectID(new DistributableObjectId(e3QN));
        getPathwayCacheService().addPathwayElement(p3);

        // Create a pathwayRouteSegment that sequences these elements
        PathwayRouteSegment pathwayRouteSegment = new PathwayRouteSegment();
        QualifiedName pathQN = new QualifiedName(pathwayRoute.getObjectID().getQualifiedName());
        pathQN.appendUnqualifiedName(new UnqualifiedName("Path", a+"-"+b+"-"+c));
        pathwayRouteSegment.setObjectID(new DistributableObjectId(pathQN));
        pathwayRouteSegment.getPathwayElementSequence().put(1, p1.getObjectID());
        pathwayRouteSegment.getPathwayElementSequence().put(2, p2.getObjectID());
        pathwayRouteSegment.getPathwayElementSequence().put(3, p3.getObjectID());
        getPathwayCacheService().addPathwayRouteSegment(pathwayRouteSegment);

        // Link segment to the pathwayRouteSegment via ID
        pathwayRoute.getRouteSegmentSequence().put(1, pathwayRouteSegment.getObjectID());
        getPathwayCacheService().addPathwayRoute(pathwayRoute);

        return pathwayRoute.getObjectID();
    }

    private Pathway buildPathway(String name){
        Pathway p = new Pathway();
        p.setName(name);
        p.setDocumentation("Stub pathway for UI tests: "+name);
        // Build a deterministic qualified name so keys are readable and stable for UI testing
        UnqualifiedName unq = new UnqualifiedName("Pathway", name);
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(unq);
        p.setObjectID(new DistributableObjectId(qn));
        p.setSpecialization("Pathway");
        p.getMetadata().setCreationDate(LocalDateTime.now());
        p.getMetadata().setLastUpdateDate(LocalDateTime.now());
        return p;
    }

    private PathwayElement buildPathwayElement(DistributableObjectId enablerComponent, String pathwayElementId, String pathwayElementName, String relationshipDescription, DistributableObjectId ingressPoint, DistributableObjectId egressPoint, List<Topic> supportedTopics){
        PathwayElement p = new PathwayElement(enablerComponent, pathwayElementId, pathwayElementName, relationshipDescription, ingressPoint, egressPoint, supportedTopics);
        p.setSpecialization("PathwayElement");
        p.getMetadata().setCreationDate(LocalDateTime.now());
        p.getMetadata().setLastUpdateDate(LocalDateTime.now());
        return p;
    }

    */
}
