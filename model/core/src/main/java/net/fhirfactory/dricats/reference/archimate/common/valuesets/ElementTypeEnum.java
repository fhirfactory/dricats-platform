package net.fhirfactory.dricats.reference.archimate.common.valuesets;

public enum ElementTypeEnum {
    // Application layer
    APPLICATION_COLLABORATION("ApplicationCollaboration", "Archimate -> Application Layer - Application Collaboration"),
    APPLICATION_COMPONENT("ApplicationComponent", "Archimate -> Application Layer - Application Component"),
    APPLICATION_DATA_OBJECT("ApplicationDataObject", "Archimate -> Application Layer - Application Data Object"),
    APPLICATION_SERVICE("ApplicationService", "Archimate -> Application Layer - Application Service"),
    APPLICATION_FUNCTION("ApplicationFunction", "Archimate -> Application Layer - Application Function"),
    APPLICATION_INTERFACE("ApplicationInterface", "Archimate -> Application Layer - Application Interface"),
    APPLICATION_PROCESS("ApplicationProcess", "Archimate -> Application Layer - Application Process"),
    APPLICATION_INTERACTION("ApplicationInteraction", "Archimate -> Application Layer - Application Interaction"),
    APPLICATION_EVENT("ApplicationEvent", "Archimate -> Application Layer - Application Event"),

    // Business layer
    BUSINESS_ACTOR("BusinessActor", "Archimate -> Business Layer - Business Actor"),
    BUSINESS_ROLE("BusinessRole", "Archimate -> Business Layer - Business Role"),
    BUSINESS_COLLABORATION("BusinessCollaboration", "Archimate -> Business Layer - Business Collaboration"),
    BUSINESS_INTERFACE("BusinessInterface", "Archimate -> Business Layer - Business Interface"),
    BUSINESS_PROCESS("BusinessProcess", "Archimate -> Business Layer - Business Process"),
    BUSINESS_FUNCTION("BusinessFunction", "Archimate -> Business Layer - Business Function"),
    BUSINESS_INTERACTION("BusinessInteraction", "Archimate -> Business Layer - Business Interaction"),
    BUSINESS_EVENT("BusinessEvent", "Archimate -> Business Layer - Business Event"),
    BUSINESS_SERVICE("BusinessService", "Archimate -> Business Layer - Business Service"),
    BUSINESS_OBJECT("BusinessObject", "Archimate -> Business Layer - Business Object"),
    CONTRACT("Contract", "Archimate -> Business Layer - Contract"),
    REPRESENTATION("Representation", "Archimate -> Business Layer - Representation"),
    PRODUCT("Product", "Archimate -> Business Layer - Product"),
    VALUE("Value", "Archimate -> Business Layer - Value"),

    // Technology layer
    ARTIFACT("Artifact", "Archimate -> Technology Layer - Artifact"),
    COMMUNICATION_NETWORK("CommunicationNetwork", "Archimate -> Technology Layer - Communication Network"),
    DEVICE("Device", "Archimate -> Technology Layer - Device"),
    DISTRIBUTION_NETWORK("DistributionNetwork", "Archimate -> Technology Layer - Distribution Network"),
    EQUIPMENT("Equipment", "Archimate -> Technology Layer - Equipment"),
    FACILITY("Facility", "Archimate -> Technology Layer - Facility"),
    MATERIAL("Material", "Archimate -> Technology Layer - Material"),
    NODE("Node", "Archimate -> Technology Layer - Node"),
    PATH("Path", "Archimate -> Technology Layer - Path"),
    SYSTEM_SOFTWARE("SystemSoftware", "Archimate -> Technology Layer - System Software"),
    TECHNOLOGY_INTERFACE("TechnologyInterface", "Archimate -> Technology Layer - Technology Interface"),
    TECHNOLOGY_COLLABORATION("TechnologyCollaboration", "Archimate -> Technology Layer - Technology Collaboration"),
    TECHNOLOGY_FUNCTION("TechnologyFunction", "Archimate -> Technology Layer - Technology Function"),
    TECHNOLOGY_INTERACTION("TechnologyInteraction", "Archimate -> Technology Layer - Technology Interaction"),
    TECHNOLOGY_PROCESS("TechnologyProcess", "Archimate -> Technology Layer - Technology Process"),
    TECHNOLOGY_SERVICE("TechnologyService", "Archimate -> Technology Layer - Technology Service");
    
    private String name; 
    private String description;
    
    private ElementTypeEnum(String name, String description){
        this.name = name;
        this.description = description;
    }
    
    public String getName(){
        return(this.name);
    }
    
    public String getDescription(){
        return(this.description);
    }
}
