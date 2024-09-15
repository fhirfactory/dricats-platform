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
package net.fhirfactory.dricats.platform.middleware.rpc;

import net.fhirfactory.dricats.internals.model.networking.NetworkEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsEndpoint;
import net.fhirfactory.dricats.platform.middleware.messaging.MessagingEndpoint;

public abstract class RemoteProcedureCallEndpoint extends JGroupsEndpoint{
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
	
	public RemoteProcedureCallEndpoint(
			String systemName, 
			String clusterName, 
			String stackName,
			NetworkEndpoint endpoint){
		super();
	}
	
	//
	// Methods
	//

	//
	// Utility Methods
	//
	
	@Override
	protected Logger getLogger() {
		return(LOG);
	}
}
