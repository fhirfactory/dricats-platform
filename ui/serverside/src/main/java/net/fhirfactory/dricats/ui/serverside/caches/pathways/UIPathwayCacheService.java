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
package net.fhirfactory.dricats.ui.serverside.caches.pathways;

import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.pathways.Pathway;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import net.fhirfactory.dricats.internals.pathways.PathwayRoute;
import net.fhirfactory.dricats.internals.pathways.PathwayRouteSegment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class UIPathwayCacheService {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UIPathwayCacheService.class);

    //
    // Attributes
    //
    private boolean initialised = false;
    private Map<String, Pathway> pathways = new HashMap<>();


    private Map<String, PathwayRoute> pathwayRoutes = new HashMap<>();
    private Map<String, PathwayRouteSegment> pathwayRouteSegments = new HashMap<>();
    private Map<String, PathwayElement> pathwayElements = new HashMap<>();

    //
    // Constructor(s)
    //

    public UIPathwayCacheService(){
        LOG.debug(".<constructor>(): Entry");
        pathways = new HashMap<>();
        pathwayRoutes = new HashMap<>();
        pathwayRouteSegments = new HashMap<>();
        pathwayElements = new HashMap<>();
        LOG.debug(".<constructor>(): Exit");
    }

    //
     // Bean Methods
    //

    public boolean isInitialised() {
        return initialised;
    }

    public void setInitialised(boolean initialised) {
        this.initialised = initialised;
    }

    public Map<String, Pathway> getPathways() {
        return pathways;
    }
    public Map<String, PathwayRoute> getPathwayRoutes() { return pathwayRoutes; }
    public Map<String, PathwayRouteSegment> getPathwayRouteSegments() { return pathwayRouteSegments; }
    public Map<String, PathwayElement> getPathwayElements() { return pathwayElements; }

    protected Logger getLogger() {
        return LOG;
    }
    //
    // Business Methods
    //

    public boolean hasObjectIdValue(ObjectId objectId){
        if(objectId==null){
            getLogger().debug(".hasObjectIdValue(): Exit, pathway has no ObjectID, returning");
            return(false);
        }
        if(objectId.getFullyDistinguishedName()==null){
            getLogger().debug(".hasObjectIdValue(): Exit, pathway has no QualifiedName, returning");
            return(false);
        }
        if(objectId.getFullyDistinguishedName().getCommonName()==null){
            getLogger().debug(".hasObjectIdValue(): Exit, pathway has no CommonName, returning");
            return(false);
        }
        if(StringUtils.isEmpty(objectId.getFullyDistinguishedName().getCommonName().getValue())){
            getLogger().debug(".hasObjectIdValue(): Exit, pathway has no CommonName.Value, returning");
            return(false);
        }
        return(true);
    }

    public String resolveKeyValue(ObjectId objectId){
        getLogger().debug(".resolveKeyValue(): Entry, objectId -> {}", objectId);
        if(hasObjectIdValue(objectId)){
            String value = objectId.getFullyDistinguishedName().getCommonName().getValue();
            getLogger().debug(".resolveKeyValue(): Exit, objectId has ObjectID etc., returning {}", value);
            return(value);
        }
        getLogger().debug(".resolveKeyValue(): Exit, objectId has no ObjectID etc., returning null");
        return(null);
    }

    public void addPathway(Pathway pathway){
        getLogger().debug(".addPathway(): Entry, pathway -> {}", pathway);
        if(pathway==null){
            getLogger().debug(".addPathway(): Exit, pathway is null, returning");
            return;
        }
        String key = resolveKeyValue(pathway.getObjectId());
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".addPathway(): Exit, pathway has no ObjectID etc., returning");
            return;
        }
        pathways.put(key, pathway);
        getLogger().debug(".addPathway(): Exit, pathway added to cache");
    }

    public void removePathway(Pathway pathway){
        getLogger().debug(".removePathway(): Entry, pathway -> {}", pathway);
        if(pathway==null){
            getLogger().debug(".removePathway(): Exit, pathway is null, returning");
            return;
        }
        String key = resolveKeyValue(pathway.getObjectId());
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".removePathway(): Exit, pathway has no ObjectID etc., returning");
            return;
        }
        pathways.remove(key);
        getLogger().debug(".removePathway(): Exit, pathway removed from cache");
    }

    public void addPathwayRoute(PathwayRoute pathwayRoute){
        getLogger().debug(".addPathwayRoute(): Entry, pathwayRoute -> {}", pathwayRoute);
        if(pathwayRoute==null){
            getLogger().debug(".addPathwayRoute(): Exit, pathwayRoute is null, returning");
            return;
        }
        String key = resolveKeyValue(pathwayRoute.getObjectId());
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".addPathwayRoute(): Exit, pathway has no ObjectID etc., returning");
            return;
        }
        pathwayRoutes.put(key, pathwayRoute);
        getLogger().debug(".addPathwayRoute(): Exit, pathwayRoute added to cache");
    }

    public PathwayRoute getPathwayRouteByIdToken(String key){
        getLogger().debug(".getPathwayRouteByIdToken(): Entry, key -> {}", key);
        if(key==null){
            getLogger().debug(".getPathwayRouteByIdToken(): Exit, idToken is null, returning");
            return(null);
        }
        PathwayRoute pathwayRoute = pathwayRoutes.get(key);
        getLogger().debug(".getPathwayRouteByIdToken(): Exit, pathwayRoute -> {}", pathwayRoute);
        return pathwayRoute;
    }

    public void addPathwayRouteSegment(PathwayRouteSegment pathwayRouteSegment){
        getLogger().debug(".addPathwayRouteSegment(): Entry, pathwayRouteSegment -> {}", pathwayRouteSegment);
        if(pathwayRouteSegment==null){
            getLogger().debug(".addPathwayRouteSegment(): Exit, pathwayRouteSegment is null, returning");
            return;
        }
        String key = resolveKeyValue(pathwayRouteSegment.getObjectId());
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".addPathwayRouteSegment(): Exit, pathwayRouteSegment has no ObjectID etc., returning");
            return;
        }
        pathwayRouteSegments.put(key, pathwayRouteSegment);
        getLogger().debug(".addPathwayRouteSegment(): Exit, pathwayRouteSegment added to cache");
    }

    public PathwayRouteSegment getPathwaySegmentById(String key){
        getLogger().debug(".getPathwaySegmentById(): Entry, key -> {}", key);
        if(key==null){
            getLogger().debug(".getPathwaySegmentById(): Exit, key is null, returning");
            return(null);
        }
        PathwayRouteSegment pathwayRouteSegment = pathwayRouteSegments.get(key);
        getLogger().debug(".getPathwaySegmentById(): Exit, pathwayRouteSegment -> {}",pathwayRouteSegment);
        return pathwayRouteSegment;
    }

    public void addPathwayElement(PathwayElement pathwayElement){
        getLogger().debug(".addPathwayElement(): Entry, pathwayElement -> {}", pathwayElement);
        if(pathwayElement==null){
            getLogger().debug(".addPathwayElement(): Exit, pathwayElement is null, returning");
            return;
        }
        String key = resolveKeyValue(pathwayElement.getObjectId());
        if(StringUtils.isEmpty(key)){
            getLogger().debug(".addPathwayElement(): Exit, pathwayElement has no ObjectID etc., returning");
            return;
        }
        getLogger().debug(".addPathwayElement(): Exit, pathwayElement added to cache");
        pathwayElements.put(key, pathwayElement);
    }

    public PathwayElement getPathwayElementById(String key){
        getLogger().debug(".getPathwayElementById(): Entry, key -> {}", key);
        if(key==null){
            getLogger().debug(".getPathwayElementById(): Exit, key is null, returning");
            return(null);
        }
        PathwayElement pathwayElement = pathwayElements.get(key);
        getLogger().debug(".getPathwayElementById(): Exit, pathwayElement -> {}", pathwayElement);
        return pathwayElement;
    }
}
