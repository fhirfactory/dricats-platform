package net.fhirfactory.dricats.internals.pathways;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.SimpleDistributableObject;
import net.fhirfactory.dricats.internals.pathways.valuesets.ProcessingPathSegmentDistributionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PathSegment extends SimpleDistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(PathSegment.class);

    //
    // Attributes
    //
     // Map<priority, pathway segment path id>
    private Map<Integer, DistributableObjectId> pathOptions;
    private ProcessingPathSegmentDistributionEnum distributionPattern;

    //
    // Constructor(s)
    //
    public PathSegment(){
        super();
        pathOptions = new HashMap<>()      ;
        distributionPattern = ProcessingPathSegmentDistributionEnum.DISTRIBUTE_RANDOM;
    }

    //
    // Getters and Setters
    //
    public Map<Integer, DistributableObjectId> getPathOptions() {
        return pathOptions;
    }

    public void setPathOptions(Map<Integer, DistributableObjectId> pathOptions) {
        this.pathOptions = pathOptions;
    }

    public ProcessingPathSegmentDistributionEnum getDistributionPattern() {
        return distributionPattern;
    }

    public void setDistributionPattern(ProcessingPathSegmentDistributionEnum distributionPattern) {
        this.distributionPattern = distributionPattern;
    }
}
