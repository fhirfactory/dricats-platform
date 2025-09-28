/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiSiteConfigurationObject implements Serializable {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
     // Attributes
    //
    private Integer siteCount;
    private List<String> siteNames;

    //
     // Constructor(s)
    //
    public MultiSiteConfigurationObject(){
        this.siteCount = 0;
        this.siteNames = new ArrayList<>();
    }

    //
     // Bean Methods
    //

    public Integer getSiteCount() {
        return siteCount;
    }

    public void setSiteCount(Integer siteCount) {
        this.siteCount = siteCount;
    }

    public List<String> getSiteNames() {
        return siteNames;
    }

    public void setSiteNames(List<String> siteNames) {
        this.siteNames = siteNames;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("siteCount", siteCount)
                .append("siteNames", siteNames)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MultiSiteConfigurationObject that = (MultiSiteConfigurationObject) o;
        return Objects.equals(siteCount, that.siteCount) && Objects.equals(siteNames, that.siteNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteCount, siteNames);
    }
}
