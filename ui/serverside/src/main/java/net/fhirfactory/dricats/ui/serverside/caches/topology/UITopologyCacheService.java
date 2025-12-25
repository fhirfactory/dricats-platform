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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class UITopologyCacheService {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UITopologyCacheService.class);

    //
    // Attributes
    //

    // private Map<ElementBase.DistributableObjectId.getCommonId().getToken(), ElementBase> components = new ConcurrentHashMap<>();
    private Map<String, ElementBase> components = new ConcurrentHashMap<>();
    private boolean initialized = false;

    //
    // Constructor(s)
    //

    public UITopologyCacheService() {
        components = new ConcurrentHashMap<>();
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

    public Map<String, ElementBase> getComponents() {
        return components;
    }

    public void setComponents(Map<String, ElementBase> components) {
        this.components = components;
    }

    //
    // Business Methods
    //

    public void addComponent(ElementBase component) {
        LOG.debug(".addComponent(): Entry, component={}", component);
        components.put(component.resolveKey(), component);
        LOG.debug(".addComponent(): Exit");
    }

    public boolean hasEntry(String id) {
        LOG.debug(".hasEntry(): Entry, id={}", id);
        boolean result = components.containsKey(id);
        LOG.info(".hasEntry(): Exit, Returning {} for id={}", result, id);
        return result;
    }

    public ElementBase getComponent(String id) {
        LOG.debug(".getComponent(): Entry, id={}", id);
        ElementBase result = components.get(id);
        LOG.info(".getComponent(): Exit, Returning {} for id={}", result, id);
        return result;
    }

    public List<ElementBase> getComponentWithIdentifier(ElementIdentifier identifier) {
        LOG.debug(".getComponentWithIdentifier(): Entry, identifier={}", identifier);
        List<ElementBase> result = new ArrayList<>();
        for(ElementBase component : components.values()){
            List<ElementIdentifier> identifiers = component.getIdentifiers();
            if(identifiers == null){
                continue;
            }
            for(ElementIdentifier id : identifiers){
                if(id.equals(identifier)){
                    result.add(component);
                }
            }
        }
        LOG.info(".getComponentWithIdentifier(): Exit, Returning {} for identifier={}", result, identifier);
        return result;
    }

    public List<ApplicationComponent> getSubComponents(String id) {
        LOG.debug(".getSubComponents(): Entry, id={}", id);
        ElementBase c = components.get(id);
        if (c == null) {
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if (c.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT) {
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        if(!(c instanceof ApplicationComponent)){
            LOG.warn(".getSubComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) c;
        List<ApplicationComponent> result = new ArrayList<>();
        if (ac.getSubComponents() != null) {
            for (ElementReference childId : ac.getSubComponents()) {
                String key = childId.getLocalObjectId().getQualifiedName().getCommonName().getValue();
                ElementBase child = components.get(key);
                boolean isWUPAdapter = child.getSpecialization().contentEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_ADAPTER.getType());
                if(isWUPAdapter){
                    continue;
                }
                if (child instanceof ApplicationComponent) {
                    result.add((ApplicationComponent) child);
                } else {
                    LOG.warn(".getSubComponents(): No child component found for id={}", key);
                }
            }
        }
        LOG.info(".getSubComponents(): Exit, Returning {} subcomponents for id={}", result.size(), id);
        return result;
    }

    public List<IngresApplicationInterface> getIngressInterfaces(String id) {
        LOG.debug(".getIngressInterfaces(): Entry, id={}", id);
        ElementBase c = components.get(id);
        if (c == null) {
            LOG.info(".getIngressInterfaces(): Exit, No interfaces for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if (c.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT) {
            LOG.info(".getIngressInterfaces(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        if(!(c instanceof ApplicationComponent)){
            LOG.warn(".getIngressInterfaces(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) c;
        List<IngresApplicationInterface> result = new ArrayList<>();

        if (ac.getInterfaces() != null) {
            for (ElementReference childInterface : ac.getInterfaces()) {
                String key = childInterface.getLocalObjectId().getQualifiedName().getCommonName().getValue();
                ElementBase child = components.get(key);
                if (child instanceof IngresApplicationInterface) {
                    result.add((IngresApplicationInterface) child);
                } else {
                    LOG.warn(".getIngressInterfaces(): Exit, No child interface found for id={}", key);
                }
            }

        }
        LOG.info(".getIngressInterfaces(): Exit, Returning {} IngressApplicationInterfaces for id={}", result.size(), id);
        return result;
    }

    public List<EgressApplicationInterface> getEgressInterfaces(String id) {
        LOG.debug(".getEgressInterfaces(): Entry, id={}", id);
        ElementBase c = components.get(id);
        if (c == null) {
            LOG.info(".getEgressInterfaces(): Exit, No interfaces for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if (c.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT) {
            LOG.info(".getEgressInterfaces(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        if(!(c instanceof ApplicationComponent)){
            LOG.warn(".getEgressInterfaces(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        ApplicationComponent ac = (ApplicationComponent) c;
        List<EgressApplicationInterface> result = new ArrayList<>();

        if (ac.getInterfaces() != null) {
            for (ElementReference childInterface : ac.getInterfaces()) {
                String key = childInterface.getLocalObjectId().getQualifiedName().getCommonName().getValue();
                ElementBase child = components.get(key);
                if (child instanceof EgressApplicationInterface) {
                    result.add((EgressApplicationInterface) child);
                } else {
                    LOG.warn(".getEgressInterfaces(): Exit, No child interface found for id={}", key);
                }
            }

        }
        LOG.info(".getEgressInterfaces(): Exit, Returning {} EgressApplicationInterface for id={}", result.size(), id);
        return result;
    }

    public List<WUPAdapterBase> getInterfaceAdapters(String id) {
        LOG.debug(".getInterfaceAdapters(): Entry, id={}", id);
        ElementBase c = components.get(id);
        if (c == null) {
            LOG.info(".getInterfaceAdapters(): Exit, No interfaces for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if (c.getElementType() != ElementTypeEnum.APPLICATION_INTERFACE) {
            LOG.info(".getInterfaceAdapters(): Exit, No subcomponents for id={} (component is not a ApplicationInterface)", id);
            return Collections.emptyList();
        }
        boolean isEgressInterface = c.getSpecialization().contentEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS.getType());
        boolean isIngressInterface = c.getSpecialization().contentEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES.getType());
        if(!isEgressInterface && !isIngressInterface){
            LOG.warn(".getInterfaceAdapters(): Exit, No subcomponents for id={} (component is not a ApplicationInterface)", id);
            return Collections.emptyList();
        }
        WUPInterfaceBase wup = (WUPInterfaceBase) c;
        List<WUPAdapterBase> result = new ArrayList<>();
        if (wup.getAdapters() != null) {
            for (ElementReference childInterface : wup.getAdapters()) {
                String key = childInterface.getLocalObjectId().getQualifiedName().getCommonName().getValue();
                ElementBase child = components.get(key);
                boolean isWUPAdapter = child.getSpecialization().contentEquals(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_ADAPTER.getType());
                if (isWUPAdapter) {
                    result.add((WUPAdapterBase) child);
                } else {
                    LOG.warn(".getInterfaceAdapters(): Exit, No child adapters found for id={}", key);
                }
            }

        }
        LOG.info(".getInterfaceAdapters(): Exit, Returning {} Adapters for id={}", result.size(), id);
        return result;
    }

}
