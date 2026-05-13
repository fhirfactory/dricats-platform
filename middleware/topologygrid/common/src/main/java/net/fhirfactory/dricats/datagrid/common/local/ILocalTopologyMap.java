package net.fhirfactory.dricats.datagrid.common.local;

import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Simple repository contract for an in-memory store of ApplicationComponent resources.
 */
public interface ILocalTopologyMap {
    void add(ElementBase element);
    boolean remove(ElementBase element);
    boolean removeByElementInstanceKey(String key);
    boolean removeByElementKey(String key);

    Optional<ApplicationComponent> findApplicationByElementInstanceKey(String elementInstanceKey);
    Optional<IngresApplicationInterface> findIngresInterfaceByElementInstanceKey(String elementInstanceKey);
    Optional<EgressApplicationInterface> findEgressInterfaceByElementInstance(String elementInstanceKey);

    /**
     * Find components whose name equals or contains the provided name fragment (case-insensitive).
     */
    List<ApplicationComponent> findApplicationComponentByName(String nameOrFragment);
    List<IngresApplicationInterface> findIngresInterfaceByName(String nameOrFragment);
    List<EgressApplicationInterface> findEgressInterfaceByName(String nameOrFragment);

    /**
     * Generic search using a predicate.
     */
    List<ApplicationComponent> search(Predicate<ApplicationComponent> predicate);

    /**
     * Get direct children of the given component, as indicated by its subComponents list.
     */
    List<ApplicationComponent> getChildrenApplicationComponents(ApplicationComponent component);
    List<ApplicationComponent> getChildrenApplicationComponentsByInstanceKey(String applicationComponentInstanceKey);
    List<ApplicationComponent> getChildrenApplicationComponentsByIdentifierKey(String applicationComoponentIdentifierKey);
    List<IngresApplicationInterface> getIngresInterfaces(ApplicationComponent component);
    List<IngresApplicationInterface> getIngresInterfaces(String applicationComponentInstanceKey);
    List<EgressApplicationInterface> getEgressInterfaces(ApplicationComponent component);
    List<EgressApplicationInterface> getEgressInterfaces(String applicationComponentInstanceKey);

    /**
     * All components currently stored.
     */
    Collection<ApplicationComponent> getAllApplicationComponents();

    /**
     * Total number of components stored.
     */
    int size();

    /**
     * Remove all components.
     */
    void clear();

    /**
     * Change tracking: monotonically increasing version incremented on add/update/remove/clear.
     */
    long getChangeVersion();

    /**
     * Change tracking helper.
     * @param sinceVersion previously observed version
     * @return true if current version is greater than sinceVersion
     */
    default boolean hasChangesSince(long sinceVersion) { return getChangeVersion() > sinceVersion; }

}
