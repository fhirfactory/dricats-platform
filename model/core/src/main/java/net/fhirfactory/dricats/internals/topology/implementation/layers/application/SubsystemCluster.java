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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application;

import java.io.Serial;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.topology.implementation.common.TopologyComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SubsystemCluster extends TopologyComponent {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678904093L;
    private static final Logger LOG = LoggerFactory.getLogger(SubsystemCluster.class);
    
    //
    // Attributes
    //

    //
    // Constructor(s)
    //

    public SubsystemCluster(){
        super();
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_CLUSTER.getType());

    }

    //
    // abstract methods
    //

    abstract ElementIdentifier specifySubsystemIdentifier();

    //
    // Getters and Setters
    //


    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }




}
