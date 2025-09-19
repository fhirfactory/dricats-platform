package net.fhirfactory.dricats.internals.oam.interfaces;

import net.fhirfactory.dricats.internals.oam.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;

import java.time.LocalDateTime;
import java.util.List;

public interface ILocalMetricsServerInterface {
    public void addMetrics(ApplicationComponent softwareComponent, ApplicationComponentMetricsData metricsData);
    public ApplicationComponentMetricsData getMetrics(ApplicationComponent softwareComponent);
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime);
    public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription);
}
