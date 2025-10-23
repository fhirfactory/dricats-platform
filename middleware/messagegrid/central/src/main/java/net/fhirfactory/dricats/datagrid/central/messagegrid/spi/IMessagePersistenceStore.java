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
package net.fhirfactory.dricats.datagrid.central.messagegrid.spi;

import java.util.List;

/**
 * SPI for pluggable message persistence used by KafkaMessageBroker.
 * Implementations should be ApplicationScoped beans when used with CDI.
 */
public interface IMessagePersistenceStore {
    /**
     * Whether the persistence store is enabled and should be used.
     */
    boolean enabled();

    /**
     * Initialize underlying resources (connections, tables, etc.).
     */
    void start() throws Exception;

    /**
     * Cleanup/close underlying resources.
     */
    void stop();

    /**
     * Persist a message payload. Should return a unique persistence id (pid).
     */
    String persist(String topic, String key, byte[] payload) throws Exception;

    /**
     * Delete a previously persisted message by pid.
     */
    void delete(String pid) throws Exception;

    /**
     * Load a batch of pending messages ordered by oldest first.
     */
    List<PendingMessage> loadBatch(int limit) throws Exception;
}
