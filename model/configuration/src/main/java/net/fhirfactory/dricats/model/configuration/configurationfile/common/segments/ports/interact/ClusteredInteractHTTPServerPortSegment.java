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
package net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.ports.interact;

import net.fhirfactory.dricats.model.configuration.configurationfile.common.segments.connectedsystems.ConnectedSystemProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusteredInteractHTTPServerPortSegment extends ClusteredInteractServerPortSegment {
    private static Logger LOG = LoggerFactory.getLogger(ClusteredInteractHTTPServerPortSegment.class);

    private String webServicePath;

    @Override
    protected Logger specifyLogger(){
        return(LOG);
    }

    public ConnectedSystemProperties getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(ConnectedSystemProperties targetSystem) {
        this.targetSystem = targetSystem;
    }

    private ConnectedSystemProperties targetSystem;

    public String getWebServicePath() {
        return webServicePath;
    }

    public void setWebServicePath(String webServicePath) {
        this.webServicePath = webServicePath;
    }

    @Override
    public String toString() {
        return "HTTPClusteredServiceInteractPortSegment{" +
                "targetSystem=" + targetSystem +
                '}';
    }
}
