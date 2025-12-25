package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets;

import net.fhirfactory.dricats.internals.datatypes.CodeableConcept;
import net.fhirfactory.dricats.internals.datatypes.CodeableConceptCode;

import java.net.URI;

public enum InterfaceComponentTypeEnum {
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
}
