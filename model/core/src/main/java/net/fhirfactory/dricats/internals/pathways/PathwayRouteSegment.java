package net.fhirfactory.dricats.internals.pathways;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.SimpleDistributableObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PathSegmentRoute extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PathSegmentRoute.class);

    //
    // Attributes
    //
     // Map<Sequence Number, Pathway Segment Path Element ID>
    private Map<Integer, DistributableObjectId> pathElementSequence;

    //
    // Constructor(s)
    //
    public PathSegmentRoute(){
        super();
        pathElementSequence = new HashMap<>()      ;
    }

    //
    // Getters and Setters
    //
    public Map<Integer, DistributableObjectId> getPathElementSequence() {
        return pathElementSequence;
    }

    public void setPathElementSequence(Map<Integer, DistributableObjectId> pathElementSequence) {
        this.pathElementSequence = pathElementSequence;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pathElementSequence", pathElementSequence)
                .toString();
    }
}
