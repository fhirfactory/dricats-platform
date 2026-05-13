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
package net.fhirfactory.dricats.components.hl7.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.components.common.SingleInOutEndpoint;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class HL7v2xBase extends SingleInOutEndpoint {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(HL7v2xBase.class);

    //
    // Attributes
    //



    //
    // Constructor(s)
    //

    public HL7v2xBase(){
        super();
    }

    public HL7v2xBase(ElementReference parent, String name, String documentation){
        super(parent, name, documentation);
    }

    //
    // Methods
    //

    @JsonIgnore
    public String getMLLPCommonComponentConfigurationItem(MLLPComponentOptionsEnum configurationItem){
        String componentConfigurationItem = getExtensionValue(configurationItem.getName());
        if(StringUtils.isEmpty(componentConfigurationItem)){
            componentConfigurationItem = "";
        }
        return (componentConfigurationItem);
    }

    @JsonIgnore
    public void setValidateMessageStructure(MLLPComponentOptionsEnum configurationItem, String value){
        getExtensions().put(configurationItem.getName(), value);
    }

    @JsonIgnore
    public URI getEgressEndpointURI(){
        URI uri = null;
        if(getEgressEndpoint() == null || getEgressEndpoint().getURI() == null){
            try {
                uri = new URI("mllp://");
            } catch( URISyntaxException e ){
                LOG.warn("Failed to create URI for egress endpoint: {}", e.getMessage());
            }
        } else {
            uri = getEgressEndpoint().getURI();
        }
        return(uri);
    }

    public enum MLLPComponentOptionsEnum{
        MLLP_COMPONENT_ACCEPT_TIMEOUT("camel-mllp-v2x-accept-timeout",  "acceptTimeout", "Timeout (in milliseconds) while waiting for a TCP connection TCP Server Only."),
        MLLP_COMPONENT_AUTO_ACK_FLAG("camel-mllp-v2x-auto-ack-flag", "autoAck", "Enable/Disable the automatic generation of a MLLP Acknowledgement MLLP Consumers only."),
        MLLP_COMPONENT_AUTO_WIRED_ENABLED_FLAG("camel-mllp-v2x-auto-wired-enabled-flag", "autowiredEnabled","Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection factories, AWS Clients, etc."),
        MLLP_COMPONENT_BACKLOG("camel-mllp-v2x-backlog", "backlog", "The maximum queue length for incoming connection indications (a request to connect) is set to the backlog parameter. If a connection indication arrives when the queue is full, the connection is refused."),
        MLLP_COMPONENT_BIND_RETRY_INTERVAL("camel-mllp-v2x-bind-retry-interval", "bindRetryInterval","TCP Server Only - The number of milliseconds to wait between bind attempts."),
        MLLP_COMPONENT_BIND_TIMEOUT("camel-mllp-v2x-bind-timeout", "bindTimeout", "TCP Server Only - The number of milliseconds to retry binding to a server port."), MLLP_COMPONENT_BRIDGE_ERROR_HANDLER("camel-mllp-v2x-bridge-error-handler", "bridgeErrorHandler", "Allows for bridging the consumer to the Camel routing Error Handler, which mean any exceptions occurred while the consumer is trying to receive incoming messages, or the likes, will now be processed as a message and handled by the routing Error Handler. If disabled, the consumer will use the org.apache.camel.spi.ExceptionHandler to deal with exceptions by logging them at WARN or ERROR level and ignored."),
        MLLP_COMPONENT_CHARSET_NAME( "camel-mllp-v2x-charset-name", "charsetName", "Sets the default charset to use."),
        MLLP_COMPONENT_CONFIGURATION("camel-mllp-v2x-configuration", "configuration","Sets the default configuration to use when creating MLLP endpoints."),
        MLLP_COMPONENT_CONNECT_TIMEOUT("camel-mllp-v2x-connect-timeout", "connectTimeout", "Timeout (in milliseconds) for establishing for a TCP connection TCP Client only."),
        MLLP_COMPONENT_DEFAULT_CHARSET("camel-mllp-v2x-default-charset", "defaultCharset","Set the default character set to use for byte to/from String conversions."),
        MLLP_COMPONENT_EXCHANGE_PATTERN("camel-mllp-v2x-exchange-pattern", "exchangePattern", "Sets the exchange pattern when the consumer creates an exchange. Enum values:InOnly, InOut}"),
        MLLP_COMPONENT_IDLE_TIMEOUT("camel-mllp-v2x-idle-timeout", "idleTimeout", "The approximate idle time allowed before the Client TCP Connection will be reset. A null value or a value less than or equal to zero will disable the idle timeout."),
        MLLP_COMPONENT_IDLE_TIMEOUT_STRATEGY("camel-mllp-v2x-idle-timeout-strategy", "idleTimeoutStrategy", "decide what action to take when idle timeout occurs. Possible values are : RESET: set SO_LINGER to 0 and reset the socket CLOSE: close the socket gracefully default is RESET. Enum values:{ RESET, CLOSE }" ),
        MLLP_COMPONENT_KEEP_ALIVE("camel-mllp-v2x-keep-alive-flag", "keepAlive", "Enable/disable the SO_KEEPALIVE socket option."),
        MLLP_COMPONENT_HL7_HEADERS("camel-mllp-v2x-hl7_headers", "hl7Headers","Enable/Disable the automatic generation of message headers from the HL7 Message MLLP Consumers only."),
        MLLP_COMPONENT_LAZY_START_PRODUCER("camel-mllp-v2x-lazy-start-producer", "lazyStartProducer", "cWhether the producer should be started lazy (on the first message). By starting lazy you can use this to allow CamelContext and routes to startup in situations where a producer may otherwise fail during starting and cause the route to fail being started. By deferring this startup to be lazy then the startup failure can be handled during routing messages via Camel’s routing error handlers. Beware that when the first message is processed then creating and starting the producer may take a little time and prolong the total processing time of the processing."),
        MLLP_COMPONENT_LENIENT_BIND("camel-mllp-v2x-lenient-bind-flag", "lenientBind", "TCP Server Only - Allow the endpoint to start before the TCP ServerSocket is bound. In some environments, it may be desirable to allow the endpoint to start before the TCP ServerSocket is bound."),
        MLLP_COMPONENT_LOG_PHI("camel-mllp-v2x-log-phi-flag", "logPhi","Whether to log PHI."),
        MLLP_COMPONENT_LOG_PHI_MAX_BYTES("camel-mllp-v2x-log-phi-max-bytes", "logPhiMaxBytes", "Set the maximum number of bytes of PHI that will be logged in a log entry."),
        MLLP_COMPONENT_MAX_BUFFER_SIZE("camel-mllp-v2x-max-buffer-size", "maxBufferSize","Maximum buffer size used when receiving or sending data over the wire."),
        MLLP_COMPONENT_MAX_CONCURRENT_CONSUMERS("camel-mllp-v2x-max-concurrent-consumers", "maxConcurrentConsumers", "The maximum number of concurrent MLLP Consumer connections that will be allowed. If a new connection is received and the maximum is number are already established, the new connection will be reset immediately."),
        MLLP_COMPONENT_MIN_BUFFER_SIZE("camel-mllp-v2x-min-buffer-size", "minBufferSize", "Minimum buffer size used when receiving or sending data over the wire."),
        MLLP_COMPONENT_READ_TIMEOUT("camel-mllp-v2x-read-timeout", "readTimeout", "The SO_TIMEOUT value (in milliseconds) used after the start of an MLLP frame has been received."),
        MLLP_COMPONENT_RECEIVE_BUFFER_SIZE("camel-mllp-v2x-receive-buffer-size", "receiveBufferSize","Sets the SO_RCVBUF option to the specified value (in bytes)."),
        MLLP_COMPONENT_RECEIVE_TIMEOUT("camel-mllp-v2x-receive-timeout", "receiveTimeout", "The SO_TIMEOUT value (in milliseconds) used when waiting for the start of an MLLP frame."),
        MLLP_COMPONENT_REQUIRE_END_OF_DATA( "camel-mllp-v2x-require-end-of-data", "requireEndOfData", "Enable/Disable strict compliance to the MLLP standard. The MLLP standard specifies START_OF_BLOCKhl7 payloadEND_OF_BLOCKEND_OF_DATA, however, some systems do not send the final END_OF_DATA byte. This setting controls whether or not the final END_OF_DATA byte is required or optional."),
        MLLP_COMPONENT_REUSE_ADDRESS("camel-mllp-v2x-reuse-address", "reuseAddress", "Enable/disable the SO_REUSEADDR socket option."),
        MLLP_COMPONENT_SEND_BUFFER_SIZE("camel-mllp-v2x-send-buffer-size", "sendBufferSize", "Sets the SO_SNDBUF option to the specified value (in bytes)."),
        MLLP_COMPONENT_SSL_CONTEXT_PARAMETERS("camel-mllp-v2x-ssl-context-parameters", "sslContextParameters", "Sets the SSLContextParameters for securing TCP connections. If set, the MLLP component will use SSL/TLS for securing both producer and consumer TCP connections. This allows the configuration of trust stores, key stores, protocols, and other SSL/TLS settings. If not set, the MLLP component will use plain TCP communication."),
        MLLP_COMPONENT_STRING_PAYLOAD_FLAG("camel-mllp-v2x-string-payload-flag", "stringPayload", "Enable/Disable converting the payload to a String. If enabled, HL7 Payloads received from external systems will be validated converted to a String. If the charsetName property is set, that character set will be used for the conversion. If the charsetName property is not set, the value of MSH-18 will be used to determine th appropriate character set. If MSH-18 is not set, then the default ISO-8859-1 character set will be use."),
        MLLP_COMPONENT_TCP_NO_DELAY_FLAG("camel-mllp-v2x-tcp-no-delay-flag", "tcpNoDelay", "Enable/disable the TCP_NODELAY socket option."),
        MLLP_COMPONENT_USE_GLOBAL_SSL_CONTEXT_PARAMETERS_FLAG("camel-mllp-v2x-use-global-ssl-context-parameters-flag", "useGlobalSslContextParameters","Enable usage of global SSL context parameters."),
        MLLP_COMPONENT_VALIDATE_FLAG("camel-mllp-v2x-validate-flag", "validatePayload", "Enable/Disable the validation of HL7 Payloads If enabled, HL7 Payloads received from external systems will be validated (see Hl7Util.generateInvalidPayloadExceptionMessage for details on the validation). If and invalid payload is detected, a MllpInvalidMessageException (for consumers) or a MllpInvalidAcknowledgementException will be thrown.");

        private String name;
        private String parameterName;
        private String description;

        private MLLPComponentOptionsEnum(String name, String parameterName, String description){
            this.name = name;
            this.description = description;
            this.parameterName = parameterName;
        }

        public String getName(){
            return( this.name );
        }

        public String getDescription() {
            return (this.description);
        }

        public String getParameterName(){
            return( this.parameterName );
        }

        public MLLPComponentOptionsEnum fromName(String name){
            for(MLLPComponentOptionsEnum enumInstance: MLLPComponentOptionsEnum.values()){
                if(StringUtils.compareIgnoreCase(enumInstance.getName(), name) == 0){
                    return(enumInstance);
                }
            }
            return(null);
        }
    }
}
