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

import net.fhirfactory.dricats.internals.tasking.InternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Default no-op implementation to keep the module self-contained.
 * Applications can provide their own @ApplicationScoped bean implementing ITaskPersistenceService
 * which CDI will select instead of this one (use @Alternative with higher priority if needed).
 */
@ApplicationScoped
@javax.enterprise.inject.Alternative
public class TaskPersistenceService implements ITaskPersistenceService {
    private static final Logger LOG = LoggerFactory.getLogger(TaskPersistenceService.class);

    @Override
    public Optional<InternalTask> load(String key) {
        LOG.trace("TaskPersistenceService.load({})", key);
        return Optional.empty();
    }

    @Override
    public void save(String key, InternalTask task) {
        LOG.trace("TaskPersistenceService.save({}, {})", key, task);
    }

    @Override
    public void delete(String key) {
        LOG.trace("TaskPersistenceService.delete({})", key);
    }
}
