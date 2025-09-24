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
package net.fhirfactory.dricats.datagrid.central.taskgrid.h2;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry;
import net.fhirfactory.dricats.internals.tasking.InternalTask;
import net.fhirfactory.dricats.datagrid.central.taskgrid.spi.ITaskPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * H2-backed implementation of the ITaskPersistenceService.
 *
 * Schema (3rd Normal Form oriented):
 *  - TASK(task_key PK, id_value)
 *  - OBJECT_ID(task_key PK/FK -> TASK.task_key, eff_start, eff_end)
 *  - QUALIFIED_NAME_PART(task_key FK -> TASK.task_key, seq, qualifier, value, PK(task_key, seq))
 *
 * Connection properties (System properties):
 *  - task.store.h2.url (default: jdbc:h2:~/dricats-task;AUTO_SERVER=TRUE)
 *  - task.store.h2.user (default: sa)
 *  - task.store.h2.pass (default: "")
 */
@ApplicationScoped
@Alternative
@Priority(1)
public class H2TaskPersistenceService implements ITaskPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(H2TaskPersistenceService.class);

    private volatile boolean initialised = false;

    private String jdbcUrl() {
        return System.getProperty("task.store.h2.url", "jdbc:h2:~/dricats-task;AUTO_SERVER=TRUE");
    }
    private String jdbcUser() {
        return System.getProperty("task.store.h2.user", "sa");
    }
    private String jdbcPass() {
        return System.getProperty("task.store.h2.pass", "");
    }

    private void initIfNeeded() {
        if (initialised) return;
        synchronized (this) {
            if (initialised) return;
            try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass());
                 Statement st = c.createStatement()) {
                c.setAutoCommit(true);
                st.execute("CREATE TABLE IF NOT EXISTS TASK (" +
                        " task_key VARCHAR(255) PRIMARY KEY," +
                        " id_value VARCHAR(1024)" +
                        ")");

                st.execute("CREATE TABLE IF NOT EXISTS OBJECT_ID (" +
                        " task_key VARCHAR(255) PRIMARY KEY," +
                        " eff_start TIMESTAMP," +
                        " eff_end TIMESTAMP," +
                        " CONSTRAINT FK_OBJECT_ID_TASK FOREIGN KEY (task_key) REFERENCES TASK(task_key) ON DELETE CASCADE" +
                        ")");

                st.execute("CREATE TABLE IF NOT EXISTS QUALIFIED_NAME_PART (" +
                        " task_key VARCHAR(255) NOT NULL," +
                        " seq INT NOT NULL," +
                        " qualifier VARCHAR(255)," +
                        " value VARCHAR(2048)," +
                        " PRIMARY KEY(task_key, seq)," +
                        " CONSTRAINT FK_QN_TASK FOREIGN KEY (task_key) REFERENCES TASK(task_key) ON DELETE CASCADE" +
                        ")");

                initialised = true;
                LOG.info("H2TaskPersistenceService initialised (schema ready) on url={}", jdbcUrl());
            } catch (SQLException e) {
                LOG.error("Failed to initialise H2 schema", e);
                throw new RuntimeException("Failed to initialise H2 schema", e);
            }
        }
    }

    @Override
    public Optional<InternalTask> load(String key) {
        if (key == null || key.isEmpty()) return Optional.empty();
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            c.setAutoCommit(false);
            String idValue = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT id_value FROM TASK WHERE task_key=?")) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        return Optional.empty();
                    }
                    idValue = rs.getString(1);
                }
            }

            LocalDateTime effStart = null;
            LocalDateTime effEnd = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT eff_start, eff_end FROM OBJECT_ID WHERE task_key=?")) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Timestamp s = rs.getTimestamp(1);
                        Timestamp e = rs.getTimestamp(2);
                        effStart = (s != null ? s.toLocalDateTime() : null);
                        effEnd = (e != null ? e.toLocalDateTime() : null);
                    }
                }
            }

            List<UnqualifiedNameEntry> parts = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT seq, qualifier, value FROM QUALIFIED_NAME_PART WHERE task_key=? ORDER BY seq ASC")) {
                ps.setString(1, key);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int seq = rs.getInt(1);
                        String q = rs.getString(2);
                        String v = rs.getString(3);
                        UnqualifiedNameEntry entry = new UnqualifiedNameEntry(q, v);
                        entry.setSequenceNumber(seq);
                        parts.add(entry);
                    }
                }
            }

            InternalTask t = new InternalTask();
            if (idValue != null && !idValue.isEmpty()) {
                t.setId(new CommonName(idValue));
            }
            // Build DistributableObjectId
            DistributableObjectId oid = new DistributableObjectId();
            QualifiedName qn = new QualifiedName();
            for (UnqualifiedNameEntry p : parts) {
                UnqualifiedName u = new UnqualifiedName(p.getQualifier(), p.getValue());
                qn.appendUnqualifiedName(u);
            }
            oid.setQualifiedName(qn);
            EffectiveDate ed = new EffectiveDate();
            if (effStart != null) ed.setEffectiveStartDate(effStart);
            if (effEnd != null) ed.setEffectiveEndDate(effEnd);
            oid.setEffectiveDate(ed);
            oid.setId(qn.getCommonName());
            t.setObjectID(oid);

            c.commit();
            return Optional.of(t);
        } catch (Exception e) {
            LOG.warn("H2TaskPersistenceService.load: error loading key={} : {}", key, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public void save(String key, InternalTask task) {
        if (key == null || key.isEmpty() || task == null) return;
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            c.setAutoCommit(false);
            // Upsert TASK
            String idVal = null;
            try {
                CommonName cn = task.getId();
                if (cn != null && cn.getValue() != null) idVal = cn.getValue();
            } catch (Exception ignore) {}

            try (PreparedStatement up = c.prepareStatement("MERGE INTO TASK(task_key, id_value) KEY(task_key) VALUES(?,?)")) {
                up.setString(1, key);
                up.setString(2, idVal);
                up.executeUpdate();
            }

            // Upsert OBJECT_ID
            LocalDateTime es = null; LocalDateTime ee = null; List<UnqualifiedNameEntry> parts = new ArrayList<>();
            try {
                DistributableObjectId oid = task.getObjectID();
                if (oid != null) {
                    if (oid.getEffectiveDate() != null) {
                        es = oid.getEffectiveDate().getEffectiveStartDate();
                        ee = oid.getEffectiveDate().getEffectiveEndDate();
                    }
                    if (oid.getQualifiedName() != null) {
                        Map<Integer, UnqualifiedNameEntry> m = oid.getQualifiedName().getUnqualifiedNameEntries();
                        for (int i = 0; i < oid.getQualifiedName().getRelativeDNCount(); i++) {
                            UnqualifiedNameEntry e = m.get(i);
                            if (e != null) parts.add(e);
                        }
                    }
                }
            } catch (Exception ignore) {}

            try (PreparedStatement up = c.prepareStatement("MERGE INTO OBJECT_ID(task_key, eff_start, eff_end) KEY(task_key) VALUES(?,?,?)")) {
                up.setString(1, key);
                up.setTimestamp(2, es != null ? Timestamp.valueOf(es) : null);
                up.setTimestamp(3, ee != null ? Timestamp.valueOf(ee) : null);
                up.executeUpdate();
            }

            // Replace QUALIFIED_NAME_PART rows for key
            try (PreparedStatement del = c.prepareStatement("DELETE FROM QUALIFIED_NAME_PART WHERE task_key=?")) {
                del.setString(1, key);
                del.executeUpdate();
            }
            if (!parts.isEmpty()) {
                try (PreparedStatement ins = c.prepareStatement("INSERT INTO QUALIFIED_NAME_PART(task_key, seq, qualifier, value) VALUES(?,?,?,?)")) {
                    int seq = 0;
                    for (UnqualifiedNameEntry p : parts) {
                        ins.setString(1, key);
                        ins.setInt(2, seq++);
                        ins.setString(3, p.getQualifier());
                        ins.setString(4, p.getValue());
                        ins.addBatch();
                    }
                    ins.executeBatch();
                }
            }

            c.commit();
        } catch (Exception e) {
            LOG.warn("H2TaskPersistenceService.save: error persisting key={} : {}", key, e.getMessage(), e);
        }
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isEmpty()) return;
        initIfNeeded();
        try (Connection c = DriverManager.getConnection(jdbcUrl(), jdbcUser(), jdbcPass())) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM TASK WHERE task_key=?")) {
                ps.setString(1, key);
                ps.executeUpdate();
            }
            c.commit();
        } catch (Exception e) {
            LOG.warn("H2TaskPersistenceService.delete: error deleting key={} : {}", key, e.getMessage(), e);
        }
    }
}
