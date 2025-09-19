package net.fhirfactory.dricats.internals.tasking.interfaces;

import java.time.LocalDateTime;

import net.fhirfactory.dricats.internals.tasking.InternalTask;

public interface LocalTaskServerInterface {
    public LocalDateTime addTask(InternalTask task);
    public InternalTask getTask(String taskId);
}
