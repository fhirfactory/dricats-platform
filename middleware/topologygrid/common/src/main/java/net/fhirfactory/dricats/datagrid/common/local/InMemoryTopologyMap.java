/*
 * Copyright (c) 2025 Mark Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.datagrid.common.local;

import net.fhirfactory.dricats.internals.common.id.ObjectKey;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository of ApplicationComponentSummary resources.
 * Children are resolved from ApplicationComponentSummary.getSubComponents() which holds DistributableObjectId references.
 */
@ApplicationScoped
public class InMemoryTopologyMap implements ILocalTopologyMap {
    private static final Logger LOG = LoggerFactory.getLogger(InMemoryTopologyMap.class);

    private final ConcurrentMap<ObjectKey, ApplicationComponent> componentsById = new ConcurrentHashMap<>();
    private final AtomicLong changeVersion = new AtomicLong(0);

    @Override
    public void add(ApplicationComponent component) {
        if (component == null || component.getObjectId() == null) {
            LOG.warn("Attempt to add null component or component with null ID: {}", component);
            return;
        }
        componentsById.put(component.getObjectId(), component);
        changeVersion.incrementAndGet();
    }

    @Override
    public boolean remove(ApplicationComponent component) {
        if (component == null || component.getObjectId() == null) {
            return false;
        }
        return removeById(component.getObjectId());
    }

    @Override
    public boolean removeById(ObjectKey id) {
        if (id == null) {
            return false;
        }
        boolean removed = componentsById.remove(id) != null;
        if (removed) {
            changeVersion.incrementAndGet();
        }
        return removed;
    }

    @Override
    public Optional<ApplicationComponent> findById(ObjectKey id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(componentsById.get(id));
    }

    @Override
    public List<ApplicationComponent> findByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) {
            return Collections.emptyList();
        }
        String needle = nameOrFragment.toLowerCase();
        return componentsById.values().stream()
                .filter(ac -> ac.getName() != null && ac.getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> search(Predicate<ApplicationComponent> predicate) {
        if (predicate == null) {
            return Collections.emptyList();
        }
        return componentsById.values().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> getChildren(ApplicationComponent component) {
        if (component == null) {
            return Collections.emptyList();
        }
        return getChildrenById(component.getObjectId());
    }

    @Override
    public List<ApplicationComponent> getChildrenById(ObjectKey id) {
        if (id == null) {
            return Collections.emptyList();
        }
        Optional<ApplicationComponent> parentOpt = findById(id);
        if (parentOpt.isEmpty()) {
            return Collections.emptyList();
        }
        ApplicationComponent parent = parentOpt.get();
        List<ApplicationComponent> children = getChildren(parent.getObjectId());
        return children;
    }

    protected List<ApplicationComponent> getChildren(ObjectKey id) {
        if (id == null) {
            return Collections.emptyList();
        }
        Optional<ApplicationComponent> parentOpt = findById(id);
        if (parentOpt.isEmpty()) {
            return Collections.emptyList();
        }

        ApplicationComponent parent = parentOpt.get();
        List<ApplicationComponent> resultList = new ArrayList<>();
        if (parent.getSubComponents() == null || parent.getSubComponents().isEmpty()) {
            return (Collections.emptyList());
        }
        for (ElementReference childReference : parent.getSubComponents()) {
            if (childReference == null) {
                continue;
            }
            Optional<ApplicationComponent> childOpt = findById(childReference.getLocalObjectId());
            childOpt.ifPresent(resultList::add);

        }
        return (resultList);
    }

    @Override
    public Collection<ApplicationComponent> getAll() {
        return Collections.unmodifiableCollection(componentsById.values());
    }

    @Override
    public int size() {
        return componentsById.size();
    }

    @Override
    public void clear() {
        componentsById.clear();
        changeVersion.incrementAndGet();
    }

    @Override
    public long getChangeVersion() {
        return changeVersion.get();
    }
}
