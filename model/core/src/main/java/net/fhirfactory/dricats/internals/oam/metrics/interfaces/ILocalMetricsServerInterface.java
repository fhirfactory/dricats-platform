package net.fhirfactory.dricats.internals.oam.metrics.interfaces;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;

import java.time.LocalDateTime;
import java.util.List;

public interface ILocalMetricsServerInterface {
    public void addMetrics(ApplicationComponentSummary softwareComponent, ApplicationComponentMetricsData metricsData);
    public ApplicationComponentMetricsData getMetrics(ApplicationComponentSummary softwareComponent);
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime);
    public void registerFailedComponent(ApplicationComponentSummary softwareComponent, String failureDescription);
}
