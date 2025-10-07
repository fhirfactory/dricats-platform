package net.fhirfactory.dricats.middleware.oam.central.persistence;

import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.oam.topology.base.ApplicationComponentSummary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class LocalMetricsServerH2 implements ILocalMetricsServerInterface {

    @Inject
    H2MetricsRepository repository;

    @Override
    public void addMetrics(ApplicationComponentSummary softwareComponent, ApplicationComponentMetricsData metricsData) {
        repository.insertMetrics(softwareComponent, metricsData);
    }

    @Override
    public ApplicationComponentMetricsData getMetrics(ApplicationComponentSummary softwareComponent) {
        return repository.fetchLatestForComponent(softwareComponent);
    }

    @Override
    public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        return repository.fetchByTimeRange(metricsStartTime, metricsEndTime);
    }

    public List<ApplicationComponentMetricsData> getMetrics(ApplicationComponentSummary softwareComponent, LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) {
        String componentId = (softwareComponent != null && softwareComponent.getObjectID() != null && softwareComponent.getObjectID().getQualifiedName() != null && softwareComponent.getObjectID().getQualifiedName().getCommonName() != null)
                ? softwareComponent.getObjectID().getQualifiedName().getCommonName().getValue() : null;
        return repository.fetchByTimeRangeForComponent(metricsStartTime, metricsEndTime, componentId);
    }

    @Override
    public void registerFailedComponent(ApplicationComponentSummary softwareComponent, String failureDescription) {
        repository.insertFailure(softwareComponent, failureDescription);
    }
}
