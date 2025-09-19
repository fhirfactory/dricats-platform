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

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

public abstract class SolutionNode extends ApplicationComponent {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900093L;
    private static final Logger LOG = LoggerFactory.getLogger(SolutionNode.class);

    //
    //
    //
    private Set<DistributableObjectId> subsystems = new HashSet<DistributableObjectId>();

    //
    // Constructor(s)
    //

    public SolutionNode(){
        super();
    }

    //
    // abstract methods
    //

    abstract DistributableObjectIdentifier specifySubsystemIdentifier();

    //
    // Getters and Setters
    //

    public Set<DistributableObjectId> getSubsystems() {
        return subsystems;
    }

    public void setSubsystems(Set<DistributableObjectId> subsystems) {
        this.subsystems = subsystems;
    }


    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }


}
