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

public class PathwayRouteSegment extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PathwayRouteSegment.class);

    //
    // Attributes
    //
     // Map<Sequence Number, PathwayElement ID>
    private Map<Integer, DistributableObjectId> pathwayElementSequence;

    //
    // Constructor(s)
    //
    public PathwayRouteSegment(){
        super();
        pathwayElementSequence = new HashMap<>()      ;
    }

    public PathwayRouteSegment(QualifiedName qualifiedName){
        super(qualifiedName);
        pathwayElementSequence = new HashMap<>()      ;
    }

    //
    // Getters and Setters
    //
    public Map<Integer, DistributableObjectId> getPathwayElementSequence() {
        return pathwayElementSequence;
    }

    public void setPathwayElementSequence(Map<Integer, DistributableObjectId> pathwayElementSequence) {
        this.pathwayElementSequence = pathwayElementSequence;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pathElementSequence", pathwayElementSequence)
                .appendSuper(super.toString())
                .toString();
    }
}
