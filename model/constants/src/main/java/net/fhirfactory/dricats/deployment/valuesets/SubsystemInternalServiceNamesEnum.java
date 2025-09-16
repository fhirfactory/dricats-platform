package net.fhirfactory.dricats.deployment.valuesets;

public enum SubsystemInternalServiceNamesEnum {
    NOTIFICATION_SERVICE_ENDPOINT("DRICaTS-Internal-Service-NotificationEndpoint"),
    MESSAGE_SERVICE_ENDPOINT("DRICaTS-Internal-Service-MessageEndpoint"),
    METRICS_SERVICE_ENDPOINT("DRICaTS-Internal-Service-MetricsEndpoint"),
    OAM_SERVICE_ENDPOINT("DRICaTS-Internal-Service-OAMEndpoint");

    private String name;

    private SubsystemInternalServiceNamesEnum(String name){
        this.name = name;
    }

    public String getName(){
        return(name);
    }
}
