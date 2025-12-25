package net.fhirfactory.dricats.datagrid.central.metricsgrid;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class LocalMetricsServerH2 implements ILocalMetricsServerInterface {

    @Inject
    H2MetricsRepository repository;

    @Override
    public void addMetrics(ApplicationComponent softwareComponentId, ApplicationComponentMetricsData metricsData) {
        repository.insertMetrics(softwareComponentId, metricsData);
    }

    @Override
    public ApplicationComponentMetricsData getMetrics(ApplicationComponent softwareComponent) {
        return repository.fetchLatestForComponent(softwareComponent);
    }

    @Override
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        return repository.fetchByTimeRange(metricsStartTime, metricsEndTime);
    }

    public List<ApplicationComponentMetricsData> getMetrics(ApplicationComponent softwareComponent, LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        String componentId = (softwareComponent != null && softwareComponent.getObjectID() != null && softwareComponent.getObjectID().getQualifiedName() != null && softwareComponent.getObjectID().getQualifiedName().getCommonName() != null)
                ? softwareComponent.getObjectID().getQualifiedName().getCommonName().getValue() : null;
        return repository.fetchByTimeRangeForComponent(metricsStartTime, metricsEndTime, componentId);
    }

    @Override
    public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription) {
        repository.insertFailure(softwareComponent, failureDescription);
    }
}
