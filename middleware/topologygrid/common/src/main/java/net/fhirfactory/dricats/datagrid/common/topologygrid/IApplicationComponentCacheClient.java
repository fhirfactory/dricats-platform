/*
 * Copyright (c) 2025 Mark Hunter
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
package net.fhirfactory.dricats.datagrid.common.topologygrid;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;

import java.util.List;

public interface IApplicationComponentCacheClient {

    List<ApplicationComponent> getSubcomponents(DistributableObjectId parent, ApplicationComponentSpecialisationEnum componentType);

    ApplicationComponent getSolutionComponent();

    void put(ApplicationComponent item);

    ApplicationComponent get(String key);

    ApplicationComponent remove(String key);

    boolean contains(String key);

    boolean containsOrLoad(String key);

    String resolveKey(ApplicationComponent item);

    /**
     * Change tracking: monotonically increasing version incremented on add/update/remove.
     */
    long getChangeVersion();

    /**
     * Change tracking helper.
     */
    default boolean hasChangesSince(long sinceVersion) {
        return getChangeVersion() > sinceVersion;
    }

    List<ApplicationComponent> getChangedApplicationComponents(Long start, Long size);
}
