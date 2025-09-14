/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.model.configuration.ports.internal;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import net.fhirfactory.dricats.model.configuration.ports.base.ServerInterfaceConfigurationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JGroupsInterfaceInterfaceConfigurationObject extends ServerInterfaceConfigurationObject {
	//
	// Housekeeping
	//
	@Serial
    private static final long serialVersionUID = 7944620523903438701L;
	private static Logger LOG = LoggerFactory.getLogger(JGroupsInterfaceInterfaceConfigurationObject.class);

    //
    // Attributes
    //
    private List<JGroupsInitialHostSegment> initialHosts;

    //
    // Constructors
    //

    public JGroupsInterfaceInterfaceConfigurationObject(){
        super();
        initialHosts = new ArrayList<>();
    }

    public JGroupsInterfaceInterfaceConfigurationObject(String id){
        super(id);
        initialHosts = new ArrayList<>();
    }

    //
    // Getters and Setters
    //

    @Override
    protected Logger getLogger() {
        return (LOG);
    }



    public List<JGroupsInitialHostSegment> getInitialHosts() {
        return initialHosts;
    }

    public void setInitialHosts(List<JGroupsInitialHostSegment> initialHosts) {
        this.initialHosts = initialHosts;
    }

    //
    // toString()
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", JGroupsInterfaceInterfaceConfigurationObject.class.getSimpleName() + "[", "]")
                .add("initialHosts=" + getInitialHosts())
                .add("encrypted=" + isEncrypted())
                .add("supportedInterfaceProfiles=" + getSupportedInterfaceProfiles())
                .add("startupDelay=" + getStartupDelay())
                .add("servicePorts=" + getServicePorts())
                .add("port=" + getPort())
                .add("configurationParameters=" + getConfigurationParameters())
                .add("name='" + getId() + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        JGroupsInterfaceInterfaceConfigurationObject that = (JGroupsInterfaceInterfaceConfigurationObject) o;
        return Objects.equals(getInitialHosts(), that.getInitialHosts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getInitialHosts());
    }
}
