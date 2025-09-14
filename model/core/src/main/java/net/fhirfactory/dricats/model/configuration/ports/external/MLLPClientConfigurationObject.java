package net.fhirfactory.dricats.model.configuration.ports.external;

import net.fhirfactory.dricats.model.configuration.ports.base.ClientInterfaceConfigurationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.StringJoiner;

public class MLLPClientConfigurationObject extends ClientInterfaceConfigurationObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900911L;
    private static Logger LOG = LoggerFactory.getLogger(MLLPClientConfigurationObject.class);

    private int defaultRetryCount;
    private int defaultRetryWait;
    private int defaultTimeout;
    private String contextPath;

    //
    // Constructor(s)
    //

    public MLLPClientConfigurationObject() {
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
        return new StringJoiner(", ", MLLPClientConfigurationObject.class.getSimpleName() + "[", "]")
                .add("defaultRetryCount=" + getDefaultRetryCount())
                .add("defaultRetryWait=" + getDefaultRetryWait())
                .add("defaultTimeout=" + getDefaultTimeout())
                .add("contextPath='" + getContextPath() + "'")
                .add("portType='" + getPortType() + "'")
                .add("portParameters='" + getPortParameters() + "'")
                .add("connectedSystem=" + getConnectedSystem())
                .add("name='" + getId() + "'")
                .add("configurationParameters=" + getConfigurationParameters())
                .add("name='" + getId() + "'")
                .toString();
    }
}
