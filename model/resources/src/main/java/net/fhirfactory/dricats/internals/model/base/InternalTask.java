/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.internals.model.base;

import net.fhirfactory.dricats.internals.model.base.dataytypes.InternalTaskProvenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.List;

public class InternalTask extends DistributableObject {    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900070L;
    private static final Logger LOG = LoggerFactory.getLogger(InternalTask.class);

    //
    // Attributes
    //

    private List<InternalTaskPayload> taskInputPayload;
    private List<InternalTaskPayload> taskOutputPayload;
    private InternalTaskProvenance taskProvenance;

    //
    // Bean Methods
    //

    public List<InternalTaskPayload> getTaskInputPayload() {
        return taskInputPayload;
    }

    public void setTaskInputPayload(List<InternalTaskPayload> taskInputPayload) {
        this.taskInputPayload = taskInputPayload;
    }

    public List<InternalTaskPayload> getTaskOutputPayload() {
        return taskOutputPayload;
    }

    public void setTaskOutputPayload(List<InternalTaskPayload> taskOutputPayload) {
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
}
