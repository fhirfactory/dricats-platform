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
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkUnitProcessor extends TopologyComponent {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -123456789032191L;


    //
     // Constructor(s)
    //

    public WorkUnitProcessor() {
        super();
        setSpecialization(ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType());
    }

    public WorkUnitProcessor(ElementReference parent, String name, String documentation){
        super(parent, name, documentation, ApplicationComponentSpecialisationEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType(), new HashMap<>());
    }

    //
    // "bean" methods
    //

    @JsonIgnore
    public List<ElementReference> getIngresInterfaces(){
        ArrayList<ElementReference> ingresInterfaces = new ArrayList<>();
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isReceiverInterface() ){
                ingresInterfaces.add(reference);
            }
        }
        return(ingresInterfaces);
    }

    @JsonIgnore
    public void addIngresInterface(IngresApplicationInterface ingresInterface){
        getInterfaces().add(ingresInterface.getReference());
    }

    @JsonIgnore
    public void removeIngresInterface(IngresApplicationInterface ingresInterface){
        getInterfaces().remove(ingresInterface.getReference());
    }

    @JsonIgnore
    public void clearIngresInterfaces(){
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isReceiverInterface() ){
                getInterfaces().remove(reference);
            }
        }
    }

    @JsonIgnore
    public void setIngresInterfaces(List<ElementReference> ingresInterfaces){
        for(ElementReference reference : ingresInterfaces){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isReceiverInterface()){
                getInterfaces().add(reference);
            }
        }
    }

    @JsonIgnore
    public void clearEgressInterfaces(){
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isSenderInterface()){
                getInterfaces().remove(reference);
            }
        }
    }

    @JsonIgnore
    public List<ElementReference> getEgressInterfaces(){
        ArrayList<ElementReference> egressInterfaces = new ArrayList<>();
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isSenderInterface()){
                egressInterfaces.add(reference);
            }
        }
        return(egressInterfaces);
    }

    @JsonIgnore
    public void setEgressInterfaces(List<ElementReference> egresInterfaces){
        for(ElementReference reference : egresInterfaces){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            assert specialisationEnum != null;
            if(specialisationEnum.isSenderInterface() ){
                getInterfaces().add(reference);
            }
        }
    }

    @JsonIgnore
    public void addEgressInterface(EgressApplicationInterface egressInterface){
        getInterfaces().add(egressInterface.getReference());
    }

    @JsonIgnore
    public void removeEgressInterface(EgressApplicationInterface egressInterface){
        getInterfaces().remove(egressInterface.getReference());
    }

    @JsonIgnore
    public List<ElementReference> getGridInterfaces(){
        ArrayList<ElementReference> gridInterfaces = new ArrayList<>();
        for(ElementReference reference : getInterfaces()){
            String specialisation = reference.getElementSpecialisation();
            InterfaceComponentTypeEnum specialisationEnum = InterfaceComponentTypeEnum.fromName(specialisation);
            if(specialisationEnum != null && specialisationEnum.isGridInterface()){
                gridInterfaces.add(reference);
            }
        }
        return(gridInterfaces);
    }

    //
    // Utility Methods
    //

}
