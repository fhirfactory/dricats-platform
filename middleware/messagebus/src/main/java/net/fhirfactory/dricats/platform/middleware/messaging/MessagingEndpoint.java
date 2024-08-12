/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.platform.middleware.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsEndpoint;
import net.fhirfactory.dricats.platform.networks.datatypes.JGroupsNetworkAddress;

public class MessagingEndpoint extends JGroupsEndpoint{
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(MessagingEndpoint.class);
	
	//
	// Attributes
	//
	
	//
	// Constructor(s)
	// 
	
	public MessagingEndpoint() {
		super();
	}
	
	//
	// Methods
	//

	@Override
	protected String specifySubsystemParticipantName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String specifyJGroupsClusterName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String specifyJGroupsChannelName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String specifyJGroupsStackFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String deriveIntegrationPointSubsystemName(String endpointName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processInterfaceAddition(JGroupsNetworkAddress addedInterface) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processInterfaceRemoval(JGroupsNetworkAddress removedInterface) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processInterfaceSuspect(JGroupsNetworkAddress suspectInterface) {
		// TODO Auto-generated method stub
		
	}

	//
	// Utility Methods
	//
	
	@Override
	protected Logger getLogger() {
		return(LOG);
	}
	
	
}
