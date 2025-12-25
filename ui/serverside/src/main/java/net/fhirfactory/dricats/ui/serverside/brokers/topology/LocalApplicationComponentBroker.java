/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.ui.serverside.brokers.topology;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.api.exceptions.*;
import net.fhirfactory.dricats.internals.api.status.MethodOutcome;
import net.fhirfactory.dricats.internals.api.valuesets.MethodOutcomeEnum;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class LocalApplicationComponentBroker  {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(LocalApplicationComponentBroker.class);

    //
    // Attributes
    //
    private boolean initialised = false;


    @Inject
    UITopologyCacheService topologyCacheService;

    //
    // Constructor(s)
    //

    public LocalApplicationComponentBroker() {
        super();
    }

    //
    // Lifecycle Methods
    //

    @PostConstruct
    public void initialise() {
        getLogger().debug(".initialise(): Entry");
        if (!initialised) {
            initialised = true;
        }
        getLogger().debug(".initialise(): Exit");
    }

    //
    // Accessor(s) / Mutator(s)
    //

    protected Logger getLogger(){
        return(LOG);
    }
    

    protected UITopologyCacheService getTopologyCacheService() {
        return topologyCacheService;
    }

    //
    // Business Methods
    //
    
    public MethodOutcome getResource(String recordID) throws ResourceInvalidSearchException {
        getLogger().debug(".getResource(): Entry, recordID --> {}", recordID);
        MethodOutcome outcome = new MethodOutcome();
        ElementBase entry = getTopologyCacheService().getComponent(recordID);
        if(entry == null){
            outcome.setStatus(MethodOutcomeEnum.REVIEW_ENTRY_NOT_FOUND);
            outcome.setId(recordID);
        } else {
            outcome.setEntry(entry);
            outcome.setStatus(MethodOutcomeEnum.REVIEW_ENTRY_FOUND);
            outcome.setId(entry.getLocalId().getValue());
        }
        getLogger().debug(".getResource(): Exit");
        return(outcome);
    }

    public boolean hasEntry(String simplifiedID){
        boolean entryExists = getTopologyCacheService().hasEntry(simplifiedID);
        return(entryExists);
    }

    public MethodOutcome searchUsingIdentifier(ElementIdentifier identifier) throws ResourceInvalidSearchException {
        getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Entry, identifier --> {}", identifier);
        List<ElementBase> searchOutcome = getTopologyCacheService().getComponentWithIdentifier(identifier);
        if(searchOutcome == null ) {
            searchOutcome = new ArrayList<>();
        }
        MethodOutcome outcome = new MethodOutcome();
        outcome.setStatus(MethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        outcome.setSearchSuccessful(true);
        outcome.setSearch(true);
        outcome.setSearchResult(searchOutcome);
        getLogger().debug(".searchForDirectoryResourceRoleUsingIdentifier(): Exit");
        return(outcome);
    }
    
    public MethodOutcome getPaginatedSortedDirectoryEntrySet(Integer pageSize, Integer page, String sortParameter, Boolean sortOrder)
            throws ResourceInvalidSortException, ResourceSortingException, ResourcePaginationException, ResourceInvalidSearchException{
        getLogger().debug(".getPaginatedSortedDirectoryEntrySet(): Entry, pageSize->{}, page->{}, sortParameter->{}, sortOrder->{}", pageSize, page, sortParameter, sortOrder);
        throw(new UnsupportedOperationException("Not yet implemented"));
    }

    public MethodOutcome search(String searchAttributeName, String searchAttributeValue, Integer pageSize, Integer page, String sortBy, Boolean sortOrder)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ResourcePaginationException, ResourceSortingException {
        getLogger().debug(".searchForESRsUsingAttribute(): Entry, searchAttributeName->{}, searchAttributeValue->{}, pageSize->{}, page->{}, sortBy->{}, sortOrder->{}", searchAttributeName, searchAttributeValue, pageSize, page, sortBy, sortOrder);
        throw(new UnsupportedOperationException("Not yet implemented"));
    }

    public MethodOutcome getChildren(String resourceKey){
        getLogger().debug(".getChildren(): Entry, resourceKey->{}", resourceKey);
        List<ApplicationComponent> subComponents = getTopologyCacheService().getSubComponents(resourceKey);
        MethodOutcome outcome = new MethodOutcome();
        ArrayList<ElementBase> elementBaseList = new ArrayList<>(subComponents);
        outcome.setSearchResult(elementBaseList);
        outcome.setStatus(MethodOutcomeEnum.SEARCH_COMPLETED_SUCCESSFULLY);
        outcome.setSearch(true);
        outcome.setSearchSuccessful(true);
        outcome.setId(resourceKey);
        getLogger().debug(".getChildren(): Exit, outcome --> {}", outcome.getStatus());
        return(outcome);
    }

    //
    // Update
    //

    protected MethodOutcome updateApplicationComponent(ElementBase entry) throws ResourceInvalidSearchException {
        getLogger().debug(".updateDirectoryEntry(): Entry");
        MethodOutcome outcome = new MethodOutcome();
        ElementBase foundResource = null;
        getLogger().trace(".updateDirectoryEntry(): Attempting to retrieve existing Resource");
        String resourceKey = null;
        try {
            resourceKey = entry.resolveKey();
        } catch (Exception ex){
            getLogger().warn(".updateDirectoryEntry(): Unable to resolve key for entry, exception --> {}", ex.getMessage());
            outcome.setStatus(MethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            return(outcome);
        }
        if(resourceKey != null){
            getLogger().trace(".updateDirectoryEntry(): The key is not-Null, so we should be able to retrieve Resource with it");
            getLogger().trace(".updateDirectoryEntry(): Attempting to retrieve PegacornDirectoryEntry for Id --> {}", resourceKey);
            foundResource = getTopologyCacheService().getComponent(resourceKey);
        }
        getLogger().trace(".updatePractitionerEntry(): Check to see if we were able to retrieve existing Resource");
        if(foundResource != null){
            getLogger().trace(".updatePractitionerEntry(): Updating existing entry");
            getTopologyCacheService().addComponent(entry);
            outcome.setId(resourceKey);
            outcome.setEntry(entry);
            outcome.setStatus(MethodOutcomeEnum.UPDATE_ENTRY_SUCCESSFUL);
            return(outcome);
        } else {
            outcome.setId(resourceKey);
            outcome.setEntry(entry);
            outcome.setStatus(MethodOutcomeEnum.UPDATE_ENTRY_INVALID);
            getLogger().debug(".updatePractitionerEntry(): Exit, problem retrieving/updating original entry");
            return (outcome);
        }
    }
}
