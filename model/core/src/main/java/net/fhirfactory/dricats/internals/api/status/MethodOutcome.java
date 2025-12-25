/*
 * Copyright (c) 2021 Mark Hunter
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
package net.fhirfactory.dricats.internals.api.status;

import net.fhirfactory.dricats.internals.api.valuesets.MethodOutcomeEnum;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MethodOutcome implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900002L;

    //
    // Attributes
    //
    private String id;
    private boolean created;
    private ElementBase entry;
    private MethodOutcomeEnum status;
    private String statusReason;

    private boolean search;
    private List<ElementBase> searchResult;
    private boolean searchSuccessful;

    //
    // Getters and Setters
    //

    public boolean isSearch() {
        return search;
    }

    public void setSearch(boolean search) {
        this.search = search;
    }

    public MethodOutcome() {
        this.searchResult = new ArrayList<>();
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public ElementBase getEntry() {
        return entry;
    }

    public void setEntry(ElementBase entry) {
        this.entry = entry;
    }

    public MethodOutcomeEnum getStatus() {
        return status;
    }

    public void setStatus(MethodOutcomeEnum status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public List<ElementBase> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(List<ElementBase> searchResult) {
        this.searchResult = searchResult;
    }

    public boolean isSearchSuccessful() {
        return searchSuccessful;
    }

    public void setSearchSuccessful(boolean searchSuccessful) {
        this.searchSuccessful = searchSuccessful;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .append("created", isCreated())
                .append("entry", getEntry())
                .append("status", getStatus())
                .append("statusReason", getStatusReason())
                .append("search", isSearch())
                .append("searchResult", getSearchResult())
                .append("searchSuccessful", isSearchSuccessful())
                .toString();
    }
}
