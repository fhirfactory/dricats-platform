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
package net.fhirfactory.dricats.model.topology.implementation.layers.application.valuesets;


import net.fhirfactory.dricats.model.common.DistributableObjectIdentifier;
import net.fhirfactory.dricats.model.common.datatypes.CodeableConcept;
import net.fhirfactory.dricats.model.common.datatypes.CodeableConceptCode;

import java.net.URI;

public enum SoftwareComponentTypeEnum {
	SUBSYSTEM ("Subsystem","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_CLUSTER ("SubsystemApplicationCluster","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_INSTANCE ("SubsystemApplicationInstance","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR ("WorkUnitProcessor","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_BLOCK ("WorkUnitProcessorBlock","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	NETWORK_ENDPOINT ("NetworkEndpoint","SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types"),
	SOLUTION ("Solution", "SoftwareComponentName", "dricats.s.applications-component.name", "https://fhirfactory.net/CodeSystems/Types");


    private final String type;
	private final String code;
	private final String display;
	private final URI system;

	private SoftwareComponentTypeEnum(String type, String display, String code, String system){
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
        codeableConcept.getCode().add(codeableConceptCode);
		return(codeableConcept);
	}
}
