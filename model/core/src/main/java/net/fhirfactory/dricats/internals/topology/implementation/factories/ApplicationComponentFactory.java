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
package net.fhirfactory.dricats.internals.topology.implementation.factories;

import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.*;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationInterface;
import org.apache.commons.lang3.SerializationUtils;

import java.util.HashMap;

import static net.fhirfactory.dricats.reference.archimate.common.ElementBase.DEFAULT_ELEMENT_SPECIALISATION;

@ApplicationScoped
public class ApplicationComponentFactory {

    public ApplicationComponent createApplicationComponent(
            ElementReference parent,
            String name,
            String doc,
            ApplicationComponentSpecialisationEnum componentType)
    {
        ApplicationComponent applicationComponent;
        boolean createdIdentifier = false;
        switch (componentType) {
            case SOLUTION:
                applicationComponent = new Solution(null, name, doc, new HashMap<>());
                createdIdentifier = true;
                break;
            case SUBSYSTEM:
                applicationComponent = new DefaultSubsystem(parent, name,doc,new HashMap<String,String>());
                createdIdentifier = true;
                break;
            case SUBSYSTEM_APPLICATION_CLUSTER:
                applicationComponent = new DefaultSubsystemCluster();
                break;
            case SUBSYSTEM_APPLICATION_INSTANCE:
                applicationComponent = new DefaultSubsystemInstance();
                break;
            case SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR:
                applicationComponent = new WorkUnitProcessor();
                break;
            default:
                applicationComponent = new ApplicationComponent();
                break;
        }

        applicationComponent.setParent(parent);
        applicationComponent.setShortName(name);
        applicationComponent.setDocumentation(doc);
        applicationComponent.setSpecialization(componentType.getType());
        if(!createdIdentifier){
            DistinguishedName fdn;
            String specialization = componentType.getType();
            if(specialization == null || specialization.isEmpty()) {
                specialization = DEFAULT_ELEMENT_SPECIALISATION;
            }
            RelativeDistinguishedName rdn = new RelativeDistinguishedName(specialization, name);
            if(parent != null) {
                fdn = new DistinguishedName(parent.getElementIdentifier().getIdentifierValue());
            } else {
                fdn = new DistinguishedName();
            }
            fdn.appendUnqualifiedName(rdn);
            ElementIdentifier elementIdentifier = new ElementIdentifier(fdn);
            applicationComponent.setIdentifier(elementIdentifier);
        }

        return applicationComponent;
    }
}
