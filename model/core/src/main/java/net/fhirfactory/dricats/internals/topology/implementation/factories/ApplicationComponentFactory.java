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

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@ApplicationScoped
public class ApplicationComponentFactory {

    


    public ApplicationComponent createApplicationComponent(
            ElementReference parent,
            String name,
            String doc,
            ApplicationComponentSpecialisationEnum componentType)
    {
        ApplicationComponent applicationComponent;
        switch (componentType) {
            case SUBSYSTEM:
                applicationComponent = new Subsystem() {
                    @Override
                    protected ElementIdentifier specifySubsystemIdentifier() {

                    }
                };
                break;
            case SUBSYSTEM_APPLICATION_CLUSTER:
                applicationComponent = new ApplicationClusterSummary();
                break;
            case SUBSYSTEM_APPLICATION_INSTANCE:
                applicationComponent = new ApplicationInstanceSummary();
                break;
            case SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR_GROUP:
                applicationComponent = new WUPGroupSummary();
                break;
            case SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR:
            case SUBSYSTEM_APPLICATION_WUP_INTERFACE_INGRES:
            case SUBSYSTEM_APPLICATION_WUP_INTERFACE_EGRESS:
                applicationComponent = new WUPSummary();
                break;
            default:
                applicationComponent = new WUPSummary();
        }
        applicationComponent.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        applicationComponent.getMetadata().setCreationDate(LocalDateTime.now());
        applicationComponent.getMetadata().setLastUpdateDate(LocalDateTime.now());
        applicationComponent.getComponentStatus().setHeartbeatInstant(LocalDateTime.now());
        applicationComponent.getComponentStatus().setLastActivityInstant(LocalDateTime.now());
        applicationComponent.setSpecialization(componentType.getType());
        applicationComponent.setParent(parent);
        applicationComponent.setComponentStatus(new ApplicationComponentStatusSummary());
        applicationComponent.getComponentStatus().setComponentStatus("OK");
        applicationComponent.getComponentStatus().setComponentStatusDescription("Component Operating within normal parameters");
        FullyDistinguishedName qualifiedName;
        if(parent == null){
            qualifiedName = new FullyDistinguishedName();
        } else {
            qualifiedName = new FullyDistinguishedName(parent.getQualifiedName());
        }
        RelativeDistinguishedName unqName = new RelativeDistinguishedName(componentType.getType(), name);
        qualifiedName.appendUnqualifiedName(unqName);
        applicationComponent.setName(name);
        applicationComponent.setDocumentation(doc);
        applicationComponent.setObjectID(new DistributableObjectId(qualifiedName));
        getTestComponentServices().getComponents().put(applicationComponent.resolveKey(), applicationComponent);
        return applicationComponent;
    }
}
}
