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
package net.fhirfactory.dricats.model.tasking;

import java.io.Serial;
import java.util.List;

import net.fhirfactory.dricats.model.data.Payload;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.model.base.dataytypes.InternalTaskProvenance;
import net.fhirfactory.dricats.model.common.DistributableObject;

public class InternalTask extends DistributableObject {    //
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

    //
    // Bean Methods
    //

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
                .appendSuper(super.toString())
                .toString();
    }
}
