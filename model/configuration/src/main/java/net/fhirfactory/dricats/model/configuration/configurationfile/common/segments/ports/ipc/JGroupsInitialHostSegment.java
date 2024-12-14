package net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.ipc;

public class JGroupsInitialHostSegment {
    private String hostName;
    private String portNumber;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public String toString() {
        return "JGroupsInitialHostSegment{" +
                "hostName=" + hostName +
                ", portNumber=" + portNumber +
                '}';
    }
}
