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
import net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummaryList;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.ui.serverside.metrics.UIMetricsCacheService;
import net.fhirfactory.dricats.ui.serverside.topology.UITopologyCacheService;
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
        LOG.debug("listComponents() invoked");
        List<ApplicationComponentSummary> list = testComponentServices.getComponents().values().stream()
                .filter(v -> v instanceof ApplicationComponentSummary)
                .map(v -> (ApplicationComponentSummary) v)
                .collect(Collectors.toList());
        LOG.info("Returning {} components", list.size());
        ApplicationComponentSummaryList resultList = new ApplicationComponentSummaryList();
        for (ApplicationComponentSummary currentListItem : list) {
            if(currentListItem instanceof net.fhirfactory.dricats.internals.oam.topology.SubsystemSummary) {
                resultList.getElementList().add(currentListItem);
            }
        }
        return(convertToJson(resultList));
    }

    public ApplicationComponentSummary getComponent(String id) {
        LOG.debug(".getComponent(): Entry, id={}", id);
        if (id == null || id.isEmpty()) {
            LOG.warn(".getComponent(), Exit, called with empty id");
            return null;
        }
        SimpleElementBase base = testComponentServices.getComponents().get(id);
        ApplicationComponentSummary c = (base instanceof ApplicationComponentSummary) ? (ApplicationComponentSummary) base : null;
        if (c == null) {
            LOG.warn(".getComponent(): Component not found for id={}", id);
        }
        LOG.debug(".getComponent(): Exit, Returning component id={}", c);
        return c;
    }

    public String getComponentAsJSON(String id){
        ApplicationComponentSummary component = getComponent(id);
        return(convertToJson(component));
    }

    public List<ApplicationComponentSummary> getSubComponents(String id) {
        LOG.debug("getSubComponents(id={}) invoked", id);
        List<ApplicationComponentSummary> subComponents = getTestComponentServices().getSubComponents(id);
        return subComponents;
    }

    public String getSubComponentsAsJSON(String id){
        List<ApplicationComponentSummary> subs = getSubComponents(id);
        return(convertToJson(subs));
    }

    public List<IngressInterfaceComponentSummary> getIngressInterfaces(String id) {
        LOG.debug("getInterfaces(id={}) invoked", id);
        List<IngressInterfaceComponentSummary> interfaces = getTestComponentServices().getIngressInterfaces(id);
        return interfaces;
    }

    public String getIngressInterfacesAsJSON(String id){
        List<IngressInterfaceComponentSummary> interfaces = getIngressInterfaces(id);
        return(convertToJson(interfaces));
    }

    public List<EgressInterfaceComponentSummary> getEgressInterfaces(String id) {
        LOG.debug("getEgressInterfaces(id={}) invoked", id);
        List<EgressInterfaceComponentSummary> interfaces = getTestComponentServices().getEgressInterfaces(id);
        return interfaces;
    }

    public String getEgressInterfacesAsJSON(String id){
        List<EgressInterfaceComponentSummary> interfaces = getEgressInterfaces(id);
        return(convertToJson(interfaces));
    }


    public ApplicationComponentMetricsData getLatestMetricsForComponent(String id) {
        LOG.debug(".getLatestMetricsForComponent(): Entry, id={}", id);
        ApplicationComponentSummary component = getComponent(id);
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
        List<ApplicationComponentMetricsData> list = testComponentServices.getComponents().values().stream()
                .map(c -> getLatestMetricsForComponent(keyOf(c)))
                .collect(Collectors.toList());
        LOG.info("Returning {} metrics entries for range [start={}, end={}]", list.size(), start, end);
        return list;
    }

    public String getMetricsInRangeAsJSON(String start, String end){
        List<ApplicationComponentMetricsData> list = getMetricsInRange(start, end);
        return(convertToJson(list));
    }

    private static String keyOf(SimpleElementBase c) {
        String key = c.resolveKey();
        return(key);
    }

    private static String normalizeKey(String id) {
        // IDs may be URL-decoded; just return as-is here
        return id;
    }




}
