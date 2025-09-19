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
package net.fhirfactory.dricats.internals.configuration.ports.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.configuration.ports.datatypes.IPPortInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

public class ServerInterfaceConfigurationObject extends InterfaceConfigurationObject {
	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 7470709568612414300L;
	private static final Logger LOG = LoggerFactory.getLogger(ServerInterfaceConfigurationObject.class);
	
	private IPPortInterface port;
    private boolean encrypted;
    private ArrayList<InterfaceDefinitionConfigurationObject> supportedInterfaceProfiles;
    private int startupDelay;

    private WebServicePortConfigurationObject servicePorts;

    //
    // Abstract Methods
    //

    @JsonIgnore
    @Override
    protected Logger getLogger() {
    	return (LOG);
    }

    //
    // Constructor(s)
    //

    public ServerInterfaceConfigurationObject(){
        super();
        this.port = new IPPortInterface();
        this.supportedInterfaceProfiles = new ArrayList<>();
        this.encrypted = false;
        this.startupDelay = 0;
        this.servicePorts = new WebServicePortConfigurationObject();
    }

    public ServerInterfaceConfigurationObject(String id){
        super(id);
        this.port = new IPPortInterface();
        this.supportedInterfaceProfiles = new ArrayList<>();
        this.encrypted = false;
        this.startupDelay = 0;
        this.servicePorts = new WebServicePortConfigurationObject();
    }

    //
    // Getters and Setters
    //

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public ArrayList<InterfaceDefinitionConfigurationObject> getSupportedInterfaceProfiles() {
        return supportedInterfaceProfiles;
    }

    public void setSupportedInterfaceProfiles(ArrayList<InterfaceDefinitionConfigurationObject> supportedInterfaceProfiles) {
        this.supportedInterfaceProfiles = supportedInterfaceProfiles;
    }

    public int getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(int startupDelay) {
        this.startupDelay = startupDelay;
    }
    
    public WebServicePortConfigurationObject getServicePorts() {
		return servicePorts;
	}

	public void setServicePorts(WebServicePortConfigurationObject servicePorts) {
		this.servicePorts = servicePorts;
	}

    public IPPortInterface getPort() {
        return port;
    }

    public void setPort(IPPortInterface port) {
        this.port = port;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", ServerInterfaceConfigurationObject.class.getSimpleName() + "[", "]")
                .add("port=" + getPort())
                .add("encrypted=" + isEncrypted())
                .add("supportedInterfaceProfiles=" + getSupportedInterfaceProfiles())
                .add("startupDelay=" + getStartupDelay())
                .add("servicePorts=" + getServicePorts())
                .add("configurationParameters=" + getConfigurationParameters())
                .add("name='" + getId() + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServerInterfaceConfigurationObject that = (ServerInterfaceConfigurationObject) o;
        return isEncrypted() == that.isEncrypted() && getStartupDelay() == that.getStartupDelay() && Objects.equals(getPort(), that.getPort()) && Objects.equals(getSupportedInterfaceProfiles(), that.getSupportedInterfaceProfiles()) && Objects.equals(getServicePorts(), that.getServicePorts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPort(), isEncrypted(), getSupportedInterfaceProfiles(), getStartupDelay(), getServicePorts());
    }
}
