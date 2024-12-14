/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.platform.configuration.factories.communicate;

import net.fhirfactory.pegacorn.core.model.componentid.ComponentTypeTypeEnum;
import net.fhirfactory.pegacorn.core.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPClientAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.HTTPServerAdapter;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.adapters.base.IPCAdapterDefinition;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.edge.petasos.PetasosEndpointTopologyTypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.matrix.InteractMatrixClientEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.interact.matrix.InteractMatrixServerEndpoint;
import net.fhirfactory.pegacorn.core.model.topology.nodes.common.EndpointProviderInterface;
import net.fhirfactory.pegacorn.deployment.names.subsystems.CommunicateIrisSubsystemFunctionalityNames;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.datatypes.ParameterNameValuePairType;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.common.segments.ports.base.InterfaceDefinitionSegment;
import net.fhirfactory.pegacorn.deployment.properties.configurationfilebased.communicate.iris.im.CommunicateIrisIMPropertyFile;
import net.fhirfactory.dricats.platform.configuration.factories.archetypes.common.PetasosEnabledSubsystemTopologyFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

public abstract class CommunicateIrisIMTopologyFactory extends PetasosEnabledSubsystemTopologyFactory {


    public CommunicateIrisIMTopologyFactory(){
        super();
    }

    private CommunicateIrisIMPropertyFile getCommunicateIrisPropertyFile(){
        return((CommunicateIrisIMPropertyFile)getPropertyFile());
    }

    @Inject
    private CommunicateIrisSubsystemFunctionalityNames communicateIrisComponentNames;

    public void addApplicationServicesServerEndpoint( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addMatrixServicesServerEndpoint(): Entry");
        InteractMatrixServerEndpoint matrixApplicationServicesServer = new InteractMatrixServerEndpoint();
        String name = communicateIrisComponentNames.getEndpointServerName(communicateIrisComponentNames.getFunctionNameIrisAppServices());
        TopologyNodeRDN nodeRDN = createNodeRDN(name, getCommunicateIrisPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), ComponentTypeTypeEnum.ENDPOINT);
        matrixApplicationServicesServer.setComponentRDN(nodeRDN);
        matrixApplicationServicesServer.constructFDN(endpointProvider.getComponentFDN(),nodeRDN);
        matrixApplicationServicesServer.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(),nodeRDN);
        matrixApplicationServicesServer.setEndpointType(PetasosEndpointTopologyTypeEnum.INTERACT_HTTP_API_SERVER);
        matrixApplicationServicesServer.setServer(true);
        HTTPServerAdapter httpServer = new HTTPServerAdapter();
        httpServer.setHostName(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getHostDNSEntry());
        httpServer.setServicePortValue(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getServicePortValue());
        httpServer.setServiceDNSName(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getServiceDNSEntry());
        httpServer.setEncrypted(getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().isEncrypted());
        if(!getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getOtherConfigurationParameters().isEmpty()){
            List<ParameterNameValuePairType> otherConfigurationParameters = getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getOtherConfigurationParameters();
            if(httpServer.getAdditionalParameters() == null){
                httpServer.setAdditionalParameters(new HashMap<>());
            }
            for(ParameterNameValuePairType currentNameValuePair: otherConfigurationParameters){
                httpServer.getAdditionalParameters().put(currentNameValuePair.getParameterName(), currentNameValuePair.getParameterValue());
            }
        }
        httpServer.setEnablingTopologyEndpoint(matrixApplicationServicesServer.getComponentID());
        httpServer.setGroupName(name);
        for(InterfaceDefinitionSegment currentInterface: getCommunicateIrisPropertyFile().getInteractMatrixApplicationServicesServer().getSupportedInterfaceProfiles()){
            IPCAdapterDefinition interfaceDef = new IPCAdapterDefinition();
            interfaceDef.setInterfaceFormalVersion(currentInterface.getInterfaceDefinitionVersion());
            interfaceDef.setInterfaceFormalName(currentInterface.getInterfaceDefinitionName());
            httpServer.getSupportedInterfaceDefinitions().add(interfaceDef);
            httpServer.getSupportedDeploymentModes().add(endpointProvider.getResilienceMode());
        }
        matrixApplicationServicesServer.setComponentType(ComponentTypeTypeEnum.ENDPOINT);
        matrixApplicationServicesServer.getAdapterList().add(httpServer);
        matrixApplicationServicesServer.setContainingNodeFDN(endpointProvider.getComponentFDN());
        endpointProvider.addEndpoint(matrixApplicationServicesServer.getComponentFDN());
        getLogger().trace(".addMatrixServicesServerEndpoint(): Add the MatrixApplicationServicesServer Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getComponentFDN(), matrixApplicationServicesServer);
        getLogger().debug(".addMatrixServicesServerEndpoint(): Exit");
    }

    public void addApplicationServicesClientEndpoint( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addApplicationServicesClientEndpoint(): Entry");
        InteractMatrixClientEndpoint matrixClientServicesAPIClient = new InteractMatrixClientEndpoint();
        String clientName = communicateIrisComponentNames.getEndpointClientName(communicateIrisComponentNames.getFunctionNameSynapseMatrixClientServices());
        TopologyNodeRDN nodeRDN = createNodeRDN(clientName, getCommunicateIrisPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), ComponentTypeTypeEnum.ENDPOINT);
        matrixClientServicesAPIClient.setComponentRDN(nodeRDN);
        matrixClientServicesAPIClient.constructFDN(endpointProvider.getComponentFDN(),nodeRDN);
        matrixClientServicesAPIClient.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(),nodeRDN);
        matrixClientServicesAPIClient.setEndpointType(PetasosEndpointTopologyTypeEnum.INTERACT_HTTP_API_CLIENT);
        HTTPClientAdapter httpClient = new HTTPClientAdapter();
        httpClient.setHostName(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getHostDNSEntry());
        httpClient.setPortNumber(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getPortValue());
        httpClient.setEncrypted(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().isEncrypted());
        if(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getConnectedSystem() != null) {
            httpClient.setTargetNameInstant(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getConnectedSystem().getSubsystemName());
        }
        matrixClientServicesAPIClient.getHTTPClientAdapters().add(httpClient);
        endpointProvider.addEndpoint(matrixClientServicesAPIClient.getComponentFDN());
        getLogger().trace(".addApplicationServicesClientEndpoint(): Add the MatrixApplicationServicesClientEndpoint Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getComponentFDN(), matrixClientServicesAPIClient);
        getLogger().debug(".addApplicationServicesClientEndpoint(): Exit");
    }

    public void addSynapseAdminClientEndpoint( EndpointProviderInterface endpointProvider) {
        getLogger().debug(".addSynapseAdminClientEndpoint(): Entry");
        InteractMatrixClientEndpoint matrixClientServicesAPIClient = new InteractMatrixClientEndpoint();
        String clientName = communicateIrisComponentNames.getEndpointClientName(communicateIrisComponentNames.getFunctionNameSynapseAdminServices());
        TopologyNodeRDN nodeRDN = createNodeRDN(clientName, getCommunicateIrisPropertyFile().getSubsystemInstant().getProcessingPlantVersion(), ComponentTypeTypeEnum.ENDPOINT);
        matrixClientServicesAPIClient.setComponentRDN(nodeRDN);
        matrixClientServicesAPIClient.constructFDN(endpointProvider.getComponentFDN(),nodeRDN);
        matrixClientServicesAPIClient.constructFunctionFDN(endpointProvider.getNodeFunctionFDN(),nodeRDN);
        matrixClientServicesAPIClient.setEndpointType(PetasosEndpointTopologyTypeEnum.INTERACT_HTTP_API_CLIENT);
        HTTPClientAdapter httpClient = new HTTPClientAdapter();
        httpClient.setHostName(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getHostDNSEntry());
        httpClient.setPortNumber(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getPortValue());
        httpClient.setEncrypted(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().isEncrypted());
        if(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getConnectedSystem() != null) {
            httpClient.setTargetNameInstant(getCommunicateIrisPropertyFile().getInteractMatrixClientServicesClient().getConnectedSystem().getSubsystemName());
        }
        matrixClientServicesAPIClient.getHTTPClientAdapters().add(httpClient);
        endpointProvider.addEndpoint(matrixClientServicesAPIClient.getComponentFDN());
        getLogger().trace(".addSynapseAdminClientEndpoint(): Add the MatrixApplicationServicesClientEndpoint Port to the Topology Cache");
        getTopologyIM().addTopologyNode(endpointProvider.getComponentFDN(), matrixClientServicesAPIClient);
        getLogger().debug(".addSynapseAdminClientEndpoint(): Exit");
    }
}
