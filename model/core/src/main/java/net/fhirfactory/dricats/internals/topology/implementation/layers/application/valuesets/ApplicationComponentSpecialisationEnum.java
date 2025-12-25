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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets;


import net.fhirfactory.dricats.internals.datatypes.CodeableConcept;
import net.fhirfactory.dricats.internals.datatypes.CodeableConceptCode;

import java.net.URI;

public enum ApplicationComponentSpecialisationEnum {
	SUBSYSTEM ("Subsystem","Subsystem", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_CLUSTER ("SubsystemApplicationCluster","Subsystem Application Cluster", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_INSTANCE ("SubsystemApplicationInstance","Subsystem Application Instance", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR ("WorkUnitProcessor","Work Unit Processor", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP("WorkUnitProcessorGroup","Work Unit Processor Group", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
    SUBSYSTEM_APPLICATION_WUP_INTERFACE("WorkUnitProcessorInterface","Work Unit Processor Interface", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
    SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES("WorkUnitProcessorIngresInterface","Work Unit Processor Ingres Interface", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
    SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS("WorkUnitProcessorEgressInterface","Work Unit Processor Egress Interface", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_WUP_INTERFACE_ADAPTER("InterfaceAdapter","Interface Adapter", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SOLUTION ("Solution", "Solution", "dricats.applications-component.name", "https://fhirfactory.net/CodeSystems/Types");


    private final String type;
	private final String code;
	private final String display;
	private final URI system;

	private ApplicationComponentSpecialisationEnum(String type, String display, String code, String system){
		this.type = type;
        this.code = code;
		this.display = display;
		this.system = URI.create(system);
	}

    public String getType(){
        return(type);
    }

	public String getCode() {
		return code;
	}

	public String getDisplay() {
		return display;
	}

	public URI getSystem() {
		return system;
	}

	public CodeableConcept toDistributableObjectType() {
        CodeableConcept codeableConcept = new CodeableConcept();
        CodeableConceptCode codeableConceptCode = new CodeableConceptCode();
        codeableConceptCode.setCode(getCode());
        codeableConceptCode.setDisplay(getDisplay());
        codeableConceptCode.setSystem(getSystem().toString());
        codeableConcept.getCoding().add(codeableConceptCode);
		return(codeableConcept);
	}

    public static ApplicationComponentSpecialisationEnum fromCode(String testCode){
        for(ApplicationComponentSpecialisationEnum b : ApplicationComponentSpecialisationEnum.values()){
            if(b.getCode().equals(testCode)){
                return(b);
            }
        }
        return(null);
    }
}
