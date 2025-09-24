package net.fhirfactory.dricats.middleware.oam.satellite;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Simple repository contract for an in-memory store of ApplicationComponent resources.
 */
public interface ILocalApplicationComponentMap {
    void add(ApplicationComponent component);
    boolean remove(ApplicationComponent component);
    boolean removeById(DistributableObjectId id);

    Optional<ApplicationComponent> findById(DistributableObjectId id);

    /**
     * Find components whose name equals or contains the provided name fragment (case-insensitive).
     */
    List<ApplicationComponent> findByName(String nameOrFragment);

    /**
     * Generic search using a predicate.
     */
    List<ApplicationComponent> search(Predicate<ApplicationComponent> predicate);

    /**
     * Get direct children of the given component, as indicated by its subComponents list.
     */
    List<ApplicationComponent> getChildren(ApplicationComponent component);

    /**
     * Get direct children for the component identified by id.
     */
    List<ApplicationComponent> getChildrenById(DistributableObjectId id);

    /**
     * All components currently stored.
     */
    Collection<ApplicationComponent> getAll();

    /**
     * Total number of components stored.
     */
    int size();

    /**
     * Remove all components.
     */
    void clear();
}
