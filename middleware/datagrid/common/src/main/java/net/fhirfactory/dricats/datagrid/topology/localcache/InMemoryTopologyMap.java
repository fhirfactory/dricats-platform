package net.fhirfactory.dricats.datagrid.topology.localcache;

import net.fhirfactory.dricats.datagrid.topology.ITopologyMapRepository;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;

import net.fhirfactory.dricats.internals.oam.topology.ApplicationInstanceSummary;
import net.fhirfactory.dricats.internals.oam.topology.SubsystemSummary;
import net.fhirfactory.dricats.internals.oam.topology.WUPGroupSummary;
import net.fhirfactory.dricats.internals.oam.topology.WUPSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository of ApplicationComponentSummary resources.
 * Children are resolved from ApplicationComponentSummary.getSubComponents() which holds DistributableObjectId references.
 */
@ApplicationScoped
public class InMemoryTopologyMap implements ITopologyMapRepository {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryTopologyMap.class);

    private final ConcurrentMap<DistributableObjectId, ApplicationComponentSummary> componentsById = new ConcurrentHashMap<>();

    @Override
    public void add(ApplicationComponentSummary component) {
        if (component == null || component.getObjectID() == null) {
            LOG.warn("Attempt to add null component or component with null ID: {}", component);
            return;
        }
        componentsById.put(component.getObjectID(), component);
    }

    @Override
    public boolean remove(ApplicationComponentSummary component) {
        if (component == null || component.getObjectID() == null) { return false; }
        return removeById(component.getObjectID());
    }

    @Override
    public boolean removeById(DistributableObjectId id) {
        if (id == null) { return false; }
        return componentsById.remove(id) != null;
    }

    @Override
    public Optional<ApplicationComponentSummary> findById(DistributableObjectId id) {
        if (id == null) { return Optional.empty(); }
        return Optional.ofNullable(componentsById.get(id));
    }

    @Override
    public List<ApplicationComponentSummary> findByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) { return Collections.emptyList(); }
        String needle = nameOrFragment.toLowerCase();
        return componentsById.values().stream()
                .filter(ac -> ac.getName() != null && ac.getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponentSummary> search(Predicate<ApplicationComponentSummary> predicate) {
        if (predicate == null) { return Collections.emptyList(); }
        return componentsById.values().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponentSummary> getChildren(ApplicationComponentSummary component) {
        if (component == null) { return Collections.emptyList(); }
        return getChildrenById(component.getObjectID());
    }

    @Override
    public List<ApplicationComponentSummary> getChildrenById(DistributableObjectId id) {
        if (id == null) { return Collections.emptyList(); }
        Optional<ApplicationComponentSummary> parentOpt = findById(id);
        if (parentOpt.isEmpty()) { return Collections.emptyList(); }
        ApplicationComponentSummary parent = parentOpt.get();
        List<ApplicationComponentSummary> children = getChildren(parent.getObjectID());
        return children;
    }

    protected List<ApplicationComponentSummary> getChildren(DistributableObjectId id){
        if (id == null) { return Collections.emptyList(); }
        Optional<ApplicationComponentSummary> parentOpt = findById(id);
        if (parentOpt.isEmpty()) { return Collections.emptyList(); }
        ApplicationComponentSummary parent = parentOpt.get();
        List<DistributableObjectId> resultList = new ArrayList<>();
        if(parent.getSpecialization().contentEquals(SoftwareComponentTypeEnum.SUBSYSTEM.getType())){
            SubsystemSummary subsystem = (SubsystemSummary) parent;
            resultList.addAll(subsystem.getApplicationInstances());
        }
        if(parent.getSpecialization().contentEquals(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType())){
            ApplicationInstanceSummary applicationInstance = (ApplicationInstanceSummary) parent;
            resultList.addAll(applicationInstance.getWupGroups());
        }
        if(parent.getSpecialization().contentEquals(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP.getType())){
            WUPGroupSummary wupGroup = (WUPGroupSummary) parent;
            resultList.addAll(wupGroup.getWorkUnitProcessors());
        }
        return( Collections.emptyList());
    }

    @Override
    public Collection<ApplicationComponentSummary> getAll() {
        return Collections.unmodifiableCollection(componentsById.values());
    }

    @Override
    public int size() { return componentsById.size(); }

    @Override
    public void clear() { componentsById.clear(); }
}
