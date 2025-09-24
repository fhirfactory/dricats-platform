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
package net.fhirfactory.dricats.datagrid.satellite;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serial;
import java.io.Serializable;

@ApplicationScoped
public class DataGridServicesGroup extends ApplicationComponent implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(DataGridServicesGroup.class);

    //
     // Attributes
    //

    @Inject
    private ISubsystem subsystem;

    //
    // Constructor(s)
    //

    public DataGridServicesGroup() {
        super();
        LOG.debug("Initialising...");
    }

    //
    // Post-Construct Initialisation
    //

    @PostConstruct
    public void initialisation(){
        getLogger().debug(".initialisation(): Entry");
        getLogger().info(".initialisation(): [Resolve DistributableObjectId] Start");
        DistributableObjectId subsystemId = getSubsystem().getSubsystem().getObjectID();
        QualifiedName subsystemQualifiedName = subsystemId.getQualifiedName();
        QualifiedName myQualifiedName = new QualifiedName(subsystemQualifiedName);
        myQualifiedName.appendUnqualifiedName(new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK.getType(), "DataGrid"));
        DistributableObjectId myId = new DistributableObjectId(myQualifiedName);
        setObjectID(myId);
        getLogger().info(".initialisation(): [Resolve DistributableObjectId] End");
        getLogger().debug(".initialisation(): Exit");
    }


    //
    // Accessors and Mutators
    //

    protected ISubsystem getSubsystem() {
        return subsystem;
    }
}
