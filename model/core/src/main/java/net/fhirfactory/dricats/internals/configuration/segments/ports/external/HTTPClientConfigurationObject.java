package net.fhirfactory.dricats.internals.configuration.segments.ports.external;

import net.fhirfactory.dricats.internals.configuration.segments.ports.base.ClientInterfaceConfigurationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.StringJoiner;

public class HTTPClientConfigurationObject extends ClientInterfaceConfigurationObject {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900911L;
    private static Logger LOG = LoggerFactory.getLogger(HTTPClientConfigurationObject.class);

    //
    // Attributes
    //

    private String contextPath;

    //
    // Constructor(s)
    //

    public HTTPClientConfigurationObject() {
        super();
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

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", HTTPClientConfigurationObject.class.getSimpleName() + "[", "]")
                .add("defaultRetryCount=" + getDefaultRetryCount())
                .add("defaultRetryWait=" + getDefaultRetryWait())
                .add("defaultTimeout=" + getDefaultTimeout())
                .add("portType='" + getPortType() + "'")
                .add("portParameters='" + getPortParameters() + "'")
                .add("connectedSystem=" + getConnectedSystem())
                .add("name='" + getId() + "'")
                .add("configurationParameters=" + getConfigurationParameters())
                .add("name='" + getId() + "'")
                .toString();
    }
}
