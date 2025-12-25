/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.ui.serverside.rest.api.base;

import com.fasterxml.jackson.core.JsonParseException;
import net.fhirfactory.dricats.internals.api.exceptions.ResourceNotFoundException;
import net.fhirfactory.dricats.internals.api.exceptions.ResourceUpdateException;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnExceptionDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;

public abstract class ResourceAPIBase extends RouteBuilder {
    //
    // Attributes
    //


    private static final String DEFAULT_SERVER_PORT = "12121";
    private static final String DEFAULT_SERVER_HOST = "0.0.0.0";
    private static final String DEFAULT_CONTEXT_PATH = "/";



    @Override
    public void configure() throws Exception {
    }


    protected String getPathSuffix() {
        String suffix = "?matchOnUriPrefix=true&option.enableCORS=true&option.corsAllowedCredentials=true";
        return (suffix);
    }

    abstract protected Logger getLogger();
    abstract protected String getResourceName();
    abstract protected Class getResourceClass();
    abstract protected String getResourceCollectionPath();


    public static String getServerPort() {
        return (DEFAULT_SERVER_PORT);
    }

    public static String getServerHost() {
        return DEFAULT_SERVER_HOST;
    }

    public static String getContextPath() {
        return DEFAULT_CONTEXT_PATH;
    }

    protected RestConfigurationDefinition getRestConfigurationDefinition() {
        RestConfigurationDefinition restConf = restConfiguration()
                .component("netty-http")
                .scheme("http")
                .port(getServerPort())
                .host(getServerHost())
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath(getContextPath())
                .enableCORS(true)
                .corsAllowCredentials(true)
                .corsHeaderProperty("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, login");
        return (restConf);
    }

    protected RestDefinition getRestGetDefinition() {
        RestDefinition restDef = rest(getResourceCollectionPath() + "/" + getResourceName())
            .get("/{commonName}").outType(getResourceClass())
                .to("direct:" + getResourceName() + "GET")
            .get("?pageSize={pageSize}&page={page}&sortBy={sortBy}&sortOrder={sortOrder}")
                .param().name("pageSize").type(RestParamType.query).required(false).endParam()
                .param().name("page").type(RestParamType.query).required(false).endParam()
                .param().name("sortBy").type(RestParamType.query).required(false).endParam()
                .param().name("sortOrder").type(RestParamType.query).required(false).endParam()
                .to("direct:" + getResourceName() + "ListGET");
        return (restDef);
    }

    protected OnExceptionDefinition getJsonParseException() {
        OnExceptionDefinition exceptionDef = onException(JsonParseException.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody(simple("Invalid json data\n"));
        return (exceptionDef);
    }

    protected OnExceptionDefinition getResourceNotFoundException() {
        OnExceptionDefinition exceptionDef = onException(ResourceNotFoundException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceNotFoundException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getResourceUpdateException() {
        OnExceptionDefinition exceptionDef = onException(ResourceUpdateException.class)
                .handled(true)
                .log(LoggingLevel.INFO, "ResourceUpdateException...")
                // use HTTP status 404 when data was not found
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setBody(simple("${exception.message}\n"));

        return(exceptionDef);
    }

    protected OnExceptionDefinition getGeneralException() {
        OnExceptionDefinition exceptionDef = onException(Exception.class)
                .handled(true)
                .log("[ResourceAPIBase] Exception handled: ${exception.class} - ${exception.message}")
                // use HTTP status 500 when we had a server side error
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .logStackTrace(true)
                .setBody(simple("${exception.message}\n"));
        return (exceptionDef);
    }
}
