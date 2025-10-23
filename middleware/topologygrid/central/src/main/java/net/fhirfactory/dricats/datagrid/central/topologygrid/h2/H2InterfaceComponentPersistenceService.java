/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.datagrid.central.topologygrid.h2;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import net.fhirfactory.dricats.datagrid.central.topologygrid.spi.IInterfaceComponentPersistenceService;
import net.fhirfactory.dricats.internals.oam.topology.EgressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.IngressInterfaceComponentSummary;
import net.fhirfactory.dricats.internals.oam.topology.base.InterfaceComponentSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

/**
 * H2-backed persistence for InterfaceComponentSummary items used by the distributed cache.
 *
 * System properties:
 *  - application.interface.store.h2.url (default: jdbc:h2:~/dricats-appinterface;AUTO_SERVER=TRUE)
 *  - application.interface.store.h2.user (default: sa)
 *  - application.interface.store.h2.pass (default: "")
 */
@ApplicationScoped
@Alternative
@Priority(1)
public class H2InterfaceComponentPersistenceService implements IInterfaceComponentPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(H2InterfaceComponentPersistenceService.class);

    private volatile boolean initialised = false;

    private String jdbcUrl() { return System.getProperty("application.interface.store.h2.url", "jdbc:h2:~/dricats-appinterface;AUTO_SERVER=TRUE"); }
    private String jdbcUser() { return System.getProperty("application.interface.store.h2.user", "sa"); }
    private String jdbcPass() { return System.getProperty("application.interface.store.h2.pass", ""); }

    private void initIfNeeded() {
        if (initialised) return;
        synchronized (this) {
            if (initialised) return;
            try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass());
                 Statement st = c.createStatement()) {
                c.setAutoCommit(true);
                st.execute("CREATE TABLE IF NOT EXISTS APP_INTERFACE (" +
                        " if_key VARCHAR(255) PRIMARY KEY," +
                        " name VARCHAR(512)," +
                        " specialization VARCHAR(256)," +
                        " documentation VARCHAR(2048)," +
                        " subtype VARCHAR(32)" +
                        ")");
                initialised = true;
                LOG.info("H2InterfaceComponentPersistenceService initialized (schema ready) on url={}", jdbcUrl());
            } catch (SQLException e) {
                LOG.error("Failed to initialise H2 schema for APP_INTERFACE", e);
                throw new RuntimeException("Failed to initialise H2 schema for APP_INTERFACE", e);
            }
        }
    }

    @Override
    public Optional<InterfaceComponentSummary> load(String key) {
        if (key == null || key.isEmpty()) return Optional.empty();
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            try (PreparedStatement ps = c.prepareStatement("SELECT name, specialization, documentation, subtype FROM APP_INTERFACE WHERE if_key=?")) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { return Optional.empty(); }
                    String name = rs.getString(1);
                    String specialization = rs.getString(2);
                    String documentation = rs.getString(3);
                    String subtype = rs.getString(4);
                    InterfaceComponentSummary s = "INGRESS".equalsIgnoreCase(subtype) ? new IngressInterfaceComponentSummary() : new EgressInterfaceComponentSummary();
                    s.setName(name);
                    s.setSpecialization(specialization);
                    s.setDocumentation(documentation);
                    return Optional.of(s);
                }
            }
        } catch (Exception e) {
            LOG.warn("H2InterfaceComponent.load: error loading key={} : {}", key, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void save(String key, InterfaceComponentSummary component) {
        if (key == null || key.isEmpty() || component == null) return;
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            String subtype = (component instanceof IngressInterfaceComponentSummary) ? "INGRESS" : "EGRESS";
            try (PreparedStatement up = c.prepareStatement("MERGE INTO APP_INTERFACE(if_key, name, specialization, documentation, subtype) KEY(if_key) VALUES(?,?,?,?,?)")) {
                up.setString(1, key);
                up.setString(2, safe(component.getName()));
                up.setString(3, safe(component.getSpecialization()));
                up.setString(4, safe(component.getDocumentation()));
                up.setString(5, subtype);
                up.executeUpdate();
            }
        } catch (Exception e) {
            LOG.warn("H2InterfaceComponent.save: error persisting key={} : {}", key, e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isEmpty()) return;
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM APP_INTERFACE WHERE if_key=?")) {
                ps.setString(1, key);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            LOG.warn("H2InterfaceComponent.delete: error deleting key={} : {}", key, e.getMessage(), e);
        }
    }

    private static String safe(String v) { return v; }
}
