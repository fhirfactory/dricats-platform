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
package net.fhirfactory.dricats.ui.uitest.handlers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.IdToken;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.ui.serverside.pathways.UIPathwayCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.common.BaseHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class TestPathwayServices extends BaseHandler {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TestPathwayServices.class);

    //
     // Attributes
    //
    @Inject
    private UIPathwayCacheService pathwayCacheService;

    //
     // Constructor(s)
    //
    public TestPathwayServices(){
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
            LOG.warn(".initialise(): Failed to pre-populate test pathways: {}", e.getMessage());
        }
        LOG.debug(".initialise(): Exit");
    }

    //
     // Getters and Setters
    //

    protected UIPathwayCacheService getPathwayCacheService(){
        return pathwayCacheService;
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    //
     // Business Methods
    //

    public String listPathwaysAsJSON() {
        LOG.debug(".listPathwaysAsJSON(): Entry");
        List<Pathway> list = new ArrayList<>();
        list.addAll(getPathwayCacheService().getPathways().values());
        LOG.info(".listPathwaysAsJSON(): Exit, Returning {} components", list.size());
        return(convertToJson(list));
    }

    public String listPathwayElementsAsJSON() {
        LOG.debug(".listPathwayElementsAsJSON(): Entry");
        java.util.List<net.fhirfactory.dricats.internals.pathways.PathwayElement> list = new java.util.ArrayList<>();
        list.addAll(getPathwayCacheService().getPathwayElements().values());
        LOG.info(".listPathwayElementsAsJSON(): Exit, Returning {} elements", list.size());
        return convertToJson(list);
    }

    public Pathway getPathway(String key) {
        LOG.debug(".getPathway(): Entry, key={}", key);
        if (StringUtils.isEmpty(key)) {
            LOG.warn(".getPathway(), Exit, called with empty key");
            return null;
        }
        Pathway c = getPathwayCacheService().getPathways().get(key);
        if (c == null) {
            LOG.warn(".getPathway(): Pathway not found for key={}", key);
        }
        LOG.debug(".getPathway(): Exit, Returning Pathway --> {}", c);
        return c;
    }

    public String getPathwayAsJSON(String key) {
        LOG.debug(".getPathwayAsJSON(): Entry, key={}", key);
        Pathway c = getPathway(key);
        String result = convertToJson(c);
        LOG.debug(".getPathwayAsJSON(): Exit, Returning Pathway --> {}", result);
        return (result);
    }

    public List<PathwayRoute> getPathwayRoutes(String key) {
        LOG.debug(".getSegments(): Entry, key -> {}", key);

        Pathway pathway = getPathwayCacheService().getPathways().get(key);
        if(pathway == null){
            LOG.warn(".getSegments(): Pathway not found for key={}", key);
            return new ArrayList<>();
        }
        List<PathwayRoute> pathwayRoutes = new ArrayList<>();
        for (net.fhirfactory.dricats.internals.common.DistributableObjectId segId : pathway.getPossiblePathwayRoutes().values()) {
            if (segId != null) {
                PathwayRoute s = getPathwayCacheService().getPathwayRouteByIdToken(segId.getQualifiedName().getCommonName().getValue());
                if (s != null) { pathwayRoutes.add(s); }
            }
        }
        LOG.debug(".getSegments(): Exit, Returning {} Pathway Segments", pathwayRoutes.size());
        return pathwayRoutes;
    }

    public String getPathwayRoutesAsJSON(String key){
        LOG.debug(".getPathwayRoutesAsJSON(): Entry, key -> {}", key);
        List<PathwayRoute> segments = getPathwayRoutes(key);
        String result = convertToJson(segments);
        LOG.debug(".getPathwayRoutesAsJSON(): Exit, Returning Pathway --> {}", result);
        return (result);
    }

    public PathwayRoute getPathwayRoute(String key){
        getLogger().debug(".getPathwayRoute(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayRoute(): Exit, key is null or blank, returning null");
            return(null); }
        PathwayRoute pathwayRoute= getPathwayCacheService().getPathwayRouteByIdToken(key);
        getLogger().debug(".getPathwayRoute(): Exit, Returning PathwayRoute --> {}", pathwayRoute);
        return (pathwayRoute) ;
    }

    public String getPathwayRouteAsJSON(String key){
        getLogger().debug(".getPathwayRouteAsJSON(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayRouteAsJSON(): Exit, id is null or blank, returning null");
            return(null);
        }
        String result = convertToJson(getPathwayRoute(key));
        getLogger().debug(".getPathwayRouteAsJSON(): Exit, Returning PathwayRoute --> {}", result);
        return result;
    }

    public PathwayRouteSegment getPathwayRouteSegment(String key){
        getLogger().debug(".getPathwayRouteSegment(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayRouteSegment(): Exit, key is null or blank, returning null");
            return null;
        }
        PathwayRouteSegment pathwaySegment = getPathwayCacheService().getPathwaySegmentById(key);
        getLogger().debug(".getPathwayRouteSegment(): Exit, Returning PathwayRouteSegment --> {}", pathwaySegment);
        return pathwaySegment;
    }

    public String getPathwayRouteSegmentAsJSON(String key){
        getLogger().debug(".getPathwayRouteSegmentAsJSON(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayRouteSegmentAsJSON(): Exit, key is null or blank, returning null");
            return null;
        }
        PathwayRouteSegment pathwayRouteSegment = getPathwayRouteSegment(key);
        String result = convertToJson(pathwayRouteSegment);
        getLogger().debug(".getPathwayRouteSegmentAsJSON(): Exit, Returning result --> {}", result);
        return result;
    }

    public PathwayElement getPathwayElement(String key){
        LOG.debug(".getPathwayElement(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayElement(): Exit, key is null or blank, returning null");
            return null;
        }
        PathwayElement pathwayElement = getPathwayCacheService().getPathwayElementById(key);
        getLogger().debug(".getPathwayElement(): Exit, Returning PathwayElement --> {}", pathwayElement);
        return pathwayElement;
    }

    public String getPathwayElementAsJSON(String key){
        LOG.debug(".getPathwayElementAsJSON(): Entry, key -> {}", key);
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".getPathwayElementAsJSON(): Exit, key is null or blank, returning null");
            return null;
        }
        String result = convertToJson(getPathwayElement(key));
        getLogger().debug(".getPathwayElementAsJSON(): Exit, Returning result --> {}", result);
        return result;
    }

    // ----------------- CRUD METHODS -----------------
    public String createOrUpdatePathway(String pathwayJson){
        LOG.debug(".createOrUpdatePathway(): Entry body={}", pathwayJson);
        if(StringUtils.isBlank(pathwayJson)){ return null; }
        try{
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            net.fhirfactory.dricats.internals.pathways.Pathway p = om.readValue(pathwayJson, net.fhirfactory.dricats.internals.pathways.Pathway.class);
            if(p==null || p.getObjectID()==null){ return null; }
            getPathwayCacheService().getPathways().put(getPathwayCacheService().resolveKeyValue(p.getObjectID()), p);
            return convertToJson(p);
        } catch (Exception e){
            LOG.warn(".createOrUpdatePathway(): failed: {}", e.toString());
            return null;
        }
    }

    public void deletePathway(String id){
        LOG.debug(".deletePathway(): Entry id={}", id);
        if(StringUtils.isBlank(id)){ return; }
        net.fhirfactory.dricats.internals.pathways.Pathway p = getPathwayCacheService().getPathways().remove(id);
        if(p!=null && p.getPossiblePathwayRoutes()!=null){
            // Remove linked routes
            for (java.util.Map.Entry<Integer, DistributableObjectId> e : p.getPossiblePathwayRoutes().entrySet()){
                if(e.getValue()!=null){ deletePathwayRoute(getPathwayCacheService().resolveKeyValue(e.getValue())); }
            }
        }
    }

    public String createPathwayRoute(String pathwayId, String routeJson){
        LOG.debug(".createPathwayRoute(): Entry pathwayId={} body={} ", pathwayId, routeJson);
        if(StringUtils.isBlank(pathwayId) || StringUtils.isBlank(routeJson)){ return null; }
        try{
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            PathwayRoute r = om.readValue(routeJson, PathwayRoute.class);
            if(r==null || r.getObjectID()==null){ return null; }
            String rKey = getPathwayCacheService().resolveKeyValue(r.getObjectID());
            getPathwayCacheService().getPathwayRoutes().put(rKey, r);
            // link to pathway
            Pathway p = getPathway(pathwayId);
            if(p!=null){
                int next = 1;
                if(p.getPossiblePathwayRoutes()!=null && !p.getPossiblePathwayRoutes().isEmpty()){
                    next = new java.util.ArrayList<>(p.getPossiblePathwayRoutes().keySet()).stream().max(Integer::compareTo).orElse(0) + 1;
                }
                p.getPossiblePathwayRoutes().put(next, r.getObjectID());
            }
            return convertToJson(r);
        } catch (Exception e){
            LOG.warn(".createPathwayRoute(): failed: {}", e.toString());
            return null;
        }
    }

    public String createOrUpdatePathwayRoute(String routeJson){
        LOG.debug(".createOrUpdatePathwayRoute(): Entry body={}", routeJson);
        if(StringUtils.isBlank(routeJson)){ return null; }
        try{
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            PathwayRoute r = om.readValue(routeJson, PathwayRoute.class);
            if(r==null || r.getObjectID()==null){ return null; }
            String rKey = getPathwayCacheService().resolveKeyValue(r.getObjectID());
            getPathwayCacheService().getPathwayRoutes().put(rKey, r);
            return convertToJson(r);
        } catch (Exception e){
            LOG.warn(".createOrUpdatePathwayRoute(): failed: {}", e.toString());
            return null;
        }
    }

    public void deletePathwayRoute(String id){
        LOG.debug(".deletePathwayRoute(): Entry id={}", id);
        if(StringUtils.isBlank(id)){ return; }
        PathwayRoute r = getPathwayCacheService().getPathwayRoutes().remove(id);
        if(r!=null && r.getRouteSegmentSequence()!=null){
            for(java.util.Map.Entry<Integer, DistributableObjectId> e : r.getRouteSegmentSequence().entrySet()){
                if(e.getValue()!=null){ deletePathwayRouteSegment(getPathwayCacheService().resolveKeyValue(e.getValue())); }
            }
        }
        // unlink from any pathway
        for(Pathway p : getPathwayCacheService().getPathways().values()){
            if(p.getPossiblePathwayRoutes()==null){ continue; }
            p.getPossiblePathwayRoutes().values().removeIf(oid -> {
                String key = getPathwayCacheService().resolveKeyValue(oid);
                return id.equals(key);
            });
        }
    }

    public String createOrUpdatePathwayRouteSegment(String segmentJson){
        LOG.debug(".createOrUpdatePathwayRouteSegment(): Entry body={}", segmentJson);
        if(StringUtils.isBlank(segmentJson)){ return null; }
        try{
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            PathwayRouteSegment s = om.readValue(segmentJson, PathwayRouteSegment.class);
            if(s==null || s.getObjectID()==null){ return null; }
            String sKey = getPathwayCacheService().resolveKeyValue(s.getObjectID());
            getPathwayCacheService().getPathwayRouteSegments().put(sKey, s);
            return convertToJson(s);
        } catch (Exception e){
            LOG.warn(".createOrUpdatePathwayRouteSegment(): failed: {}", e.toString());
            return null;
        }
    }

    public void deletePathwayRouteSegment(String id){
        LOG.debug(".deletePathwayRouteSegment(): Entry id={}", id);
        if(StringUtils.isBlank(id)){ return; }
        getPathwayCacheService().getPathwayRouteSegments().remove(id);
        // unlink from any route
        for(PathwayRoute r : getPathwayCacheService().getPathwayRoutes().values()){
            if(r.getRouteSegmentSequence()==null){ continue; }
            r.getRouteSegmentSequence().values().removeIf(oid -> {
                String key = getPathwayCacheService().resolveKeyValue(oid);
                return id.equals(key);
            });
        }
    }

    /**
     * Create and register some simple test pathways in the cache.
     */
    public void createTestPathways(){
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
}
