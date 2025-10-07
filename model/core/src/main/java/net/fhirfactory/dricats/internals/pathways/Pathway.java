/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.pathways;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.pathways.valuesets.PathwayRouteSelectionCriteriaEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationProcess;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

public class Pathway extends ApplicationProcess {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900003L;
    private static final Logger LOG = LoggerFactory.getLogger(Pathway.class);

    //
     // Attributes
    //
     // Map<Priority, PathwayRoute ID>
    private Map<Integer, DistributableObjectId> possiblePathwayRoutes;
    private PathwayRouteSelectionCriteriaEnum routeSelectionCriteria;

    //
     // Constructor(s)
    //
    public Pathway(){
        super();
        possiblePathwayRoutes = new HashMap<>();
        routeSelectionCriteria = PathwayRouteSelectionCriteriaEnum.DISTRIBUTE_RANDOM;

    }

    //
    // Getters and Setters
    //

    public Map<Integer, DistributableObjectId> getPossiblePathwayRoutes() {
        return possiblePathwayRoutes;
    }

    public void setPossiblePathwayRoutes(Map<Integer, DistributableObjectId> possiblePathwayRoutes) {
        this.possiblePathwayRoutes = possiblePathwayRoutes;
    }

    public PathwayRouteSelectionCriteriaEnum getRouteSelectionCriteria() {
        return routeSelectionCriteria;
    }

    public void setRouteSelectionCriteria(PathwayRouteSelectionCriteriaEnum routeSelectionCriteria) {
        this.routeSelectionCriteria = routeSelectionCriteria;
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("possiblePathwayRoutes", possiblePathwayRoutes)
                .append("routeSelectionCriteria", routeSelectionCriteria)
                .appendSuper(super.toString())
                .toString();
    }
}
