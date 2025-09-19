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
package net.fhirfactory.dricats.internals.configuration.ports.internal;

import java.io.Serial;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.configuration.ports.base.InterfaceDefinitionConfigurationObject;

public class JGroupsInterfaceConfigurationObject extends JGroupsInterfaceInterfaceConfigurationObject {
	//
	// Housekeeping
	//
	@Serial
    private static final long serialVersionUID = 7944631523903438701L;
	private static Logger LOG = LoggerFactory.getLogger(JGroupsInterfaceConfigurationObject.class);
	
	//
	// Attributes
	//
	
    private String interfaceName;
    private String interfaceNameUniqueSuffix;
    private ArrayList<InterfaceDefinitionConfigurationObject> supportedInterfaceProfiles;

    //
    // Constructor(s)
    //
    
    public JGroupsInterfaceConfigurationObject(){
    	super();
        this.interfaceName = null;
        this.interfaceNameUniqueSuffix = null;
        this.supportedInterfaceProfiles = new ArrayList<>();
    }

    public JGroupsInterfaceConfigurationObject(String id){
        super(id);
        this.interfaceName = null;
        this.interfaceNameUniqueSuffix = null;
        this.supportedInterfaceProfiles = new ArrayList<>();
    }
    
    //
    // Bean Methods
    //

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceNameUniqueSuffix() {
        return interfaceNameUniqueSuffix;
    }

    public void setInterfaceNameUniqueSuffix(String interfaceNameUniqueSuffix) {
        this.interfaceNameUniqueSuffix = interfaceNameUniqueSuffix;
    }

    public ArrayList<InterfaceDefinitionConfigurationObject> getSupportedInterfaceProfiles() {
        return supportedInterfaceProfiles;
    }

    public void setSupportedInterfaceProfiles(ArrayList<InterfaceDefinitionConfigurationObject> supportInterfaces) {
        this.supportedInterfaceProfiles = supportInterfaces;
    }
    
    //
    // Utility Methods
    //
    
    @Override
    protected Logger getLogger() {
    	return(LOG);
    }
}
