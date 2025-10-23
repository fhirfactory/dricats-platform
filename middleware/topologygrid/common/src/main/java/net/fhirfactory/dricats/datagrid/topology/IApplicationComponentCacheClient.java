package net.fhirfactory.dricats.datagrid.topology;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;

import java.util.List;

public interface IApplicationComponentCacheClient {

    List<ApplicationComponentSummary> getContainedComponents(DistributableObjectId parent, SoftwareComponentTypeEnum componentType);

    ApplicationComponentSummary getSolutionComponent();

    void put(ApplicationComponentSummary item);

    ApplicationComponentSummary get(String key);

    ApplicationComponentSummary remove(String key);

    boolean contains(String key);

    boolean containsOrLoad(String key);

    String resolveKey(ApplicationComponentSummary item);
}
