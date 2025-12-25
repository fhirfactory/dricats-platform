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
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkUnitProcessor extends ApplicationComponent {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -123456789032191L;
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessor.class);


    //
     // Constructor(s)
    //

    public WorkUnitProcessor() {
        super();
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType());
    }

    //
    // "bean" methods
    //

    @JsonIgnore
    public List<ElementReference> getIngresInterfaces(){
        ArrayList<ElementReference> ingresInterfaces = new ArrayList<>();
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getObjectSpecialisation();
            ApplicationComponentSpecialisationEnum specialisationEnum = ApplicationComponentSpecialisationEnum.fromCode(specialisation);
            if(specialisationEnum == ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES ){
                ingresInterfaces.add(reference);
            }
        }
        return(ingresInterfaces);
    }

    @JsonIgnore
    public void setIngresInterfaces(List<ElementReference> ingresInterfaces){
        for(ElementReference reference : ingresInterfaces){
            String specialisation = reference.getObjectSpecialisation();
            ApplicationComponentSpecialisationEnum specialisationEnum = ApplicationComponentSpecialisationEnum.fromCode(specialisation);
            if(specialisationEnum == ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES ){
                getInterfaces().add(reference);
            }
        }
    }

    @JsonIgnore
    public List<ElementReference> getEgressInterfaces(){
        ArrayList<ElementReference> egressInterfaces = new ArrayList<>();
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getObjectSpecialisation();
            ApplicationComponentSpecialisationEnum specialisationEnum = ApplicationComponentSpecialisationEnum.fromCode(specialisation);
            if(specialisationEnum == ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS ){
                egressInterfaces.add(reference);
            }
        }
        return(egressInterfaces);
    }

    @JsonIgnore
    public void setEgresInterfaces(List<ElementReference> egresInterfaces){
        for(ElementReference reference : egresInterfaces){
            String specialisation = reference.getObjectSpecialisation();
            ApplicationComponentSpecialisationEnum specialisationEnum = ApplicationComponentSpecialisationEnum.fromCode(specialisation);
            if(specialisationEnum == ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS ){
                getInterfaces().add(reference);
            }
        }
    }

    //
    // Utility Methods
    //
    @Override
    protected Logger getLogger(){
        return(LOG);
    }
}
