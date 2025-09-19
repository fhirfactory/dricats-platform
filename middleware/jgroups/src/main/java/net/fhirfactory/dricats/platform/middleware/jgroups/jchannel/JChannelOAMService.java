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
package net.fhirfactory.dricats.platform.middleware.jgroups.jchannel;

import net.fhirfactory.dricats.deployment.valuesets.SubsystemInternalServiceNamesEnum;
import net.fhirfactory.dricats.internals.tasking.interfaces.LocalTaskServerInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JChannelOAMService extends JChannelControllerBase {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(JChannelOAMService.class);
	
	//
	// Attributes
	//

	private LocalTaskServerInterface localTaskServer;
	
	//
	// Constructor(s)
	// 
	
	public JChannelOAMService() {
		super();
	}

    //
    // Configuration Methods
    //

    @Override
    protected JChannelInterface resolveEndpoint() {
        getLogger().debug(".resolveEndpoint(): Entry");
        Object configurationObject = getConfigurationServer().getConfigurationObject(SubsystemInternalServiceNamesEnum.OAM_SERVICE_ENDPOINT.getName());
        getLogger().trace(".resolveEndpoint(): Obtained object -> {}", configurationObject);
        if(configurationObject instanceof JChannelInterface) {
            getLogger().trace(".resolveEndpoint(): Object is a JChannelEndpoint :)");
            JChannelInterface endpointObject = (JChannelInterface) configurationObject;
            getLogger().debug(".resolveEndpoint(): Exit, endpoint -> {}", endpointObject);
            return endpointObject;
        }
        getLogger().debug(".resolveEndpoint(): Exit, unable to resolve endpoint, returning null");
        return null;
    }

    @Override
    protected void populateIdentityDetails() {

    }
    //
	// Business Methods
	//

	//
	// Utility Methods
	//

	protected Logger getLogger() {
		return(LOG);
	}

	//
	// Getters and Setters
	//

}
