/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.ui.serverside.caches.topology;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.datagrid.common.local.ILocalTopologyMap;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UITopologyCacheService {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UITopologyCacheService.class);

    //
    // Attributes
    //

    @Inject
    private ILocalTopologyMap topologyMap;

    private boolean initialized = false;

    //
    // Constructor(s)
    //

    public UITopologyCacheService() {
    }

    @PostConstruct
    public void initialise() {
        LOG.debug(".initialise(): Entry");
        if (!initialized) {
            initialized = true;
        }
        LOG.debug(".initialise(): Exit");
    }

    //
    // Bean Methods
    //

    public ILocalTopologyMap getTopologyMap() {
        return topologyMap;
    }

    //
    // Business Methods
    //

    public void addComponent(ElementBase component) {
        LOG.debug(".addComponent(): Entry, component={}", component);
        topologyMap.add(component);
        LOG.debug(".addComponent(): Exit");
    }

    public void addComponentInstance(ElementBase componentInstance) {
        LOG.debug(".addComponentInstance(): Entry, componentInstance={}", componentInstance);
        topologyMap.add(componentInstance);
        LOG.debug(".addComponentInstance(): Exit");
    }

    public boolean hasEntry(String id) {
        LOG.debug(".hasEntry(): Entry, id={}", id);
        boolean result = topologyMap.findApplicationByElementInstanceKey(id).isPresent() ||
                topologyMap.findIngresInterfaceByElementInstanceKey(id).isPresent() ||
                topologyMap.findEgressInterfaceByElementInstance(id).isPresent();
        LOG.debug(".hasEntry(): Exit, Returning {} for id={}", result, id);
        return result;
    }

    public ElementBase getComponent(String id) {
        LOG.debug(".getComponent(): Entry, id={}", id);
        Optional<ApplicationComponent> ac = topologyMap.findApplicationByElementInstanceKey(id);
        if (ac.isPresent()) {
            return ac.get();
        }
        Optional<IngresApplicationInterface> ii = topologyMap.findIngresInterfaceByElementInstanceKey(id);
        if (ii.isPresent()) {
            return ii.get();
        }
        Optional<EgressApplicationInterface> ei = topologyMap.findEgressInterfaceByElementInstance(id);
        if (ei.isPresent()) {
            return ei.get();
        }
        LOG.debug(".getComponent(): Exit, Returning null for id={}", id);
        return null;
    }

    public List<ElementBase> getComponentWithIdentifier(ElementIdentifier identifier) {
        LOG.debug(".getComponentWithIdentifier(): Entry, identifier={}", identifier);
        if (identifier == null || identifier.getIdentifierValue() == null || identifier.getIdentifierValue().getCommonName() == null || identifier.getIdentifierValue().getCommonName().getName() == null) {
            return Collections.emptyList();
        }
        String name = identifier.getIdentifierValue().getCommonName().getName();
        List<ElementBase> result = new ArrayList<>();
        result.addAll(topologyMap.findApplicationComponentByName(name));
        result.addAll(topologyMap.findIngresInterfaceByName(name));
        result.addAll(topologyMap.findEgressInterfaceByName(name));
        LOG.debug(".getComponentWithIdentifier(): Exit, result.size()={}", result.size());
        return result;
    }

    public List<ApplicationComponent> getSubComponents(String id) {
        LOG.debug(".getSubComponents(): Entry, id={}", id);
        List<ApplicationComponent> result = topologyMap.getChildrenApplicationComponentsByInstanceKey(id);
        LOG.debug(".getSubComponents(): Exit, Returning {} subcomponents for id={}", result.size(), id);
        return result;
    }

    public List<IngresApplicationInterface> getIngressInterfaces(String id) {
        LOG.debug(".getIngressInterfaces(): Entry, id={}", id);
        List<IngresApplicationInterface> result = topologyMap.getIngresInterfaces(id);
        LOG.debug(".getIngressInterfaces(): Exit, Returning {} IngressApplicationInterfaces for id={}", result.size(), id);
        return result;
    }

    public List<EgressApplicationInterface> getEgressInterfaces(String id) {
        LOG.debug(".getEgressInterfaces(): Entry, id={}", id);
        List<EgressApplicationInterface> result = topologyMap.getEgressInterfaces(id);
        LOG.debug(".getEgressInterfaces(): Exit, Returning {} EgressApplicationInterface for id={}", result.size(), id);
        return result;
    }

    public List<WUPAdapterBase> getInterfaceAdapters(String id) {
        LOG.debug(".getInterfaceAdapters(): Entry, id={}", id);
        Optional<IngresApplicationInterface> ii = topologyMap.findIngresInterfaceByElementInstanceKey(id);
        List<WUPAdapterBase> result = new ArrayList<>();
        if (ii.isPresent()) {
            result.addAll(ii.get().getAdapters().stream()
                    .map(ref -> getComponent(ref.getElementInstanceId().getIdValue()))
                    .filter(c -> c instanceof WUPAdapterBase)
                    .map(c -> (WUPAdapterBase) c)
                    .toList());
        }
        Optional<EgressApplicationInterface> ei = topologyMap.findEgressInterfaceByElementInstance(id);
        if (ei.isPresent()) {
            result.addAll(ei.get().getAdapters().stream()
                    .map(ref -> getComponent(ref.getElementInstanceId().getIdValue()))
                    .filter(c -> c instanceof WUPAdapterBase)
                    .map(c -> (WUPAdapterBase) c)
                    .toList());
        }
        LOG.debug(".getInterfaceAdapters(): Exit, Returning {} Adapters for id={}", result.size(), id);
        return result;
    }

    public Collection<ApplicationComponent> getAllApplicationComponents() {
        LOG.debug(".getAllApplicationComponents(): Entry");
        Collection<ApplicationComponent> allComponents = topologyMap.getAllApplicationComponents();
        LOG.debug(".getAllApplicationComponents(): Exit, returning {} components", allComponents.size());
        return allComponents;
    }

}
