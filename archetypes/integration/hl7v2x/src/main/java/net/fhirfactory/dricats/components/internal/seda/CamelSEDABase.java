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
package net.fhirfactory.dricats.components.internal.seda;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.components.common.SingleInOutEndpoint;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class CamelSEDABase extends SingleInOutEndpoint {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(CamelSEDABase.class);

    //
    // Attributes
    //

    //
    // Constructor(s)
    //

    public CamelSEDABase() {
        super();
    }

    public CamelSEDABase(ElementReference parent, String name, String documentation) {
        super(parent, name, documentation);
    }

    //
    // Methods
    //

    @JsonIgnore
    public String getSEDACommonComponentConfigurationItem(SEDAComponentOptionsEnum configurationItem) {
        String componentConfigurationItem = getExtensionValue(configurationItem.getName());
        if (StringUtils.isEmpty(componentConfigurationItem)) {
            componentConfigurationItem = "";
        }
        return (componentConfigurationItem);
    }

    @JsonIgnore
    public void setSEDACommonComponentConfigurationItem(SEDAComponentOptionsEnum configurationItem, String value) {
        getExtensions().put(configurationItem.getName(), value);
    }

    @JsonIgnore
    public URI getEgressEndpointURI() {
        URI uri = null;
        if (getEgressEndpoint() == null || getEgressEndpoint().getURI() == null) {
            try {
                uri = new URI("seda://");
            } catch (URISyntaxException e) {
                LOG.warn("Failed to create URI for egress endpoint: {}", e.getMessage());
            }
        } else {
            uri = getEgressEndpoint().getURI();
        }
        return (uri);
    }

    public enum SEDAComponentOptionsEnum {
        SEDA_COMPONENT_BRIDGE_ERROR_HANDLER("camel-seda-bridge-error-handler", "bridgeErrorHandler", "Allows for bridging the consumer to the Camel routing Error Handler, which mean any exceptions occurred while the consumer is trying to receive incoming messages, or the likes, will now be processed as a message and handled by the routing Error Handler. If disabled, the consumer will use the org.apache.camel.spi.ExceptionHandler to deal with exceptions by logging them at WARN or ERROR level and ignored."),
        SEDA_COMPONENT_CONCURRENT_CONSUMERS("camel-seda-concurrent-consumers", "concurrentConsumers", "Sets the default number of concurrent threads processing exchanges."),
        SEDA_COMPONENT_DEFAULT_QUEUE_FACTORY("camel-seda-default-queue-factory", "defaultQueueFactory", "Sets the default queue factory."),
        SEDA_COMPONENT_LAZY_START_PRODUCER("camel-seda-lazy-start-producer", "lazyStartProducer", "Whether the producer should be started lazy (on the first message). By starting lazy you can use this to allow CamelContext and routes to startup in situations where a producer may otherwise fail during starting and cause the route to fail being started. By deferring this startup to be lazy then the startup failure can be handled during routing messages via Camel’s routing error handlers. Beware that when the first message is processed then creating and starting the producer may take a little time and prolong the total processing time of the processing."),
        SEDA_COMPONENT_QUEUE_SIZE("camel-seda-queue-size", "queueSize", "Sets the default maximum capacity of the SEDA queue (i.e., the number of messages it can hold). Default value is 1000."),
        SEDA_COMPONENT_AUTOWIRED_ENABLED("camel-seda-autowired-enabled", "autowiredEnabled", "Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection factories, AWS Clients, etc."),
        SEDA_COMPONENT_DEFAULT_BLOCK_WHEN_FULL("camel-seda-default-block-when-full", "defaultBlockWhenFull", "Whether a thread that sends messages to a full SEDA queue will block until the queue's capacity is no longer exhausted. By default, an exception will be thrown stating that the queue is full. By enabling this option, the calling thread will instead block and wait until the message can be accepted."),
        SEDA_COMPONENT_DEFAULT_DISCARD_WHEN_FULL("camel-seda-default-discard-when-full", "defaultDiscardWhenFull", "Whether a thread that sends messages to a full SEDA queue will be discarded. By default, an exception will be thrown stating that the queue is full. By enabling this option, the calling thread will instead discard the message and continue."),
        SEDA_COMPONENT_DEFAULT_OFFER_TIMEOUT("camel-seda-default-offer-timeout", "defaultOfferTimeout", "Whether a thread that sends messages to a full SEDA queue will block until the queue's capacity is no longer exhausted. By default, an exception will be thrown stating that the queue is full. By enabling this option, the calling thread will instead block and wait until the message can be accepted, but only for the specified timeout value (in milliseconds).");

        private String name;
        private String parameterName;
        private String description;

        private SEDAComponentOptionsEnum(String name, String parameterName, String description) {
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

        public SEDAComponentOptionsEnum fromName(String name) {
            for (SEDAComponentOptionsEnum enumInstance : SEDAComponentOptionsEnum.values()) {
                if (StringUtils.compareIgnoreCase(enumInstance.getName(), name) == 0) {
                    return (enumInstance);
                }
            }
            return (null);
        }
    }
}
