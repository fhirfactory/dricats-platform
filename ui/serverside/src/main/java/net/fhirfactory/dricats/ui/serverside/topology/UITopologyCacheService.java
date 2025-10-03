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
package net.fhirfactory.dricats.ui.serverside.topology;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.InterfaceComponentSummary;
import net.fhirfactory.dricats.internals.reference.common.SimpleElementBase;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
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
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(UITopologyCacheService.class);

    //
     // Attributes
    //
    private Map<String, SimpleElementBase> components = new ConcurrentHashMap<>();
    private boolean initialized = false;

    //
     // Constructor(s)
    //

    public UITopologyCacheService(){
        components = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void initialise(){
        LOG.debug(".initialise(): Entry");
        if (!initialized) {
            initialized = true;
        }
        LOG.debug(".initialise(): Exit");
    }

    //
     // Bean Methods
    //

    public Map<String, SimpleElementBase> getComponents() {
        return components;
    }

    public void setComponents(Map<String, SimpleElementBase> components) {
        this.components = components;
    }

    //
     // Business Methods
    //

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        LOG.debug(".getSubComponents(): Entry, id={}", id);
        SimpleElementBase c = components.get(id);
        if(c == null){
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if(c.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT){
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        ApplicationComponentSummary acs = (ApplicationComponentSummary) c;
        if (acs.getSubComponents() == null) {
            LOG.info(".getSubComponents(): Exit, No subcomponents for id={} ", id);
            return Collections.emptyList();
        }
        List<ApplicationComponentSummary> result = new ArrayList<>();
        for (DistributableObjectId childId : acs.getSubComponents()) {
            String key = childId.getQualifiedName().getCommonName().getValue();
            ApplicationComponentSummary child = (ApplicationComponentSummary) components.get(key);
            if (child != null) {
                result.add(child);
            } else {
                LOG.warn(".getSubComponents(): Exit, No child component found for id={}", key);
            }
        }
        LOG.info(".getSubComponents(): Exit, Returning {} subcomponents for id={}", result.size(), id);
        return result;
    }

    public List<InterfaceComponentSummary> getInterfaceComponents(String id) {
        LOG.debug(".getInterfaceComponents(): Entry, id={}", id);
        SimpleElementBase c = components.get(id);
        if(c == null){
            LOG.info(".getInterfaceComponents(): Exit, No interfaces for id={} (component missing)", id);
            return Collections.emptyList();
        }
        if(c.getElementType() != ElementTypeEnum.APPLICATION_COMPONENT){
            LOG.info(".getInterfaceComponents(): Exit, No subcomponents for id={} (component is not a ApplicationComponent)", id);
            return Collections.emptyList();
        }
        ApplicationComponentSummary acs = (ApplicationComponentSummary) c;
        if (acs.getInterfaces() == null) {
            LOG.info(".getInterfaceComponents(): Exit, No interfaces for id={} ", id);
            return Collections.emptyList();
        }
        List<InterfaceComponentSummary> result = new ArrayList<>();
        for (DistributableObjectId childId : acs.getInterfaces()) {
            String key = childId.getQualifiedName().getCommonName().getValue();
            InterfaceComponentSummary child = (InterfaceComponentSummary) components.get(key);
            if (child != null) {
                result.add(child);
            } else {
                LOG.warn(".getInterfaceComponents(): Exit, No child component found for id={}", key);
            }
        }
        LOG.info(".getInterfaceComponents(): Exit, Returning {} subcomponents for id={}", result.size(), id);
        return result;
    }
}
