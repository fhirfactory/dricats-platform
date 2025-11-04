/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.InterfaceImplementationBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.StringJoiner;

public class HTTPInterface extends InterfaceImplementationBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900052L;
    private static final Logger LOG = LoggerFactory.getLogger(HTTPInterface.class);

    //
    // Constants
    //
    private static final String EXTERNAL_HTTP_SERVER = "HTTP_SERVER";
    private static final String EXTERNAL_HTTP_CLIENT = "HTTP_CLIENT";

    //
    // Constructor(s)
    //

    public HTTPInterface() {
        super();
        getLogger().trace("ApplicationInterface(): constructed");
    }

    public HTTPInterface(String name, String documentation, String interfaceSpecialisation) {
        super(name, documentation, interfaceSpecialisation);
        getLogger().trace("ApplicationInterface(name, documentation, interfaceSpecialisation): constructed");
    }

    public HTTPInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation) {
        super(parent, name, documentation, interfaceSpecialisation);
        getLogger().trace("ApplicationInterface(parent, name, documentation, interfaceSpecialisation): constructed");
    }

    public HTTPInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, URI uri) {
        super(parent, name, documentation, interfaceSpecialisation, uri);
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    //
    // Accessors / Mutators
    //

    @JsonIgnore
    public boolean isServer() {
        if (getSpecialization().contentEquals(EXTERNAL_HTTP_SERVER)) {
            return (true);
        }
        return (false);
    }

    protected Logger getLogger() {
        return LOG;
    }

    @JsonIgnore
    public boolean isClient() {
        if (getSpecialization().contentEquals(EXTERNAL_HTTP_CLIENT)) {
            return (true);
        }
        return (false);
    }

    @JsonIgnore
    public String getPath(){
        try {
            String url = getProperties().get(PROPERTY_URL);
            URI uri = URI.create(url);
            String path = uri.getPath();
            getLogger().trace("getPath(): Exit, path --> {}", path);
            return (path);
        } catch (Exception e) {
            getLogger().error("getPath(): Exception caught, {}, return empty String", e.getMessage());
            return ("");
        }

    }

    //
    // Utility Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof HTTPInterface)) {
            return false;
        }
        return true;
    }
}
