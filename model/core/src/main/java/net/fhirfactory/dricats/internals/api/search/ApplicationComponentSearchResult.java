/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.api.search;

import net.fhirfactory.dricats.internals.api.exceptions.ResourceFilteringException;
import net.fhirfactory.dricats.internals.api.exceptions.ResourceSortingException;
import net.fhirfactory.dricats.internals.api.search.base.SearchResult;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;

public class ApplicationComponentSearchResult extends SearchResult implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponentSearchResult.class);

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    @Override
    public SearchResult filterBy(String attributeName, String attributeValue) throws ResourceFilteringException {
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}", attributeName, attributeValue);
        ApplicationComponentSearchResult result = (ApplicationComponentSearchResult) filterBy(attributeName, attributeValue, true);
        getLogger().debug(".filterBy(): Exit");
        return(result);
    }

    @Override
    public SearchResult filterBy(String attributeName, String attributeValue, boolean isInclusive) throws ResourceFilteringException {
        getLogger().debug(".filterBy(): Entry, attributeName->{}, attributeValue->{}, isInclusive->{}", attributeName, attributeValue, isInclusive);
        ApplicationComponentSearchResult result = (ApplicationComponentSearchResult)instatiateNewSearchResult();

        return(result);
    }

    @Override
    public SearchResult sortBy(String attributeName) throws ResourceSortingException {
        getLogger().debug(".sortBy(): Entry, attributeName->{}, attributeValue->{}", attributeName);
        ApplicationComponentSearchResult result = (ApplicationComponentSearchResult) sortBy(attributeName, true);
        getLogger().debug(".sortBy(): Exit");
        return(result);
    }

    @Override
    public SearchResult sortBy(String attributeName, boolean ascendingOrder) throws ResourceSortingException{
        getLogger().debug(".sortBy(): Entry, attributeName->{}, ascendingOrder->{}", attributeName, ascendingOrder);
        if(attributeName == null){
            attributeName = "simplifiedID";
        }
        ApplicationComponentSearchResult result = (ApplicationComponentSearchResult)instatiateNewSearchResult();
        result.getSearchResultList().addAll(getSearchResultList());
        String sortByLowerCase = attributeName.toLowerCase();
        switch(sortByLowerCase){
            case "objectId" : {
                Collections.sort(result.getSearchResultList(), ElementBase.objectIdComparator);
                break;
            }
            case "name" :{
                Collections.sort(result.getSearchResultList(), ElementBase.nameComparator);
                break;
            }
            case "extension" :{
                Collections.sort(result.getSearchResultList(), ElementBase.extensionComparator);
                break;
            }
            case "commonname" :{
                Collections.sort(result.getSearchResultList(), ElementBase.commonNameComparator);
                break;
            }
            default:{
                Collections.sort(result.getSearchResultList(), ElementBase.objectIdComparator);
            }
        }
        if(!ascendingOrder){
            reverseSortOrder(getSearchResultList());
        }
        return(result);
    }

    @Override
    protected SearchResult instatiateNewSearchResult() {
        ApplicationComponentSearchResult newCareTeamSearchResult = new ApplicationComponentSearchResult();
        return (newCareTeamSearchResult);
    }
}
