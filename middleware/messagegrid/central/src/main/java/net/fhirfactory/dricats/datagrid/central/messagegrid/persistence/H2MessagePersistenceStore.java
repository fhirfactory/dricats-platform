/*
 * Copyright (c) 2025 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.datagrid.central.messagegrid.persistence;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.datagrid.central.messagegrid.spi.IMessagePersistenceStore;
import net.fhirfactory.dricats.datagrid.central.messagegrid.spi.PendingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class H2MessagePersistenceStore implements IMessagePersistenceStore {
    private static final Logger LOG = LoggerFactory.getLogger(H2MessagePersistenceStore.class);

    public static final String ENV_PERSIST_ENABLED = "DRICATS_BROKER_PERSIST_ENABLED";
    public static final String ENV_PERSIST_URL = "DRICATS_BROKER_PERSIST_URL";
    public static final String ENV_PERSIST_USER = "DRICATS_BROKER_PERSIST_USER";
    public static final String ENV_PERSIST_PASS = "DRICATS_BROKER_PERSIST_PASS";

    private boolean enabled;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPass;

    @PostConstruct
    public void init() {
        this.enabled = resolveEnabled();
        this.jdbcUrl = resolveJdbcUrl();
        this.jdbcUser = firstNonBlank(System.getProperty("dricats.broker.persist.user"), System.getenv(ENV_PERSIST_USER), "sa");
        this.jdbcPass = firstNonBlank(System.getProperty("dricats.broker.persist.pass"), System.getenv(ENV_PERSIST_PASS), "");
        if (enabled) {
            try {
                start();
            } catch (Exception e) {
                LOG.warn("H2MessagePersistenceStore: Failed to initialize; disabling persistence.", e);
                enabled = false;
            }
        }
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void start() throws Exception {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pending_messages (" +
                    "pid VARCHAR(100) PRIMARY KEY, " +
                    "topic VARCHAR(255) NOT NULL, " +
                    "key_str VARCHAR(255), " +
                    "payload BLOB NOT NULL, " +
                    "ts BIGINT NOT NULL)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_pending_ts ON pending_messages(ts)");
        }
        LOG.info("H2MessagePersistenceStore: initialized at {}", jdbcUrl);
    }

    @PreDestroy
    @Override
    public void stop() {
        // nothing to close; H2 file stays as configured
    }

    @Override
    public String persist(String topic, String key, byte[] payload) throws Exception {
        String pid = UUID.randomUUID().toString();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "MERGE INTO pending_messages(pid, topic, key_str, payload, ts) KEY(pid) VALUES(?,?,?,?,?)")) {
            ps.setString(1, pid);
            ps.setString(2, topic);
            ps.setString(3, key);
            ps.setBytes(4, payload);
            ps.setLong(5, System.currentTimeMillis());
            ps.executeUpdate();
        }
        return pid;
    }

    @Override
    public void delete(String pid) throws Exception {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM pending_messages WHERE pid=?")) {
            ps.setString(1, pid);
            ps.executeUpdate();
        }
    }

    @Override
    public List<PendingMessage> loadBatch(int limit) throws Exception {
        List<PendingMessage> results = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(
                "SELECT pid, topic, key_str, payload FROM pending_messages ORDER BY ts ASC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pid = rs.getString(1);
                    String topic = rs.getString(2);
                    String key = rs.getString(3);
                    byte[] payload = rs.getBytes(4);
                    results.add(new PendingMessage(pid, topic, key, payload));
                }
            }
        }
        return results;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
    }

    private boolean resolveEnabled() {
        String fromProp = System.getProperty("dricats.broker.persist.enabled");
        String fromEnv = System.getenv(ENV_PERSIST_ENABLED);
        String val = firstNonBlank(fromProp, fromEnv, "true");
        return Boolean.parseBoolean(val);
    }

    private String resolveJdbcUrl() {
        String fromProp = System.getProperty("dricats.broker.persist.url");
        String fromEnv = System.getenv(ENV_PERSIST_URL);
        return firstNonBlank(fromProp, fromEnv, "jdbc:h2:file:./data/dricats-broker;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE");
    }

    private String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) {
                return v;
            }
        }
        return null;
    }
}
