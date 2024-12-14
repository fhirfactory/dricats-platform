package net.fhirfactory.dricats.internals.model.oam.interfaces;

import net.fhirfactory.dricats.internals.model.oam.SoftwareComponentMetricsData;
import net.fhirfactory.dricats.internals.model.software.SoftwareComponent;

import java.time.LocalDateTime;
import java.util.List;

public interface LocalMetricsServerInterface {
    public void addMetrics(SoftwareComponent softwareComponent, SoftwareComponentMetricsData metricsData);
    public SoftwareComponentMetricsData getMetrics(SoftwareComponent softwareComponent);
    public List<SoftwareComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime);
    public void registerFailedComponent(SoftwareComponent softwareComponent, String failureDescription);
}
