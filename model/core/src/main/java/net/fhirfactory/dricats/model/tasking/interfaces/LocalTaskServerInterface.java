package net.fhirfactory.dricats.model.tasking.interfaces;

import java.time.LocalDateTime;

import net.fhirfactory.dricats.model.tasking.InternalTask;

public interface LocalTaskServerInterface {
    public LocalDateTime addTask(InternalTask task);
    public InternalTask getTask(String taskId);
}
