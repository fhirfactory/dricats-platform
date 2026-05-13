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
package net.fhirfactory.dricats.components.internal.direct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.components.common.SingleInOutEndpoint;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class CamelDirectBase extends SingleInOutEndpoint {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(CamelDirectBase.class);

    //
    // Attributes
    //

    //
    // Constructor(s)
    //

    public CamelDirectBase() {
        super();
    }

    public CamelDirectBase(ElementReference parent, String name, String documentation) {
        super(parent, name, documentation);
    }

    //
    // Methods
    //

    @JsonIgnore
    public String getDirectCommonComponentConfigurationItem(DirectComponentOptionsEnum configurationItem) {
        String componentConfigurationItem = getExtensionValue(configurationItem.getName());
        if (StringUtils.isEmpty(componentConfigurationItem)) {
            componentConfigurationItem = "";
        }
        return (componentConfigurationItem);
    }

    @JsonIgnore
    public void setDirectCommonComponentConfigurationItem(DirectComponentOptionsEnum configurationItem, String value) {
        getExtensions().put(configurationItem.getName(), value);
    }

    @JsonIgnore
    public URI getEgressEndpointURI() {
        URI uri = null;
        if (getEgressEndpoint() == null || getEgressEndpoint().getURI() == null) {
            try {
                uri = new URI("direct://");
            } catch (URISyntaxException e) {
                LOG.warn("Failed to create URI for egress endpoint: {}", e.getMessage());
            }
        } else {
            uri = getEgressEndpoint().getURI();
        }
        return (uri);
    }

    public enum DirectComponentOptionsEnum {
        DIRECT_COMPONENT_BRIDGE_ERROR_HANDLER("camel-direct-bridge-error-handler", "bridgeErrorHandler", "Allows for bridging the consumer to the Camel routing Error Handler, which mean any exceptions occurred while the consumer is trying to receive incoming messages, or the likes, will now be processed as a message and handled by the routing Error Handler. If disabled, the consumer will use the org.apache.camel.spi.ExceptionHandler to deal with exceptions by logging them at WARN or ERROR level and ignored."),
        DIRECT_COMPONENT_LAZY_START_PRODUCER("camel-direct-lazy-start-producer", "lazyStartProducer", "Whether the producer should be started lazy (on the first message). By starting lazy you can use this to allow CamelContext and routes to startup in situations where a producer may otherwise fail during starting and cause the route to fail being started. By deferring this startup to be lazy then the startup failure can be handled during routing messages via Camel’s routing error handlers. Beware that when the first message is processed then creating and starting the producer may take a little time and prolong the total processing time of the processing."),
        DIRECT_COMPONENT_AUTOWIRED_ENABLED("camel-direct-autowired-enabled", "autowiredEnabled", "Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection factories, AWS Clients, etc.");

        private String name;
        private String parameterName;
        private String description;

        private DirectComponentOptionsEnum(String name, String parameterName, String description) {
            this.name = name;
            this.description = description;
            this.parameterName = parameterName;
        }

        public String getName() {
            return (this.name);
        }

        public String getDescription() {
            return (this.description);
        }

        public String getParameterName() {
            return (this.parameterName);
        }

        public DirectComponentOptionsEnum fromName(String name) {
            for (DirectComponentOptionsEnum enumInstance : DirectComponentOptionsEnum.values()) {
                if (StringUtils.compareIgnoreCase(enumInstance.getName(), name) == 0) {
                    return (enumInstance);
                }
            }
            return (null);
        }
    }
}
