package net.fhirfactory.dricats.datagrid.common.jmx;

import java.util.Map;

public interface LocalMetricsJMXProducerMXBean {
    public Map<String, String> getLatestMetrics();
}
