/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.ui.uitest.configuration;

import net.fhirfactory.dricats.internals.configuration.segments.ports.external.HTTPServerConfigurationObject;
import net.fhirfactory.dricats.model.configuration.configurationfile.base.BaseSubsystemConfigurationObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class UITestServerConfiguration extends BaseSubsystemConfigurationObject implements Serializable {
    //
     // Housekeeping
    //
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //

    private HTTPServerConfigurationObject httpServer;

    //
     // Bean Methods
    //

    public HTTPServerConfigurationObject getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HTTPServerConfigurationObject httpServer) {
        this.httpServer = httpServer;
    }


    //
     // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("httpServer", httpServer)
                .appendSuper(super.toString())
                .toString();
    }
}
