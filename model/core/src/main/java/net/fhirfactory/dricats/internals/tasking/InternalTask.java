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
package net.fhirfactory.dricats.internals.tasking;

import net.fhirfactory.dricats.internals.data.Payload;
import net.fhirfactory.dricats.internals.tasking.datatypes.InternalTaskProvenance;
import net.fhirfactory.dricats.internals.tasking.valuesets.InternalTaskTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationProcess;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InternalTask extends ApplicationProcess implements Serializable {    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900070L;
    private static final Logger LOG = LoggerFactory.getLogger(InternalTask.class);

    //
    // Attributes
    //

    private List<Payload> taskInputPayload;
    private List<Payload> taskOutputPayload;
    private InternalTaskProvenance taskProvenance;
    private InternalTaskTypeEnum internalTaskSpecialisation;

    //
    // Constructor(s)
    //

    public InternalTask(){
        super();
        this.taskInputPayload = new ArrayList<>();
        this.taskOutputPayload = new ArrayList<>();
        this.taskProvenance = new InternalTaskProvenance();
        this.internalTaskSpecialisation = InternalTaskTypeEnum.INTERNAL_TASK_TYPE_ARCHETYPE;
    }

    //
    // Bean Methods
    //

    public InternalTaskTypeEnum getInternalTaskSpecialisation() {
        return internalTaskSpecialisation;
    }

    public void setInternalTaskSpecialisation(InternalTaskTypeEnum internalTaskSpecialisation) {
        this.internalTaskSpecialisation = internalTaskSpecialisation;
    }

    public List<Payload> getTaskInputPayload() {
        return taskInputPayload;
    }

    public void setTaskInputPayload(List<Payload> taskInputPayload) {
        this.taskInputPayload = taskInputPayload;
    }

    public List<Payload> getTaskOutputPayload() {
        return taskOutputPayload;
    }

    public void setTaskOutputPayload(List<Payload> taskOutputPayload) {
        this.taskOutputPayload = taskOutputPayload;
    }

    public InternalTaskProvenance getTaskProvenance() {
        return taskProvenance;
    }

    public void setTaskProvenance(InternalTaskProvenance taskProvenance) {
        this.taskProvenance = taskProvenance;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }

    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("taskInputPayload", taskInputPayload)
                .append("taskOutputPayload", taskOutputPayload)
                .append("taskProvenance", taskProvenance)
                .append("internalTaskSpecialisation", internalTaskSpecialisation)
                .appendSuper(super.toString())
                .toString();
    }
}
