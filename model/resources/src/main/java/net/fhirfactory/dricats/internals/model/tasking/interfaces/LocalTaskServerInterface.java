package net.fhirfactory.dricats.internals.model.tasking.interfaces;

import net.fhirfactory.dricats.internals.model.tasking.InternalTask;

import java.time.LocalDateTime;

public interface LocalTaskServerInterface {
    public LocalDateTime addTask(InternalTask task);
    public InternalTask getTask(String taskId);
}
