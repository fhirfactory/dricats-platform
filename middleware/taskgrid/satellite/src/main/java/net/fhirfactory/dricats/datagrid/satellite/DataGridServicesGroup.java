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

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ObjectId subsystemId = getSubsystem().getSubsystem().getObjectId();
        FullyDistinguishedName subsystemQualifiedName = subsystemId.getFullyDistinguishedName();
        FullyDistinguishedName myQualifiedName = new FullyDistinguishedName(subsystemQualifiedName);
        myQualifiedName.appendUnqualifiedName(new RelativeDistinguishedName(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP.getType(), "DataGrid"));
        ObjectId myId = new ObjectId(myQualifiedName);
        setObjectId(myId);
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
