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
package net.fhirfactory.dricats.datagrid.messagebus;

import net.fhirfactory.dricats.datagrid.DataBusServicesGroup;
import net.fhirfactory.dricats.deployment.valuesets.SubsystemInternalServiceNamesEnum;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.messaging.interfaces.ILocalMessageService;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsTransactionResult;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelControllerBase;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupTransactionResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class MessageDistributionService extends JChannelControllerBase {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(MessageDistributionService.class);

	//
	// Attributes
	//

    @Inject
	private ILocalMessageService localMessageServer;

    @Inject
    private DataBusServicesGroup dataBusServicesGroup;

	
	//
	// Constructor(s)
	// 
	
	public MessageDistributionService(){
        super();
	}

	//
	// Configuration Methods
	//

    @Override
    protected JChannelInterface resolveEndpoint() {
        getLogger().debug(".resolveEndpoint(): Entry");
        Object configurationObject = getConfigurationServer().getConfigurationObject(SubsystemInternalServiceNamesEnum.MESSAGE_SERVICE_ENDPOINT.getName());
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
    protected void populateIdentityDetails(){
        getLogger().debug(".buildObjectId(): Entry");
        DistributableObjectId databusServiceGroupId = getDataBusServicesGroup().getObjectID();
        QualifiedName myQualifiedName = new QualifiedName(databusServiceGroupId.getQualifiedName());
        myQualifiedName.appendUnqualifiedName(new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType(), "MessageDistributionService"));
        DistributableObjectId myId = new DistributableObjectId(myQualifiedName);
        setObjectID(myId);
        setParent(getDataBusServicesGroup().getObjectID());
        getLogger().debug(".buildObjectId(): Exit");
    }

    //
     // Getters and Setters
    //

    protected DataBusServicesGroup getDataBusServicesGroup(){
        return(dataBusServicesGroup);
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
            getLocalChannel().send(multicastMessage);
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
            getLocalChannel().send(multicastMessage);
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

	protected ILocalMessageService getLocalMessageServer() {
		return(localMessageServer);
	}

}
