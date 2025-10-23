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
package net.fhirfactory.dricats.datagrid.satellite.taskgrid;

import net.fhirfactory.dricats.internals.tasking.InternalTask;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

/**
 * A thin client/facade to access the Infinispan-backed IAdministrativeTask store from other modules/applications.
 *
 * This client wraps TaskInfinispanStore and provides a simpler API surface for common operations.
 */
@ApplicationScoped
public class DistributedTaskCacheClient {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedTaskCacheClient.class);

    @Inject
    DistributedTaskCache store;

    /**
     * Save or update the task in the cache.
     * Returns the cache key used to store the task (derived from IAdministrativeTask/ObjectId or generated if absent).
     */
    public String save(InternalTask task) {
        if (task == null) {
            LOG.warn("save(InternalTask): task is null, ignoring");
            return null;
        }
        String key = store.resolveKey(task);
        store.put(task);
        LOG.debug("save(InternalTask): key={}", key);
        return key;
    }

    /**
     * Fetch a task by its cache key.
     */
    public Optional<InternalTask> get(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }
        InternalTask task = store.get(key);
        return Optional.ofNullable(task);
    }

    /**
     * Remove a task by its cache key. Returns true if a value was removed.
     */
    public boolean remove(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        InternalTask removed = store.remove(key);
        return removed != null;
    }

    /**
     * Check whether a task exists for the given key.
     */
    public boolean exists(String key) {
        return key != null && !key.isEmpty() && store.contains(key);
    }

    /**
     * Return the direct cache instance for advanced usage.
     */
    public Optional<Cache<String, InternalTask>> getCache() {
        return store.getCache();
    }

    /**
     * Publish a load request for a given key so that a store node will load it from persistence.
     */
    public void requestLoad(String key) {
        store.requestLoad(key);
    }

    /**
     * Convenience to request a load and wait up to the timeout for the IAdministrativeTask to appear.
     */
    public Optional<InternalTask> getOrRequestAndWait(String key, long timeoutMillis) {
        return store.getOrRequestAndWait(key, timeoutMillis);
    }

}
