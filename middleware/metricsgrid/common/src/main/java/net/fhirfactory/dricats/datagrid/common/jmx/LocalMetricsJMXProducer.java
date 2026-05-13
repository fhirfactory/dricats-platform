package net.fhirfactory.dricats.datagrid.common.jmx;

import net.fhirfactory.dricats.datagrid.common.metricsgrid.LocalMetricsServer;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class LocalMetricsJMXProducer implements LocalMetricsJMXProducerMXBean {
    private static final Logger LOG = LoggerFactory.getLogger(LocalMetricsJMXProducer.class);

    private ObjectName objectName;

    @Inject
    private LocalMetricsServer localMetricsServer;

    @PostConstruct
    public void init() {
        LOG.info(".init(): Entry");
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            objectName = new ObjectName("net.fhirfactory.dricats.metrics:type=LocalMetrics");
            if (!mbs.isRegistered(objectName)) {
                mbs.registerMBean(this, objectName);
                LOG.info(".init(): Registered MBean: {}", objectName);
            }
        } catch (Exception e) {
            LOG.error(".init(): Failed to register MBean", e);
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.info(".destroy(): Entry");
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (objectName != null && mbs.isRegistered(objectName)) {
                mbs.unregisterMBean(objectName);
                LOG.info(".destroy(): Unregistered MBean: {}", objectName);
            }
        } catch (Exception e) {
            LOG.error(".destroy(): Failed to unregister MBean", e);
        }
    }

    @Override
    public Map<String, String> getLatestMetrics() {
        LOG.debug(".getLatestMetrics(): Entry");
        Map<ApplicationComponent, ApplicationComponentMetricsData> cache = localMetricsServer.getLatestMetricsCache();
        Map<String, String> metricsMap = new HashMap<>();
        for (Map.Entry<ApplicationComponent, ApplicationComponentMetricsData> entry : cache.entrySet()) {
            String componentId = entry.getKey().resolveElementKey();
            String metricsData = entry.getValue().toString();
            metricsMap.put(componentId, metricsData);
        }
        return metricsMap;
    }
}
