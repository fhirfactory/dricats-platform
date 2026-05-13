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
package net.fhirfactory.dricats.ui.serverside.rest.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.api.exceptions.*;
import net.fhirfactory.dricats.internals.api.status.MethodOutcome;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.brokers.topology.LocalApplicationComponentBroker;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class ApplicationComponentHandler {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationComponentHandler.class);

    //
    // Attributes
    //

    @Inject
    private LocalApplicationComponentBroker resourceBroker;


    //
    // Bean Methods
    //

    protected Logger getLogger() {
        return (LOG);
    }

    protected LocalApplicationComponentBroker getResourceBroker() {
        return (resourceBroker);
    }

    //
    // Business Methods
    //


    public List<ApplicationComponent> getResourceList(@Header("sortBy") String sortBy,
                                                      @Header("sortOrder") String sortOrder,
                                                      @Header("pageSize") String pageSize,
                                                      @Header("page") String page)
            throws ResourcePaginationException, ResourceInvalidSortException, ResourceInvalidSearchException, ResourceSortingException {
        getLogger().debug(".getResourceList(): Entry, sortBy->{}, sortOrder->{}, pageSize->{}, page->{}", sortBy, sortOrder, pageSize, page);
        Integer pageSizeValue = null;
        Integer pageValue = null;
        Boolean sortOrderValue = true;
        if (pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if (page != null) {
            pageValue = Integer.valueOf(page);
        }
        if (sortOrder != null) {
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        if (sortBy == null) {
            sortBy = "ObjectId";
        }
        MethodOutcome outcome = getResourceBroker().getPaginatedSortedDirectoryEntrySet(pageSizeValue, pageValue, sortBy, sortOrderValue);
        List<ApplicationComponent> resultList = extractApplicationComponents(outcome);
        getLogger().debug(".defaultGetResourceList(): Exit");
        return (resultList);
    }

    //
    // Review (Search via Identifier)
    //

    protected MethodOutcome identifierBasedSearch(ElementIdentifier identifier) throws ResourceInvalidSearchException {
        getLogger().debug(".doEntrySearch(): Entry, value->{}", identifier.getIdentifierValue());
        throw (new UnsupportedOperationException("Not yet implemented"));
        // getLogger().debug(".doEntrySearch(): Exit");
        // return(new MethodOutcome());
    }

    //
    // Review (General Search)
    //

    public List<ApplicationComponent> defaultSearch(@Header("unqualifiedName") String unqualifiedName,
                                                    @Header("commonName") String commonName,
                                                    @Header("displayName") String displayName,
                                                    @Header("leafValue") String leafValue,
                                                    @Header("sortBy") String sortBy,
                                                    @Header("sortOrder") String sortOrder,
                                                    @Header("pageSize") String pageSize,
                                                    @Header("page") String page)
            throws ResourceNotFoundException, ResourceInvalidSortException, ResourceInvalidSearchException, ResourcePaginationException, ResourceSortingException {
        getLogger().debug(".defaultSearch(): Entry, unqualifiedName->{}, commonName->{}, displayName->{}" +
                        "sortBy->{}, sortOrder->{}, pageSize->{},page->{}",
                unqualifiedName, commonName, displayName, sortBy, sortOrder, pageSize, page);
        String searchAttributeName = null;
        String searchAttributeValue = null;
        if (unqualifiedName != null) {
            searchAttributeValue = unqualifiedName;
            searchAttributeName = "unqualifiedName";
        } else if (commonName != null) {
            searchAttributeValue = commonName;
            searchAttributeName = "commonName";
        } else if (displayName != null) {
            searchAttributeValue = displayName;
            searchAttributeName = "displayName";
        } else if (leafValue != null) {
            searchAttributeValue = leafValue;
            searchAttributeName = "leafValue";
        } else {
            throw (new ResourceInvalidSearchException("Search parameter not specified"));
        }
        Integer pageSizeValue = null;
        Integer pageValue = null;
        Boolean sortOrderValue = true;
        if (pageSize != null) {
            pageSizeValue = Integer.valueOf(pageSize);
        }
        if (page != null) {
            pageValue = Integer.valueOf(page);
        }
        if (sortOrder != null) {
            sortOrderValue = Boolean.valueOf(sortOrder);
        }
        String searchAttributeValueURLDecoded = URLDecoder.decode(searchAttributeValue, StandardCharsets.UTF_8);
        MethodOutcome outcome = getResourceBroker().search(searchAttributeName, searchAttributeValueURLDecoded, pageSizeValue, pageValue, sortBy, sortOrderValue);
        List<ApplicationComponent> results = extractApplicationComponents(outcome);
        getLogger().debug(".defaultSearch(): Exit");
        return (results);
    }


    //
    // Helper Methods
    //

    protected String convertToJson(Object obj)
            throws JsonParseException, JsonProcessingException {
        getLogger().debug(".convertToJson(): Entry");
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.enable(SerializationFeature.INDENT_OUTPUT);
        String result = om.writeValueAsString(obj);
        getLogger().debug(".convertToJson(): Exit, Returning result --> {}", result);
        return (result);
    }

    protected <T extends ElementBase> T convertFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonProcessingException {
        getLogger().debug(".convertFromJson(): Entry");
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        T result = om.readValue(json, clazz);
        getLogger().debug(".convertFromJson(): Exit, Returning result --> {}", result);
        return (result);
    }

    protected List<ApplicationComponent> extractApplicationComponents(MethodOutcome outcome) {
        getLogger().debug(".extractApplicationComponents(): Entry");
        List<ApplicationComponent> resultList = new java.util.ArrayList<>();
        for (ElementBase currentEntry : outcome.getSearchResult()) {
            if (currentEntry instanceof ApplicationComponent) {
                resultList.add((ApplicationComponent) currentEntry);
            }
        }
        getLogger().debug(".extractApplicationComponents(): Exit, Returning {} components", resultList.size());
        return (resultList);
    }
}
