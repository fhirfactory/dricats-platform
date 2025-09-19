package net.fhirfactory.dricats.internals.topology.implementation.relationships.datatypes;

import net.fhirfactory.dricats.internals.topology.implementation.relationships.valuesets.ProcessingPathSegmentDistributionEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProcessingPathwaySegment implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ProcessingPathwaySegment.class);

    //
    // Attributes
    //
    private Map<Integer, ProcessingPathwaySegmentPathway> processingPathwaySegmentPathways;
    private ProcessingPathSegmentDistributionEnum distributionPattern;

    //
    // Constructor(s)
    //
    public ProcessingPathwaySegment(){
        super();
        processingPathwaySegmentPathways = new HashMap<>()      ;
        distributionPattern = ProcessingPathSegmentDistributionEnum.DISTRIBUTE_RANDOM;
    }

    //
    // Getters and Setters
    //
    public Map<Integer, ProcessingPathwaySegmentPathway> getProcessingPathwaySegmentPathways() {
        return processingPathwaySegmentPathways;
    }

    public void setProcessingPathwaySegmentPathways(Map<Integer, ProcessingPathwaySegmentPathway> processingPathwaySegmentPathways) {
        this.processingPathwaySegmentPathways = processingPathwaySegmentPathways;
    }

    public ProcessingPathSegmentDistributionEnum getDistributionPattern() {
        return distributionPattern;
    }

    public void setDistributionPattern(ProcessingPathSegmentDistributionEnum distributionPattern) {
        this.distributionPattern = distributionPattern;
    }
}
