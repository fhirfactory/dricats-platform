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
package net.fhirfactory.dricats.ui.uitest.handlers;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.topology.implementation.common.TopologyComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.caches.metrics.UIMetricsCacheService;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import net.fhirfactory.dricats.ui.uitest.handlers.common.BaseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory stubbed data provider for OAM UI tests. Provides a small
 * topology of components and simplistic metrics values.
 */
@ApplicationScoped
public class TopologyResourceHandler extends BaseHandler {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(TopologyResourceHandler.class);

    //
    // Attributes
    //

    @Inject
    private UITopologyCacheService testComponentServices;

    @Inject
    private UIMetricsCacheService testMetricsService;

    //
    // Constructor(s)
    //

    public TopologyResourceHandler() {
        LOG.debug(".constructor(): Entry");
        getLogger().debug(".constructor(): Exit");
    }

    //
    // Post Construct (for dependency injection)
    //
    @PostConstruct
    public void initialise() {
        LOG.debug("initialise() invoked");
        getTestMetricsService().initialise();
        getTestComponentServices().initialise();
        LOG.debug("initialise() exit");
    }

    //
    // Getters and Setters
    //

    protected Logger getLogger() {
        return (LOG);
    }

    protected UITopologyCacheService getTestComponentServices() {
        return testComponentServices;
    }

    protected UIMetricsCacheService getTestMetricsService() {
        return testMetricsService;
    }

    //
     // Business Methods
    //

    public String listComponentsAsJSON() {
        LOG.debug(".listComponentsAsJSON(): Entry");
        List<ApplicationComponent> list = testComponentServices.getAllApplicationComponents().stream()
                .map(v -> (ApplicationComponent) v)
                .collect(Collectors.toList());
        LOG.trace(".listComponentsAsJSON(): Found {} components", list.size());

        List<ApplicationComponent> resultList = list.stream()
                .filter(c -> c instanceof Subsystem || (c.getSpecialization() != null && c.getSpecialization().equals(ApplicationComponentSpecialisationEnum.SUBSYSTEM.getType())))
                .collect(Collectors.toList());

        LOG.trace(".listComponentsAsJSON(): Converting List to JSON");
        String result = convertToJson(resultList);
        LOG.debug(".listComponentsAsJSON(): Exit, Returning {} components", resultList.size());
        return(result);
    }

    public ApplicationComponent getComponent(String id) {
        LOG.debug(".getComponent(): Entry, id={}", id);
        if (id == null || id.isEmpty()) {
            LOG.warn(".getComponent(), Exit, called with empty id");
            return null;
        }
        ElementBase base = testComponentServices.getComponent(id);
        ApplicationComponent c = (base instanceof ApplicationComponent) ? (ApplicationComponent) base : null;
        if (c == null) {
            LOG.warn(".getComponent(): Component not found for id={}", id);
        }
        LOG.debug(".getComponent(): Exit, Returning component id={}", c);
        return c;
    }

    public String getComponentAsJSON(String id){
        ApplicationComponent component = getComponent(id);
        return(convertToJson(component));
    }

    public List<ApplicationComponent> getSubComponents(String id) {
        LOG.debug("getSubComponents(id={}) invoked", id);
        List<ApplicationComponent> subComponents = getTestComponentServices().getSubComponents(id);
        return subComponents;
    }

    public String getSubComponentsAsJSON(String id){
        List<ApplicationComponent> subs = getSubComponents(id);
        return(convertToJson(subs));
    }

    public List<IngresApplicationInterface> getIngressInterfaces(String id) {
        LOG.debug("getInterfaces(id={}) invoked", id);
        List<IngresApplicationInterface> interfaces = getTestComponentServices().getIngressInterfaces(id);
        return interfaces;
    }

    public String getIngressInterfacesAsJSON(String id){
        List<IngresApplicationInterface> interfaces = getIngressInterfaces(id);
        return(convertToJson(interfaces));
    }

    public List<EgressApplicationInterface> getEgressInterfaces(String id) {
        LOG.debug("getEgressInterfaces(id={}) invoked", id);
        List<EgressApplicationInterface> interfaces = getTestComponentServices().getEgressInterfaces(id);
        return interfaces;
    }

    public String getEgressInterfacesAsJSON(String id){
        List<EgressApplicationInterface> interfaces = getEgressInterfaces(id);
        return(convertToJson(interfaces));
    }


    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        LOG.debug(".getLatestMetricsForComponent(): Entry, id={}", id);
        ApplicationComponent component = getComponent(id);
        ApplicationComponentMetricsData latestMetricsForComponent = testMetricsService.getLatestMetricsForComponent(component);
        LOG.debug(".getLatestMetricsForComponent(): Exit, Returning latest metrics for component id={}", id);
        return(latestMetricsForComponent);
    }

        public String getLatestMetricsForComponentAsJSON(String id){
        ApplicationComponentMetricsData m = getLatestMetricsForComponent(id);
        return(convertToJson(m));
    }

    public List<ApplicationComponentMetricsData> getMetricsInRange(String start, String end) {
        LOG.debug("getMetricsInRange(start={}, end={}) invoked", start, end);
        // For UI tests, just return the latest metrics for all components
        List<ApplicationComponentMetricsData> list = testComponentServices.getAllApplicationComponents().stream()
                .map(c -> getLatestMetricsForComponent(keyOf(c)))
                .collect(Collectors.toList());
        LOG.info("Returning {} metrics entries for range [start={}, end={}]", list.size(), start, end);
        return list;
    }

    public String getMetricsInRangeAsJSON(String start, String end){
        List<ApplicationComponentMetricsData> list = getMetricsInRange(start, end);
        return(convertToJson(list));
    }

    private static String keyOf(ElementBase c) {
        String key = c.resolveElementInstanceKey();
        return(key);
    }

    private static String normalizeKey(String id) {
        // IDs may be URL-decoded; just return as-is here
        return id;
    }




}
