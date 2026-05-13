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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.common.TopologyComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class Solution extends TopologyComponent {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900093L;
    private static final Logger LOG = LoggerFactory.getLogger(Solution.class);

    //
    // Attributes
    //


    //
    // Constructor(s)
    //

    public Solution(){
        super();
        setSpecialization(ApplicationComponentSpecialisationEnum.SOLUTION.getType());
    }

    public Solution(ElementReference parent, String name, String documentation, Map<String, String> extensions ){
        super(parent, name, documentation, ApplicationComponentSpecialisationEnum.SOLUTION.toString(), extensions );
    }

    //
    // Getters and Setters
    //

    @JsonIgnore
    public List<ElementReference> getSubsystems() {
        return getSubComponents();
    }

    public void setSubsystems(List<ElementReference> subsystems) {
        getSubComponents().clear();
        if (subsystems != null) {
            getSubComponents().addAll(subsystems);
        }
    }


    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return(LOG);
    }


}
