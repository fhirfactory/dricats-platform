package net.fhirfactory.dricats.internals.pathways;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.SimpleDistributableObject;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PathwayRoute extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PathwayRoute.class);

    //
    // Attributes
    //
     // Map<priority, PathwayRouteSegment id>
    private Map<Integer, DistributableObjectId> routeSegmentSequence;

    //
    // Constructor(s)
    //
    public PathwayRoute(){
        super();
        routeSegmentSequence = new HashMap<>();
    }

    public PathwayRoute(QualifiedName qualifiedName){
        super(qualifiedName);
        routeSegmentSequence = new HashMap<>();
    }

    //
    public Map<Integer, DistributableObjectId> getRouteSegmentSequence() {
        return routeSegmentSequence;
    }

    public void setRouteSegmentSequence(Map<Integer, DistributableObjectId> routeSegmentSequence) {
        this.routeSegmentSequence = routeSegmentSequence;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("segmentSequence", routeSegmentSequence)
                .appendSuper(super.toString())
                .toString();
    }
}
