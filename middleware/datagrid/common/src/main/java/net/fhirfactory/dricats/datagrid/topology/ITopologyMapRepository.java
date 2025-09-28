package net.fhirfactory.dricats.datagrid.topology;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Simple repository contract for an in-memory store of ApplicationComponent resources.
 */
public interface ITopologyMapRepository {
    void add(ApplicationComponentSummary component);
    boolean remove(ApplicationComponentSummary component);
    boolean removeById(DistributableObjectId id);

    Optional<ApplicationComponentSummary> findById(DistributableObjectId id);

    /**
     * Find components whose name equals or contains the provided name fragment (case-insensitive).
     */
    List<ApplicationComponentSummary> findByName(String nameOrFragment);

    /**
     * Generic search using a predicate.
     */
    List<ApplicationComponentSummary> search(Predicate<ApplicationComponentSummary> predicate);

    /**
     * Get direct children of the given component, as indicated by its subComponents list.
     */
    List<ApplicationComponentSummary> getChildren(ApplicationComponentSummary component);

    /**
     * Get direct children for the component identified by id.
     */
    List<ApplicationComponentSummary> getChildrenById(DistributableObjectId id);

    /**
     * All components currently stored.
     */
    Collection<ApplicationComponentSummary> getAll();

    /**
     * Total number of components stored.
     */
    int size();

    /**
     * Remove all components.
     */
    void clear();
}
