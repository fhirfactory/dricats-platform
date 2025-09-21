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
package net.fhirfactory.dricats.datagrid.notificationgrid;

import net.fhirfactory.dricats.datagrid.DataGridServicesGroup;
import net.fhirfactory.dricats.deployment.valuesets.SubsystemInternalServiceNamesEnum;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.events.interfaces.ILocalNotificationService;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsTransactionResult;
import net.fhirfactory.dricats.platform.middleware.jgroups.datatypes.JGroupsNetworkAddress;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelControllerBase;
import net.fhirfactory.dricats.platform.middleware.jgroups.valuesets.JGroupTransactionResultEnum;
import org.apache.commons.lang3.StringUtils;
import org.jgroups.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;

@ApplicationScoped
public class NotificationSubscriptionService extends JChannelControllerBase {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(NotificationSubscriptionService.class);

	//
	// Attributes
	//

    @Inject
    private ILocalNotificationService localNotificationService;

    @Inject
    private DataGridServicesGroup dataGridServicesGroup;

	//
	// Constructor(s)
	//

	public NotificationSubscriptionService() {

	}

	//
	// Configuration Methods
	//

    @Override
    protected JChannelInterface resolveEndpoint() {
        getLogger().debug(".resolveEndpoint(): Entry");
        Object configurationObject = getConfigurationServer().getConfigurationObject(SubsystemInternalServiceNamesEnum.NOTIFICATION_SERVICE_ENDPOINT.getName());
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
        DistributableObjectId datagridServiceGroupId = getDataBusServicesGroup().getObjectID();
        QualifiedName myQualifiedName = new QualifiedName(datagridServiceGroupId.getQualifiedName());
        myQualifiedName.appendUnqualifiedName(new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_WORK_UNIT_PROCESSOR.getType(), "NotificationDistributionService"));
        DistributableObjectId myId = new DistributableObjectId(myQualifiedName);
        setObjectID(myId);
        setParent(getDataBusServicesGroup().getObjectID());
        getLogger().debug(".buildObjectId(): Exit");
    }

    //
     // Getters and Setters
    //

    protected ILocalNotificationService getLocalNotificationService(){
        return(localNotificationService);
    }

    protected DataGridServicesGroup getDataBusServicesGroup(){
        return(dataGridServicesGroup);
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
		Message multicastMessage = new Message(null, messageString);
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
        getLogger().info(".sendMessage(): Entry, targetAddress -> {}, messageString -> {}", targetAddress, messageString);
		if (targetAddress == null) {
			JGroupsTransactionResult result = new JGroupsTransactionResult();
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage("Target Address is Null!");
			result.setTimestamp(LocalDateTime.now());
            getLogger().info(".sendMessage(): Exit, result -> {}", result);
			return (result);
		}
		if (StringUtils.equalsIgnoreCase("*", targetAddress.getAddressName())) {
			JGroupsTransactionResult result = sendMessage(messageString);
            getLogger().info(".sendMessage(): Exit, result -> {}", result);
            return (result);
		}
		Message multicastMessage = new Message(targetAddress.getJGroupsAddress(), messageString);
        JGroupsTransactionResult result = new JGroupsTransactionResult();
		try {
            getLogger().info(".sendMessage(): Sending message to -> {}", targetAddress);
			getLocalChannel().send(multicastMessage);
            getLogger().info(".sendMessage(): Message Sent!");
            result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_OK);
            result.setResultMessage("Message Sent!");
            result.setTimestamp(LocalDateTime.now());
		} catch (Exception e) {
			result.setResultCode(JGroupTransactionResultEnum.JGROUPS_TRANSACTION_RESULT_FAILED);
			result.setResultMessage(e.getMessage());
			result.setTimestamp(LocalDateTime.now());
		}
        getLogger().info(".sendMessage(): Exit, result -> {}", result);
        return (result);
	}

	public void receive(Message msg) {

	}

    public void send(Message msg) {}

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


}
