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

import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.id.ElementInstanceId;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPAdapterBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final ConcurrentMap<String, ElementBase> componentInstances = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<String>> componentByIdentifier = new ConcurrentHashMap<>();
    private final AtomicLong changeVersion = new AtomicLong(0);

    //
    // Business Methods
    //

    @Override
    public void add(ElementBase element) {
        LOG.debug(".add(): [Entry] element -> {}", element);
        if (element == null) {
            LOG.warn("Attempt to add null element");
            return;
        }
        String instanceKey = element.resolveElementInstanceKey();
        if (instanceKey == null) {
            LOG.warn("Attempt to add element with null instance key: {}", element);
            return;
        }
        componentInstances.put(instanceKey, element);
        addComponentInstanceForIdentifier(element);
        changeVersion.incrementAndGet();
        LOG.debug(".add(): [Exit] element -> {}, instanceKey -> {}, changeVersion -> {}", element, instanceKey, changeVersion.get());
    }

    private void addComponentInstanceForIdentifier(ElementBase componentInstance) {
        LOG.debug(".addComponentInstanceForIdentifier(): [Entry] componentInstance -> {}", componentInstance);
        String componentKey = componentInstance.resolveElementKey();
        if (componentKey == null) {
            LOG.debug(".addComponentInstanceForIdentifier(): Exit, componentKey is null for element -> {}", componentInstance);
            return;
        }
        List<String> componentInstanceKeys = componentByIdentifier.get(componentKey);
        if (componentInstanceKeys == null) {
            componentInstanceKeys = new ArrayList<>();
            componentByIdentifier.put(componentKey, componentInstanceKeys);
        }
        if (!componentInstanceKeys.contains(componentInstance.resolveElementInstanceKey())) {
            componentInstanceKeys.add(componentInstance.resolveElementInstanceKey());
        }
        LOG.debug(".addComponentInstanceForIdentifier(): [Exit]");
    }

    @Override
    public boolean remove(ElementBase element) {
        LOG.debug(".remove(): [Entry] element -> {}", element);
        if (element == null) {
            LOG.debug(".remove(): [Exit] element is null, returning false");
            return false;
        }
        boolean b = removeByElementInstanceKey(element.resolveElementInstanceKey());
        LOG.debug(".remove(): [Exit] Success {} status of removal of element -> {}", b, element);
        return (b);
    }

    @Override
    public boolean removeByElementInstanceKey(String key) {
        LOG.debug(".removeByElementInstanceKey(): [Entry] key -> {}", key);
        if (key == null) {
            LOG.debug(".removeByElementInstanceKey(): [Exit] key is null, returning false");
            return false;
        }
        ElementBase removed = componentInstances.remove(key);
        boolean successfulRemoval = false;
        if (removed != null) {
            // Also remove from componentByIdentifier
            String componentKey = removed.resolveElementKey();
            if (componentKey != null) {
                List<String> instanceKeys = componentByIdentifier.get(componentKey);
                if (instanceKeys != null) {
                    instanceKeys.remove(key);
                    if (instanceKeys.isEmpty()) {
                        componentByIdentifier.remove(componentKey);
                    }
                }
            }
            changeVersion.incrementAndGet();
            successfulRemoval = true;
        }
        LOG.debug(".removeByElementInstanceKey(): [Exit] sucessfulRemoval -> {}", successfulRemoval);
        return successfulRemoval;
    }

    @Override
    public boolean removeByElementKey(String key) {
        if (key == null) {
            return false;
        }
        List<String> instanceKeys = componentByIdentifier.get(key);
        if (instanceKeys == null || instanceKeys.isEmpty()) {
            return false;
        }
        List<String> keysToRemove = new ArrayList<>(instanceKeys);
        boolean anyRemoved = false;
        for (String instanceKey : keysToRemove) {
            if (removeByElementInstanceKey(instanceKey)) {
                anyRemoved = true;
            }
        }
        return anyRemoved;
    }

    @Override
    public Optional<ApplicationComponent> findApplicationByElementInstanceKey(String elementInstanceKey) {
        if (elementInstanceKey == null) {
            return Optional.empty();
        }
        ElementBase element = componentInstances.get(elementInstanceKey);
        if (element instanceof ApplicationComponent) {
            return Optional.of((ApplicationComponent) element);
        }
        return Optional.empty();
    }

    @Override
    public Optional<IngresApplicationInterface> findIngresInterfaceByElementInstanceKey(String elementInstanceKey) {
        LOG.info(".findIngresInterfaceByElementInstanceKey(): Entry, elementInstanceKey --> {}", elementInstanceKey);
        if (elementInstanceKey == null) {
            LOG.info(".findIngresInterfaceByElementInstanceKey(): Exit, elementInstanceKey is null");
            return Optional.empty();
        }
        ElementBase element = componentInstances.get(elementInstanceKey);
        if (element instanceof IngresApplicationInterface) {
            LOG.info(".findIngresInterfaceByElementInstanceKey(): Exit, element --> {}", element);
            return Optional.of((IngresApplicationInterface) element);
        }
        LOG.info(".findIngresInterfaceByElementInstanceKey(): Exit, no element found.");
        return Optional.empty();
    }

    @Override
    public Optional<EgressApplicationInterface> findEgressInterfaceByElementInstance(String elementInstanceKey) {
        LOG.info(".findEgressInterfaceByElementInstance(): Entry, elementInstanceKey --> {}", elementInstanceKey);
        if (elementInstanceKey == null) {
            LOG.info(".findEgressInterfaceByElementInstance(): Exit, elementInstanceKey is null");
            return Optional.empty();
        }
        ElementBase element = componentInstances.get(elementInstanceKey);
        if (element instanceof EgressApplicationInterface) {
            LOG.info(".findEgressInterfaceByElementInstance(): Exit, element -> {}", element);
            return Optional.of((EgressApplicationInterface) element);
        }
        LOG.info(".findEgressInterfaceByElementInstance(): Exit, no element found.");
        return Optional.empty();
    }

    @Override
    public List<ApplicationComponent> findApplicationComponentByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) {
            return Collections.emptyList();
        }
        String needle = nameOrFragment.toLowerCase();
        return componentInstances.values().stream()
                .filter(e -> e instanceof ApplicationComponent)
                .map(e -> (ApplicationComponent) e)
                .filter(ac -> ac.getIdentifier() != null && ac.getIdentifier().getIdentifierValue() != null && ac.getIdentifier().getIdentifierValue().getCommonName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<IngresApplicationInterface> findIngresInterfaceByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) {
            return Collections.emptyList();
        }
        String needle = nameOrFragment.toLowerCase();
        return componentInstances.values().stream()
                .filter(e -> e instanceof IngresApplicationInterface)
                .map(e -> (IngresApplicationInterface) e)
                .filter(ac -> ac.getIdentifier() != null && ac.getIdentifier().getIdentifierValue() != null && ac.getIdentifier().getIdentifierValue().getCommonName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<EgressApplicationInterface> findEgressInterfaceByName(String nameOrFragment) {
        if (nameOrFragment == null || nameOrFragment.isBlank()) {
            return Collections.emptyList();
        }
        String needle = nameOrFragment.toLowerCase();
        return componentInstances.values().stream()
                .filter(e -> e instanceof EgressApplicationInterface)
                .map(e -> (EgressApplicationInterface) e)
                .filter(ac -> ac.getIdentifier() != null && ac.getIdentifier().getIdentifierValue() != null && ac.getIdentifier().getIdentifierValue().getCommonName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName() != null && ac.getIdentifier().getIdentifierValue().getCommonName().getName().toLowerCase().contains(needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> search(Predicate<ApplicationComponent> predicate) {
        if (predicate == null) {
            return Collections.emptyList();
        }
        return componentInstances.values().stream()
                .filter(e -> e instanceof ApplicationComponent)
                .map(e -> (ApplicationComponent) e)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationComponent> getChildrenApplicationComponents(ApplicationComponent component) {
        if (component == null) {
            return Collections.emptyList();
        }
        return getChildrenApplicationComponentsByInstanceKey(component.resolveElementInstanceKey());
    }

    @Override
    public List<ApplicationComponent> getChildrenApplicationComponentsByInstanceKey(String applicationComponentInstanceKey) {
        if (applicationComponentInstanceKey == null) {
            return Collections.emptyList();
        }
        ElementBase component = componentInstances.get(applicationComponentInstanceKey);
        if (!(component instanceof ApplicationComponent)) {
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) component;
        List<ApplicationComponent> children = new ArrayList<>();
        if (ac.getSubComponents() != null) {
            for (ElementReference ref : ac.getSubComponents()) {
                if (ref != null && ref.getElementInstanceId() != null) {
                    ElementBase child = componentInstances.get(ref.getElementInstanceId().getIdValue());
                    if (child instanceof ApplicationComponent) {
                        children.add((ApplicationComponent) child);
                    }
                }
            }
        }
        return children;
    }

    @Override
    public List<ApplicationComponent> getChildrenApplicationComponentsByIdentifierKey(String applicationComoponentIdentifierKey) {
        if (applicationComoponentIdentifierKey == null) {
            return Collections.emptyList();
        }
        List<String> instanceKeys = componentByIdentifier.get(applicationComoponentIdentifierKey);
        if (instanceKeys == null) {
            return Collections.emptyList();
        }
        List<ApplicationComponent> allChildren = new ArrayList<>();
        for (String instanceKey : instanceKeys) {
            allChildren.addAll(getChildrenApplicationComponentsByInstanceKey(instanceKey));
        }
        return allChildren;
    }

    @Override
    public List<IngresApplicationInterface> getIngresInterfaces(ApplicationComponent component) {
        if (component == null) {
            return Collections.emptyList();
        }
        return getIngresInterfaces(component.resolveElementInstanceKey());
    }

    @Override
    public List<IngresApplicationInterface> getIngresInterfaces(String applicationComponentInstanceKey) {
        if (applicationComponentInstanceKey == null) {
            return Collections.emptyList();
        }
        ElementBase component = componentInstances.get(applicationComponentInstanceKey);
        if (!(component instanceof ApplicationComponent)) {
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) component;
        List<IngresApplicationInterface> result = new ArrayList<>();
        if (ac.getInterfaces() != null) {
            for (ElementReference ref : ac.getInterfaces()) {
                if (ref != null && ref.getElementInstanceId() != null) {
                    ElementBase inter = componentInstances.get(ref.getElementInstanceId().getIdValue());
                    if (inter instanceof IngresApplicationInterface) {
                        result.add((IngresApplicationInterface) inter);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<EgressApplicationInterface> getEgressInterfaces(ApplicationComponent component) {
        if (component == null) {
            return Collections.emptyList();
        }
        return getEgressInterfaces(component.resolveElementInstanceKey());
    }

    @Override
    public List<EgressApplicationInterface> getEgressInterfaces(String applicationComponentInstanceKey) {
        if (applicationComponentInstanceKey == null) {
            return Collections.emptyList();
        }
        ElementBase component = componentInstances.get(applicationComponentInstanceKey);
        if (!(component instanceof ApplicationComponent)) {
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) component;
        List<EgressApplicationInterface> result = new ArrayList<>();
        if (ac.getInterfaces() != null) {
            for (ElementReference ref : ac.getInterfaces()) {
                if (ref != null && ref.getElementInstanceId() != null) {
                    ElementBase inter = componentInstances.get(ref.getElementInstanceId().getIdValue());
                    if (inter instanceof EgressApplicationInterface) {
                        result.add((EgressApplicationInterface) inter);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Collection<ApplicationComponent> getAllApplicationComponents() {
        return componentInstances.values().stream()
                .filter(e -> e instanceof ApplicationComponent)
                .map(e -> (ApplicationComponent) e)
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return componentInstances.size();
    }

    @Override
    public void clear() {
        componentInstances.clear();
        componentByIdentifier.clear();
        changeVersion.incrementAndGet();
    }

    @Override
    public long getChangeVersion() {
        return changeVersion.get();
    }
}
