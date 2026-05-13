/*
 * Copyright (c) 2026 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets;

import net.fhirfactory.dricats.internals.datatypes.CodeableConcept;
import net.fhirfactory.dricats.internals.datatypes.CodeableConceptCode;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;

import java.net.URI;

public enum InterfaceComponentTypeEnum {
    GENERIC_RECEIVER_INTERFACE("GenericReceiver", "Generic Receiver", "dricats.interface-specialisation.generic-receiver", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    GENERIC_SENDER_INTERFACE("GenericSender", "Generic Sender", "dricats.interface-specialisation.generic-sender", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    GENERIC_GRID_INTERFACE("GenericSender", "Generic Sender", "dricats.interface-specialisation.generic-sender", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    HTTP_SERVER_INTERFACE ("HTTPServer","HTTP Server", "dricats.interface-specialisation.http-server", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    HTTP_CLIENT_INTERFACE ("HTTPClient","HTTP Client", "dricats.interface-specialisation.http-client", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    MLLP_RECEIVER_INTERFACE("MLLPReceiver","MLLP Receiver", "dricats.interface-specialisation.mllp-receiver", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    MLLP_SENDER_INTERFACE("MLLPSender","MLLP Sender", "dricats.interface-specialisation.mllp-sender", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    JGROUPS_MESSAGING_INTERFACE ("JGroupsMessaging","JGroups Messaging", "dricats.interface-specialisation.jgroups-messaging", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    JGROUPS_RMI_INTERFACE ("JGroupsRMI","JGroups RMI Interface", "dricats.interface-specialisation.jgroups-rmi", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    INTERNAL_CAMEL_RECEIVER_INTERFACE ("InternalCamelReceiver","Internal Camel Receiver Interface", "dricats.interface-specialisation.internal-camel-receiver", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    INTERNAL_CAMEL_SENDER_INTERFACE ("InternalCamelSender","Internal Camel Sender Interface", "dricats.interface-specialisation.internal-camel-sender", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes"),
    INTERNAL_DATAGRID ("InternalDataGrid", "Internal Data Grid", "dricats.interface-specialisation.internal-datagrid", "https://fhirfactory.net/CodeSystems/InterfaceSpecialisationTypes");


    private final String name;
    private final String code;
    private final String display;
    private final URI system;

    private InterfaceComponentTypeEnum(String name, String display, String code, String system){
        this.name = name;
        this.code = code;
        this.display = display;
        this.system = URI.create(system);
    }

    public String getName(){
        return(name);
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

    public static InterfaceComponentTypeEnum fromName(String testName){
        for(InterfaceComponentTypeEnum b : InterfaceComponentTypeEnum.values()){
            if(b.getName().equals(testName)){
                return(b);
            }
        }
        return(null);
    }

    public static InterfaceComponentTypeEnum fromCode(String testCode){
        for(InterfaceComponentTypeEnum b : InterfaceComponentTypeEnum.values()){
            if(b.getCode().equals(testCode)){
                return(b);
            }
        }
        return(null);
    }

    public boolean isReceiverInterface(){
        if(this == GENERIC_RECEIVER_INTERFACE || this == HTTP_SERVER_INTERFACE || this == MLLP_RECEIVER_INTERFACE || this == INTERNAL_CAMEL_RECEIVER_INTERFACE){
            return(true);
        }
        return(false);
    }

    public boolean isSenderInterface(){
        if(this == GENERIC_SENDER_INTERFACE || this == HTTP_CLIENT_INTERFACE || this == MLLP_SENDER_INTERFACE || this == INTERNAL_CAMEL_SENDER_INTERFACE){
            return(true);
        }
        return(false);
    }

    public boolean isGridInterface(){
        if(this == GENERIC_GRID_INTERFACE || this == JGROUPS_MESSAGING_INTERFACE || this == JGROUPS_RMI_INTERFACE || this == INTERNAL_DATAGRID){
            return(true);
        }
        return(false);
    }
}
