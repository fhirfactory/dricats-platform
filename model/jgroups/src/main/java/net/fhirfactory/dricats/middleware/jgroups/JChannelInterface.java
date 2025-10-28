/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.middleware.jgroups;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.JGroupsInterface;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.middleware.jgroups.configuration.JChannelConfiguration;
import net.fhirfactory.dricats.middleware.jgroups.valuesets.JChannelStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class JChannelInterface extends JGroupsInterface {

	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 3403310128066770387L;
	private static final Logger LOG = LoggerFactory.getLogger(JChannelInterface.class);

	//
	// Attributes
	//

    private JChannelConfiguration configuration;
    private JChannelStatusEnum endpointStatus;

    //
    // Constructor(s)
    //
    /*
    public JChannelEndpoint(){
    	super();
        this.localChannel = null;
        this.rpcDispatcher = null;
        this.localChannelLock = new Object();
        setEndpointStatus(JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }
    */

    public JChannelInterface(
            DistributableObjectId ownerComponent,
            JChannelConfiguration configurationObject,
            ISubsystem subsystemInterface) {
    	super(ownerComponent, configurationObject.getId(), "JGroups Endpoint", JGroupsInterface.INTERFACE_JGROUPS_RMI, configurationObject );
    	setEndpointStatus(JChannelStatusEnum.JGROUPS_ENDPOINT_STATUS_UNINITIALISED);
    }

    //
    // Bean Methods
    //

    /**
   	 * @return the endpointStatus
   	 */
   	public JChannelStatusEnum getEndpointStatus() {
   		return endpointStatus;
   	}

   	/**
   	 * @param endpointStatus the endpointStatus to set
   	 */
   	public void setEndpointStatus(JChannelStatusEnum endpointStatus) {
   		this.endpointStatus = endpointStatus;
   	}

    public JChannelConfiguration getConfiguration() {
    	return(this.configuration);
    }

	public void setConfiguration(JChannelConfiguration configurationObject) {
    	this.configuration = configurationObject;
    }

    //
    // Utility Methods
    //

    @Override
    protected Logger getLogger() {
    	return(LOG);
    }
}
