package net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardInteractHTTPClientPortSegment extends StandardExternalFacingPort{
    private static Logger LOG = LoggerFactory.getLogger(StandardInteractHTTPClientPortSegment.class);

    private int defaultRetryCount;
    private int defaultRetryWait;
    private int defaultTimeout;
    private String contextPath;

    //
    // Constructor(s)
    //

    public StandardInteractHTTPClientPortSegment() {
        super();
        this.defaultRetryCount = 3;
        this.defaultRetryWait = 30;
        this.defaultTimeout = 60;
        this.contextPath = null;
    }

    //
    // Getters and Setters
    //

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public int getDefaultRetryCount() {
        return defaultRetryCount;
    }

    public void setDefaultRetryCount(int defaultRetryCount) {
        this.defaultRetryCount = defaultRetryCount;
    }

    public int getDefaultRetryWait() {
        return defaultRetryWait;
    }

    public void setDefaultRetryWait(int defaultRetryWait) {
        this.defaultRetryWait = defaultRetryWait;
    }

    public int getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(int defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "StandardInteractClientPortSegment{" +
                "name=" + getName() +
                ", otherConfigurationParameters=" + getOtherConfigurationParameters() +
                ", portValue=" + getPortValue() +
                ", portType=" + getPortType() +
                ", server=" + isServer() +
                ", encrypted=" + isEncrypted() +
                ", supportedInterfaceProfiles=" + getSupportedInterfaceProfiles() +
                ", hostDNSEntry=" + getHostDNSEntry() +
                ", startupDelay=" + getStartupDelay() +
                ", contextPath=" + contextPath +
                ", connectedSystem=" + getConnectedSystem() +
                ", defaultRetryCount=" + defaultRetryCount +
                ", defaultRetryWait=" + defaultRetryWait +
                ", defaultTimeout=" + defaultTimeout +
                '}';
    }
}
