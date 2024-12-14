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
package net.fhirfactory.dricats.platform.middleware.jgroups.jchannel;

import net.fhirfactory.dricats.internals.model.messaging.interfaces.LocalMessageServerInterface;
import net.fhirfactory.dricats.internals.model.oam.interfaces.LocalMetricsServerInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelEndpoint;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsNamingServices;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsTransactionResult;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelControllerBase;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelFactory;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupTransactionResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class JChannelMessageController extends JChannelControllerBase {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(JChannelMessageController.class);

	//
	// Attributes
	//
	private LocalMessageServerInterface localMessageServer;
	
	//
	// Constructor(s)
	// 
	
	public JChannelMessageController(
			JChannelEndpoint endpoint,
			JGroupsNamingServices namingServices,
			JChannelFactory channelFactory,
			LocalMetricsServerInterface localMetricsServer,
			LocalMessageServerInterface localMessageServer) {
		super(endpoint, namingServices, channelFactory, localMetricsServer);
		this.localMessageServer = localMessageServer;
	}

	//
	// Configuration Methods
	//

	@Override
	protected String specifyChannelName() {
		return("");
	}

	@Override
	protected String specifyClusterName() {
		return "";
	}

	//
	// Business Methods
	//

	public JGroupsTransactionResult sendMessage(String messageString){
		if(StringUtils.isEmpty(messageString)){
			JGroupsTransactionResult result = new JGroupsTransactionResult();
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage("Message is Empty!");
			result.setTimestamp(LocalDateTime.now());
			return (result);
		}
		Message multicastMessage = new ObjectMessage(null, messageString);
        try {
			getEndpoint().getLocalChannel().send(multicastMessage);
        } catch (Exception e) {
			JGroupsTransactionResult result = new JGroupsTransactionResult();
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage(e.getMessage());
			result.setTimestamp(LocalDateTime.now());
			return (result);
        }
		JGroupsTransactionResult result = new JGroupsTransactionResult();
		result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_OK);
		result.setResultMessage("Message Sent!");
		result.setTimestamp(LocalDateTime.now());
		return (result);
    }

	public JGroupsTransactionResult sendMessage(JGroupsNetworkAddress targetAddress, String messageString) {
		if (targetAddress == null) {
			JGroupsTransactionResult result = new JGroupsTransactionResult();
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage("Target Address is Null!");
			result.setTimestamp(LocalDateTime.now());
			return (result);
		}
		if (StringUtils.equalsIgnoreCase("*", targetAddress.getAddressName())) {
			JGroupsTransactionResult result = sendMessage(messageString);
		}
		Message multicastMessage = new ObjectMessage(targetAddress.getJGroupsAddress(), messageString);
		try {
			getEndpoint().getLocalChannel().send(multicastMessage);
		} catch (Exception e) {
			JGroupsTransactionResult result = new JGroupsTransactionResult();
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage(e.getMessage());
			result.setTimestamp(LocalDateTime.now());
			return (result);
		}
		JGroupsTransactionResult result = new JGroupsTransactionResult();
		result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_OK);
		result.setResultMessage("Message Sent!");
		result.setTimestamp(LocalDateTime.now());
		return (result);
	}

	public void receive(Message msg) {

	}

	//
	// Utility Methods
	//

	@Override
	protected Logger getLogger() {
		return(LOG);
	}
	
	//
	// Getters and Setters
	//

	protected LocalMessageServerInterface getLocalMessageServer() {
		return(localMessageServer);
	}

}
