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
package net.fhirfactory.dricats.middleware.auditing.server;

import jakarta.enterprise.context.ApplicationScoped;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.persistence.jdbc.common.configuration.PooledConnectionFactoryConfigurationBuilder;
import org.infinispan.persistence.jdbc.configuration.JdbcStringBasedStoreConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates persistence configuration for the ApplicationAuditEvent cache.
 * Currently configures an Infinispan JDBC String-based store backed by H2.
 *
 * System properties used (with defaults):
 * - dricats.audit.grid.jdbc.url (default: jdbc:h2:file:./data/dricats-auditing;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE)
 * - dricats.audit.grid.jdbc.user (default: sa)
 * - dricats.audit.grid.jdbc.pass (default: empty)
 */
@ApplicationScoped
public class AuditEventPersistenceConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(AuditEventPersistenceConfigurator.class);

    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPass;

    public AuditEventPersistenceConfigurator() {
        this.jdbcUrl = System.getProperty("dricats.audit.grid.jdbc.url", "jdbc:h2:file:./data/dricats-auditing;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE");
        this.jdbcUser = System.getProperty("dricats.audit.grid.jdbc.user", "sa");
        this.jdbcPass = System.getProperty("dricats.audit.grid.jdbc.pass", "");
    }

    /**
     * Apply persistence configuration to the provided cache ConfigurationBuilder.
     */
    public void apply(ConfigurationBuilder builder) {
        JdbcStringBasedStoreConfigurationBuilder store = builder.persistence()
                .addStore(JdbcStringBasedStoreConfigurationBuilder.class)
                .segmented(false);
        store.key2StringMapper("org.infinispan.persistence.keymappers.DefaultTwoWayKey2StringMapper");
        store.table()
                .tableNamePrefix("AUDIT_EVENTS_")
                .idColumnName("ID").idColumnType("VARCHAR(255)")
                .dataColumnName("DATA").dataColumnType("BLOB")
                .timestampColumnName("TS").timestampColumnType("BIGINT");

        PooledConnectionFactoryConfigurationBuilder pcf = store.connectionFactory(PooledConnectionFactoryConfigurationBuilder.class);
        pcf.driverClass("org.h2.Driver")
                .connectionUrl(jdbcUrl)
                .username(jdbcUser)
                .password(jdbcPass);
        LOG.info("AuditEventPersistenceConfigurator: Configured JDBC/H2 store at {} (user='{}')", jdbcUrl, jdbcUser);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
