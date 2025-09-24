package net.fhirfactory.dricats.middleware.oam.central.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fhirfactory.dricats.internals.oam.metrics.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lightweight JDBC repository using H2 to persist metrics data for OAM central module.
 * This avoids introducing a full JPA stack and keeps dependencies minimal.
 */
@ApplicationScoped
public class H2MetricsRepository {
    private static final Logger LOG = LoggerFactory.getLogger(H2MetricsRepository.class);

    private final ObjectMapper mapper = new ObjectMapper();

    // Use local file DB under target to avoid polluting source tree
    private static final String JDBC_URL = "jdbc:h2:./target/oam-metrics;AUTO_SERVER=TRUE";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASS = "";

    public H2MetricsRepository() {
        initSchema();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    private void initSchema() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            // New table to store ApplicationComponent identity and type
            st.execute("CREATE TABLE IF NOT EXISTS application_components (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "object_id_common_name VARCHAR(256), " +
                    "distributable_object_id CLOB, " +
                    "component_type VARCHAR(64)" +
                    ")");
            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS idx_app_components_common_name ON application_components(object_id_common_name)");

            // Metrics table, add component_fk for relation to application_components
            st.execute("CREATE TABLE IF NOT EXISTS metrics_records (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "component_id VARCHAR(256), " +
                    "component_name VARCHAR(256), " +
                    "participant_name VARCHAR(256), " +
                    "component_type VARCHAR(64), " +
                    "last_activity TIMESTAMP, " +
                    "startup_instant TIMESTAMP, " +
                    "component_status VARCHAR(64), " +
                    "ingres_count INT, " +
                    "egress_attempt INT, " +
                    "egress_success INT, " +
                    "egress_failure INT, " +
                    "internal_distributed INT, " +
                    "internal_received INT, " +
                    "internal_distribution_map CLOB" +
                    ")");
            // Add new FK column if upgrading existing DB
            st.execute("ALTER TABLE metrics_records ADD COLUMN IF NOT EXISTS component_fk BIGINT");
            st.execute("CREATE INDEX IF NOT EXISTS idx_metrics_component ON metrics_records(component_id)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_metrics_time ON metrics_records(last_activity)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_metrics_component_fk ON metrics_records(component_fk)");

            // Failures table with optional FK
            st.execute("CREATE TABLE IF NOT EXISTS component_failures (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "component_id VARCHAR(256), " +
                    "component_name VARCHAR(256), " +
                    "participant_name VARCHAR(256), " +
                    "failure_description CLOB, " +
                    "failure_time TIMESTAMP" +
                    ")");
            st.execute("ALTER TABLE component_failures ADD COLUMN IF NOT EXISTS component_fk BIGINT");
            st.execute("CREATE INDEX IF NOT EXISTS idx_failures_component_fk ON component_failures(component_fk)");

            // Add FK constraints (best-effort, ignore if already present)
            try {
                st.execute("ALTER TABLE metrics_records ADD CONSTRAINT IF NOT EXISTS fk_metrics_component FOREIGN KEY(component_fk) REFERENCES application_components(id)");
            } catch (SQLException ignore) {}
            try {
                st.execute("ALTER TABLE component_failures ADD CONSTRAINT IF NOT EXISTS fk_failures_component FOREIGN KEY(component_fk) REFERENCES application_components(id)");
            } catch (SQLException ignore) {}
        } catch (SQLException e) {
            LOG.error("Failed to initialise H2 schema", e);
        }
    }

    public void insertMetrics(ApplicationComponent component, ApplicationComponentMetricsData md) {
        Long compFk = ensureComponentRow(component, md);
        String sql = "INSERT INTO metrics_records (component_fk, component_id, component_name, participant_name, component_type, last_activity, startup_instant, component_status, ingres_count, egress_attempt, egress_success, egress_failure, internal_distributed, internal_received, internal_distribution_map) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (compFk != null) {
                ps.setLong(1, compFk);
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            // Backwards-compatibility removed: do not populate legacy component_id/component_name columns
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
            ps.setString(4, md.getParticipantName());
            ps.setString(5, md.getComponentType() != null ? md.getComponentType().name() : null);
            ps.setTimestamp(6, toTimestamp(md.getLastActivityInstant()));
            ps.setTimestamp(7, toTimestamp(md.getComponentStartupInstant()));
            ps.setString(8, md.getComponentStatus());
            ps.setInt(9, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getIngresMessageCount() : 0);
            ps.setInt(10, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageAttemptCount() : 0);
            ps.setInt(11, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageSuccessCount() : 0);
            ps.setInt(12, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageFailureCount() : 0);
            ps.setInt(13, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalDistributedMessageCount() : 0);
            ps.setInt(14, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalReceivedMessageCount() : 0);
            ps.setString(15, toJson(md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalDistributionCountMap() : null));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert metrics", e);
        }
    }

    private Long ensureComponentRow(ApplicationComponent component, ApplicationComponentMetricsData md) {
        String commonName = null;
        if (component != null && component.getObjectID() != null && component.getObjectID().getQualifiedName() != null && component.getObjectID().getQualifiedName().getCommonName() != null) {
            commonName = component.getObjectID().getQualifiedName().getCommonName().getValue();
        }
        String typeStr = (md != null && md.getComponentType() != null) ? md.getComponentType().name() : null;
        String doiJson = toJsonGeneral(component != null ? component.getObjectID() : null);
        if (commonName == null && doiJson == null && typeStr == null) {
            return null; // nothing to store
        }
        try (Connection conn = getConnection()) {
            // Try find existing
            try (PreparedStatement sel = conn.prepareStatement("SELECT id, distributable_object_id, component_type FROM application_components WHERE object_id_common_name = ?")) {
                sel.setString(1, commonName);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        // Optionally update details
                        try (PreparedStatement up = conn.prepareStatement("UPDATE application_components SET distributable_object_id = COALESCE(?, distributable_object_id), component_type = COALESCE(?, component_type) WHERE id = ?")) {
                            if (doiJson != null) { up.setString(1, doiJson); } else { up.setNull(1, Types.CLOB); }
                            if (typeStr != null) { up.setString(2, typeStr); } else { up.setNull(2, Types.VARCHAR); }
                            up.setLong(3, id);
                            up.executeUpdate();
                        }
                        return id;
                    }
                }
            }
            // Insert new
            try (PreparedStatement ins = conn.prepareStatement("INSERT INTO application_components (object_id_common_name, distributable_object_id, component_type) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, commonName);
                if (doiJson != null) { ins.setString(2, doiJson); } else { ins.setNull(2, Types.CLOB); }
                if (typeStr != null) { ins.setString(3, typeStr); } else { ins.setNull(3, Types.VARCHAR); }
                ins.executeUpdate();
                try (ResultSet gk = ins.getGeneratedKeys()) {
                    if (gk.next()) {
                        return gk.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOG.warn("ensureComponentRow: failed to upsert component '{}': {}", commonName, e.getMessage());
        }
        return null;
    }

    private String toJsonGeneral(Object obj) {
        if (obj == null) return null;
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public ApplicationComponentMetricsData fetchLatestForComponent(ApplicationComponent component) {
        String componentId = (component.getObjectID()!=null && component.getObjectID().getQualifiedName()!=null && component.getObjectID().getQualifiedName().getCommonName()!=null)
                ? component.getObjectID().getQualifiedName().getCommonName().getValue() : null;
        String sql = "SELECT mr.* FROM metrics_records mr JOIN application_components ac ON mr.component_fk = ac.id WHERE ac.object_id_common_name = ? ORDER BY mr.last_activity DESC NULLS LAST, mr.id DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, componentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMetrics(rs);
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch latest metrics for component={}", componentId, e);
        }
        return null;
    }

    public List<ApplicationComponentMetricsData> fetchByTimeRange(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM metrics_records WHERE (? IS NULL OR last_activity >= ?) AND (? IS NULL OR last_activity <= ?) ORDER BY last_activity ASC, id ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp tsStart = start != null ? Timestamp.valueOf(start) : null;
            Timestamp tsEnd = end != null ? Timestamp.valueOf(end) : null;
            // For H2, setNull with type
            if (tsStart == null) {
                ps.setNull(1, Types.TIMESTAMP);
                ps.setNull(2, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(1, tsStart);
                ps.setTimestamp(2, tsStart);
            }
            if (tsEnd == null) {
                ps.setNull(3, Types.TIMESTAMP);
                ps.setNull(4, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(3, tsEnd);
                ps.setTimestamp(4, tsEnd);
            }
            List<ApplicationComponentMetricsData> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMetrics(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.error("Failed to fetch metrics by time range", e);
            return new ArrayList<>();
        }
    }

    public List<ApplicationComponentMetricsData> fetchByTimeRangeForComponent(LocalDateTime start, LocalDateTime end, String componentId) {
        String sql = "SELECT mr.* FROM metrics_records mr JOIN application_components ac ON mr.component_fk = ac.id WHERE ac.object_id_common_name = ? AND (? IS NULL OR mr.last_activity >= ?) AND (? IS NULL OR mr.last_activity <= ?) ORDER BY mr.last_activity ASC, mr.id ASC";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, componentId);
            Timestamp tsStart = start != null ? Timestamp.valueOf(start) : null;
            Timestamp tsEnd = end != null ? Timestamp.valueOf(end) : null;
            if (tsStart == null) {
                ps.setNull(2, Types.TIMESTAMP);
                ps.setNull(3, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(2, tsStart);
                ps.setTimestamp(3, tsStart);
            }
            if (tsEnd == null) {
                ps.setNull(4, Types.TIMESTAMP);
                ps.setNull(5, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(4, tsEnd);
                ps.setTimestamp(5, tsEnd);
            }
            List<ApplicationComponentMetricsData> list = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapMetrics(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            LOG.error("Failed to fetch metrics by time range for component={}", componentId, e);
            return new ArrayList<>();
        }
    }

    public void insertFailure(ApplicationComponent component, String description) {
        Long compFk = ensureComponentRow(component, null);
        String sql = "INSERT INTO component_failures (component_fk, component_id, component_name, participant_name, failure_description, failure_time) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (compFk != null) { ps.setLong(1, compFk); } else { ps.setNull(1, Types.BIGINT); }
            // Backwards-compatibility removed: do not populate legacy component_id/component_name columns
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
            // participant name can be found on metrics usually; we leave null here
            ps.setString(4, null);
            ps.setString(5, description);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert component failure", e);
        }
    }

    private ApplicationComponentMetricsData mapMetrics(ResultSet rs) throws SQLException {
        ApplicationComponentMetricsData md = new ApplicationComponentMetricsData();
        md.setParticipantName(rs.getString("participant_name"));
        String componentType = rs.getString("component_type");
        if (componentType != null) {
            try {
                md.setComponentType(net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum.valueOf(componentType));
            } catch (IllegalArgumentException iae) {
                // ignore unknown
            }
        }
        md.setLastActivityInstant(fromTimestamp(rs.getTimestamp("last_activity")));
        md.setComponentStartupInstant(fromTimestamp(rs.getTimestamp("startup_instant")));
        md.setComponentStatus(rs.getString("component_status"));
        // Populate messaging statistics structure
        net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics ms = new net.fhirfactory.dricats.internals.oam.metrics.datatypes.ComponentMessagingStatistics();
        ms.setIngresMessageCount(rs.getInt("ingres_count"));
        ms.setEgressMessageAttemptCount(rs.getInt("egress_attempt"));
        ms.setEgressMessageSuccessCount(rs.getInt("egress_success"));
        ms.setEgressMessageFailureCount(rs.getInt("egress_failure"));
        ms.setInternalDistributedMessageCount(rs.getInt("internal_distributed"));
        ms.setInternalReceivedMessageCount(rs.getInt("internal_received"));
        String json = rs.getString("internal_distribution_map");
        if (json != null) {
            try {
                @SuppressWarnings("unchecked") Map<String,Integer> map = mapper.readValue(json, Map.class);
                ms.setInternalDistributionCountMap(map);
            } catch (Exception e) {
                LOG.warn("Failed to parse distribution map JSON: {}", e.getMessage());
            }
        }
        md.setMessagingStatistics(ms);
        return md;
    }

    private Timestamp toTimestamp(Instant instant) { return instant == null ? null : Timestamp.from(instant); }
    private Instant fromTimestamp(Timestamp ts) { return ts == null ? null : ts.toInstant(); }

    private String toJson(Map<String, Integer> map) {
        if (map == null) return null;
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            LOG.warn("Failed to serialise map to JSON: {}", e.getMessage());
            return null;
        }
    }

    // --- Overloads using ApplicationComponentSummary ---------------------------------
    public void insertMetrics(net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary component,
                              ApplicationComponentMetricsData md) {
        Long compFk = ensureComponentRow(component, md);
        String sql = "INSERT INTO metrics_records (component_fk, component_id, component_name, participant_name, component_type, last_activity, startup_instant, component_status, ingres_count, egress_attempt, egress_success, egress_failure, internal_distributed, internal_received, internal_distribution_map) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (compFk != null) {
                ps.setLong(1, compFk);
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            // legacy columns removed
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
            ps.setString(4, md.getParticipantName());
            ps.setString(5, md.getComponentType() != null ? md.getComponentType().name() : null);
            ps.setTimestamp(6, toTimestamp(md.getLastActivityInstant()));
            ps.setTimestamp(7, toTimestamp(md.getComponentStartupInstant()));
            ps.setString(8, md.getComponentStatus());
            ps.setInt(9, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getIngresMessageCount() : 0);
            ps.setInt(10, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageAttemptCount() : 0);
            ps.setInt(11, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageSuccessCount() : 0);
            ps.setInt(12, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getEgressMessageFailureCount() : 0);
            ps.setInt(13, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalDistributedMessageCount() : 0);
            ps.setInt(14, md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalReceivedMessageCount() : 0);
            ps.setString(15, toJson(md.getMessagingStatistics() != null ? md.getMessagingStatistics().getInternalDistributionCountMap() : null));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert metrics (summary)", e);
        }
    }

    private Long ensureComponentRow(net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary component,
                                    ApplicationComponentMetricsData md) {
        String commonName = null;
        if (component != null && component.getObjectID() != null && component.getObjectID().getQualifiedName() != null && component.getObjectID().getQualifiedName().getCommonName() != null) {
            commonName = component.getObjectID().getQualifiedName().getCommonName().getValue();
        }
        String typeStr = (md != null && md.getComponentType() != null) ? md.getComponentType().name() : null;
        String doiJson = toJsonGeneral(component != null ? component.getObjectID() : null);
        if (commonName == null && doiJson == null && typeStr == null) {
            return null; // nothing to store
        }
        try (Connection conn = getConnection()) {
            // Try find existing
            try (PreparedStatement sel = conn.prepareStatement("SELECT id, distributable_object_id, component_type FROM application_components WHERE object_id_common_name = ?")) {
                sel.setString(1, commonName);
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong(1);
                        // Optionally update details
                        try (PreparedStatement up = conn.prepareStatement("UPDATE application_components SET distributable_object_id = COALESCE(?, distributable_object_id), component_type = COALESCE(?, component_type) WHERE id = ?")) {
                            if (doiJson != null) { up.setString(1, doiJson); } else { up.setNull(1, Types.CLOB); }
                            if (typeStr != null) { up.setString(2, typeStr); } else { up.setNull(2, Types.VARCHAR); }
                            up.setLong(3, id);
                            up.executeUpdate();
                        }
                        return id;
                    }
                }
            }
            // Insert new
            try (PreparedStatement ins = conn.prepareStatement("INSERT INTO application_components (object_id_common_name, distributable_object_id, component_type) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, commonName);
                if (doiJson != null) { ins.setString(2, doiJson); } else { ins.setNull(2, Types.CLOB); }
                if (typeStr != null) { ins.setString(3, typeStr); } else { ins.setNull(3, Types.VARCHAR); }
                ins.executeUpdate();
                try (ResultSet gk = ins.getGeneratedKeys()) {
                    if (gk.next()) {
                        return gk.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOG.warn("ensureComponentRow(summary): failed to upsert component '{}': {}", commonName, e.getMessage());
        }
        return null;
    }

    public ApplicationComponentMetricsData fetchLatestForComponent(net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary component) {
        String componentId = (component != null && component.getObjectID()!=null && component.getObjectID().getQualifiedName()!=null && component.getObjectID().getQualifiedName().getCommonName()!=null)
                ? component.getObjectID().getQualifiedName().getCommonName().getValue() : null;
        String sql = "SELECT mr.* FROM metrics_records mr JOIN application_components ac ON mr.component_fk = ac.id WHERE ac.object_id_common_name = ? ORDER BY mr.last_activity DESC NULLS LAST, mr.id DESC LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, componentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMetrics(rs);
                }
            }
        } catch (SQLException e) {
            LOG.error("Failed to fetch latest metrics for component(summary)={} ", componentId, e);
        }
        return null;
    }

    public void insertFailure(net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary component, String description) {
        Long compFk = ensureComponentRow(component, null);
        String sql = "INSERT INTO component_failures (component_fk, component_id, component_name, participant_name, failure_description, failure_time) VALUES (?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (compFk != null) { ps.setLong(1, compFk); } else { ps.setNull(1, Types.BIGINT); }
            ps.setNull(2, Types.VARCHAR);
            ps.setNull(3, Types.VARCHAR);
            ps.setString(4, null);
            ps.setString(5, description);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Failed to insert component failure (summary)", e);
        }
    }
}
