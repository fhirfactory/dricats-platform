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
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.ui.serverside.caches.pathways.UIPathwayCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ApplicationScoped
public class PathwayTestResourceSetBuilder {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(PathwayTestResourceSetBuilder.class);

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
            createTestPathways();
            pathwayCacheService.setInitialised(true);
        } catch (Exception e){
            LOG.warn(".initialise(): Failed to pre-populate test pathways: {}", e.getMessage(), e);
        }
        LOG.debug(".initialise(): Exit");
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
    public void createTestPathways(){
        LOG.info(".createTestPathways(): Creating sample MessageProcessingPathway data for UI testing");

        // Pathology Results Pathway
        Pathway pathologyResults = buildPathway("PathologyResults");
        pathologyResults.setRouteSelectionCriteria(PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM);
        LOG.info(".createTestPathways(): pathologyResultsPathway --> {}", pathologyResults.getReference());
        ElementReference route1Ref = buildPathwayRoute(pathologyResults.getReference(), 1, "ADT_A01_Ingress", "ADT_Route_A", "ADT_Validate");
        pathologyResults.getPossiblePathwayRoutes().put(0, route1Ref);
        LOG.info(".createTestPathways(): route1Ref --> {}", route1Ref);
        ElementReference route2Ref = buildPathwayRoute(pathologyResults.getReference(), 2, "ADT_Normalize", "ADT_Persist", "ADT_Notify");
        pathologyResults.getPossiblePathwayRoutes().put(1, route2Ref);
        LOG.info(".createTestPathways(): route2Ref --> {}", route2Ref);
        getPathwayCacheService().addPathway(pathologyResults);

        // Referral Processing pathway
        Pathway referralProcessing = buildPathway("ReferralProcessing");
        ElementReference refRoute1 = buildPathwayRoute(referralProcessing.getReference(), 1, "REF_Ingress", "REF_Parse", "REF_Validate");
        referralProcessing.getPossiblePathwayRoutes().put(1, refRoute1);
        getPathwayCacheService().addPathway(referralProcessing);

        LOG.info("Added {} test pathways to cache", getPathwayCacheService().getPathways().size());
    }

    private ElementReference buildPathwayRoute(ElementReference pathwayRef, int priority, String a, String b, String c) {
        DistinguishedName routeQN = new DistinguishedName(pathwayRef.getElementIdentifier().getIdentifierValue());
        routeQN.appendUnqualifiedName(new RelativeDistinguishedName("PathwayRoute", "Route-" + a + "-" + b + "-" + c));

        PathwayRoute pathwayRoute = new PathwayRoute(routeQN);
        // Using setShortName as surrogate for setName/setOwner if they are missing
        pathwayRoute.setShortName("Route-" + a + "-" + b + "-" + c);

        ElementReference segmentRef = buildPathwayRouteSegment(pathwayRoute.getReference(), a, b, c);
        pathwayRoute.getRouteSegmentSequence().put(1, segmentRef);

        getPathwayCacheService().addPathwayRoute(pathwayRoute);
        return pathwayRoute.getReference();
    }

    private ElementReference buildPathwayRouteSegment(ElementReference routeRef, String a, String b, String c) {
        DistinguishedName segmentQN = new DistinguishedName(routeRef.getElementIdentifier().getIdentifierValue());
        segmentQN.appendUnqualifiedName(new RelativeDistinguishedName("PathwayRouteSegment", "Segment-" + a + "-" + b + "-" + c));

        PathwayRouteSegment segment = new PathwayRouteSegment(segmentQN);
        segment.setShortName("Segment-" + a + "-" + b + "-" + c);

        ElementReference e1 = buildPathwayElement(segment.getReference(), 1, a, b);
        segment.getPathwayElementSequence().put(1, e1);

        ElementReference e2 = buildPathwayElement(segment.getReference(), 2, b, c);
        segment.getPathwayElementSequence().put(2, e2);

        getPathwayCacheService().addPathwayRouteSegment(segment);
        return segment.getReference();
    }

    private ElementReference buildPathwayElement(ElementReference segmentRef, int sequence, String sourceName, String targetName) {
        String elementName = sourceName + "To" + targetName;
        PathwayElement element = new PathwayElement();
        element.setShortName(elementName);

        DistinguishedName elementQN = new DistinguishedName(segmentRef.getElementIdentifier().getIdentifierValue());
        elementQN.appendUnqualifiedName(new RelativeDistinguishedName("PathwayElement", elementName));
        element.setIdentifier(new ElementIdentifier(elementQN));

        // Mock source/target references
        element.setSource(createMockReference("Interface", sourceName));
        element.setTarget(createMockReference("Interface", targetName));

        getPathwayCacheService().addPathwayElement(element);
        return element.getReference();
    }

    private Pathway buildPathway(String name){
        Pathway p = new Pathway(name, "No Documentation");
        return p;
    }

    private ElementReference createMockReference(String type, String name) {
        DistinguishedName qn = new DistinguishedName();
        qn.appendUnqualifiedName(new RelativeDistinguishedName(type, name));
        ElementReference ref = new ElementReference();
        ref.setElementIdentifier(new ElementIdentifier(qn));
        ref.setElementType(type);
        return ref;
    }
}
