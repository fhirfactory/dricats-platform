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
package net.fhirfactory.dricats.datagrid.central.taskgrid.spi;

import net.fhirfactory.dricats.internals.pubsub.MessageSubscription;

import java.util.Optional;

/**
 * SPI for plugging a custom persistence service behind the MessageSubscription cache.
 *
 * Implement this interface in your application/module and expose it as a CDI bean (@ApplicationScoped)
 * to have the distributed cache delegate persistence operations to it.
 */
public interface IMessageSubscriptionPersistenceService {
    /**
     * Load a MessageSubscription by its cache key.
     * @param key cache key
     * @return Optional containing the MessageSubscription if found
     */
    Optional<MessageSubscription> load(String key);

    /**
     * Persist a MessageSubscription by its cache key. Implementations should insert or update.
     * @param key cache key
     * @param subscription subscription to persist
     */
    void save(String key, MessageSubscription subscription);

    /**
     * Delete a MessageSubscription by its cache key, if present.
     * @param key cache key
     */
    void delete(String key);
}
