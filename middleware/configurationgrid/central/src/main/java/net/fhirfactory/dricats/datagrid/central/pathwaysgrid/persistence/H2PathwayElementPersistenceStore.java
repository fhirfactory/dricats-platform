package net.fhirfactory.dricats.datagrid.central.pathwaysgrid.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.datagrid.central.pathwaysgrid.spi.IPathwayElementPersistenceService;
import net.fhirfactory.dricats.internals.pathways.PathwayElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.Optional;

/**
 * H2-backed persistence for PathwayElement cache entries.
 * Stores serialized PathwayElement as a BLOB keyed by a String id.
 */
@ApplicationScoped
public class H2PathwayElementPersistenceStore implements IPathwayElementPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(H2PathwayElementPersistenceStore.class);

    public static final String ENV_ENABLED = "DRICATS_PATHWAY_PERSIST_ENABLED";
    public static final String ENV_URL = "DRICATS_PATHWAY_PERSIST_URL";
    public static final String ENV_USER = "DRICATS_PATHWAY_PERSIST_USER";
    public static final String ENV_PASS = "DRICATS_PATHWAY_PERSIST_PASS";

    private boolean enabled;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPass;

    @PostConstruct
    public void init() {
        this.enabled = resolveEnabled();
        this.jdbcUrl = resolveJdbcUrl();
        this.jdbcUser = firstNonBlank(System.getProperty("dricats.pathway.persist.user"), System.getenv(ENV_USER), "sa");
        this.jdbcPass = firstNonBlank(System.getProperty("dricats.pathway.persist.pass"), System.getenv(ENV_PASS), "");
        if (enabled) {
            try {
                start();
            } catch (Exception e) {
                LOG.warn("H2PathwayElementPersistenceStore: Failed to initialize; disabling persistence.", e);
                enabled = false;
            }
        } else {
            LOG.info("H2PathwayElementPersistenceStore: disabled by configuration.");
        }
    }

    private boolean resolveEnabled() {
        String fromProp = System.getProperty("dricats.pathway.persist.enabled");
        String fromEnv = System.getenv(ENV_ENABLED);
        String val = firstNonBlank(fromProp, fromEnv, "true");
        return Boolean.parseBoolean(val);
    }

    private String resolveJdbcUrl() {
        String fromProp = System.getProperty("dricats.pathway.persist.url");
        String fromEnv = System.getenv(ENV_URL);
        return firstNonBlank(fromProp, fromEnv, "jdbc:h2:file:./data/dricats-pathway;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE");
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }

    public void start() throws Exception {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pathway_elements (" +
                    "id VARCHAR(200) PRIMARY KEY, " +
                    "payload BLOB NOT NULL, " +
                    "ts BIGINT NOT NULL)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_pathway_elements_ts ON pathway_elements(ts)");
        }
        LOG.info("H2PathwayElementPersistenceStore: initialized at {}", jdbcUrl);
    }

    @PreDestroy
    public void stop() {
        // nothing to close explicitly
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
    }

    @Override
    public Optional<PathwayElement> load(String key) {
        if (!enabled) {
            return Optional.empty();
        }
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT payload FROM pathway_elements WHERE id=?")) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] blob = rs.getBytes(1);
                    return Optional.ofNullable(deserialize(blob));
                }
            }
        } catch (Exception e) {
            LOG.debug("H2PathwayElementPersistenceStore: load failed for key={}", key, e);
        }
        return Optional.empty();
    }

    @Override
    public void save(String key, PathwayElement element) {
        if (!enabled) {
            return;
        }
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "MERGE INTO pathway_elements(id, payload, ts) KEY(id) VALUES(?,?,?)")) {
            ps.setString(1, key);
            ps.setBytes(2, serialize(element));
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.debug("H2PathwayElementPersistenceStore: save failed for key={}", key, e);
        }
    }

    @Override
    public void delete(String key) {
        if (!enabled) {
            return;
        }
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM pathway_elements WHERE id=?")) {
            ps.setString(1, key);
            ps.executeUpdate();
        } catch (Exception e) {
            LOG.debug("H2PathwayElementPersistenceStore: delete failed for key={}", key, e);
        }
    }

    private byte[] serialize(PathwayElement element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(element);
        }
        return baos.toByteArray();
    }

    private PathwayElement deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object o = ois.readObject();
            return (PathwayElement) o;
        }
    }
}
