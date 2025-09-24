package net.fhirfactory.dricats.middleware.oam.satellite;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository of ApplicationComponent resources.
 * Children are resolved from ApplicationComponent.getSubComponents() which holds DistributableObjectId references.
 */
@ApplicationScoped
public class LocalApplicationComponentMap implements ILocalApplicationComponentMap {
    private static final Logger LOG = LoggerFactory.getLogger(LocalApplicationComponentMap.class);

    private final ConcurrentMap<DistributableObjectId, ApplicationComponent> componentsById = new ConcurrentHashMap<>();

    @Override
    public void add(ApplicationComponent component) {
        if (component == null || component.getObjectID() == null) {
            LOG.warn("Attempt to add null component or component with null ID: {}", component);
            return;
        }
        componentsById.put(component.getObjectID(), component);
    }

    @Override
    public boolean remove(ApplicationComponent component) {
        if (component == null || component.getObjectID() == null) { return false; }
        return removeById(component.getObjectID());
    }

    @Override
    public boolean removeById(DistributableObjectId id) {
        if (id == null) { return false; }
        return componentsById.remove(id) != null;
    }

    @Override
    public Optional<ApplicationComponent> findById(DistributableObjectId id) {
        if (id == null) { return Optional.empty(); }
        return Optional.ofNullable(componentsById.get(id));
    }

    @Override
    public List<ApplicationComponent> findByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) { return Collections.emptyList(); }
        String needle = nameOrFragment.toLowerCase();
        return componentsById.values().stream()
                .filter(ac -> ac.getName() != null && ac.getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> search(Predicate<ApplicationComponent> predicate) {
        if (predicate == null) { return Collections.emptyList(); }
        return componentsById.values().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> getChildren(ApplicationComponent component) {
        if (component == null) { return Collections.emptyList(); }
        return getChildrenById(component.getObjectID());
    }

    @Override
    public List<ApplicationComponent> getChildrenById(DistributableObjectId id) {
        if (id == null) { return Collections.emptyList(); }
        Optional<ApplicationComponent> parentOpt = findById(id);
        if (parentOpt.isEmpty()) { return Collections.emptyList(); }
        ApplicationComponent parent = parentOpt.get();
        if (parent.getSubComponents() == null || parent.getSubComponents().isEmpty()) {
            return Collections.emptyList();
        }
        List<ApplicationComponent> children = new ArrayList<>();
        for (DistributableObjectId childId : parent.getSubComponents()) {
            ApplicationComponent child = componentsById.get(childId);
            if (child != null) {
                children.add(child);
            }
        }
        return children;
    }

    @Override
    public Collection<ApplicationComponent> getAll() {
        return Collections.unmodifiableCollection(componentsById.values());
    }

    @Override
    public int size() { return componentsById.size(); }

    @Override
    public void clear() { componentsById.clear(); }
}
