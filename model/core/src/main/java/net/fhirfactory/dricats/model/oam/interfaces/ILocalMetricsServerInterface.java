package net.fhirfactory.dricats.model.oam.interfaces;

import net.fhirfactory.dricats.model.oam.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.model.topology.reference.layers.application.ApplicationComponent;

import java.time.LocalDateTime;
import java.util.List;

public interface ILocalMetricsServerInterface {
    public void addMetrics(ApplicationComponent softwareComponent, ApplicationComponentMetricsData metricsData);
    public ApplicationComponentMetricsData getMetrics(ApplicationComponent softwareComponent);
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime);
    public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription);
}
