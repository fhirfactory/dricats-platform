/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.ui.uitest.testdata;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.pubsub.content.ContentSubscription;
import net.fhirfactory.dricats.internals.topology.implementation.factories.ApplicationComponentFactory;
import net.fhirfactory.dricats.internals.topology.implementation.factories.ApplicationInterfaceFactory;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.WorkUnitProcessor;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.adapters.camel.*;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPAdapterBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.ui.serverside.caches.topology.UITopologyCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

@ApplicationScoped
public class WorkUnitProcessorTestResourceBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(WorkUnitProcessorTestResourceBuilder.class);

    @Inject
    ApplicationInterfaceFactory interfaceFactory;

    @Inject
    ApplicationComponentFactory componentFactory;

    @Inject
    private UITopologyCacheService uiTopologyCacheService;

    @Inject
    private ContentFilterTestResourceBuilder contentFilterTestResourceBuilder;

    @Inject
    private SubscriptionTestResourceBuilder subscriptionTestResourceBuilder;

    public WorkUnitProcessor createWUP(ApplicationComponent parent, String name, String documentation ){
        WorkUnitProcessor wup = createWUP(parent.getReference(), name, documentation);
        return(wup);
    }

    public WorkUnitProcessor createWUP(ElementReference parent, String name, String documentation){
        WorkUnitProcessor wup = new WorkUnitProcessor(parent, name, documentation);
        return(wup);
    }

    public WUPInterfaceBase createInternalIngressInterface(WorkUnitProcessor parentWUP, String name, String documentation){
        WUPInterfaceBase wupInterface = interfaceFactory.createIngresInterface(parentWUP.getReference(), name, documentation, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE, new HashMap<>());
        parentWUP.addInterface(wupInterface.getReference());
        DirectConsumerAdapter consumerAdapter = new DirectConsumerAdapter(wupInterface.getReference(), name+"-Ingres", documentation);
        wupInterface.getAdapters().add(consumerAdapter.getReference());
        uiTopologyCacheService.addComponent(wupInterface);
        uiTopologyCacheService.addComponent(consumerAdapter);
        return(wupInterface);
    }

    public WUPInterfaceBase createInternalIngressInterface(WorkUnitProcessor parentWUP, String name, String documentation, String contentGroup, String contentSpecifics){
        IngresApplicationInterface wupInterface = interfaceFactory.createIngresInterface(parentWUP.getReference(), name, documentation, InterfaceComponentTypeEnum.INTERNAL_CAMEL_RECEIVER_INTERFACE, new HashMap<>());
        ContentSubscription subscription = subscriptionTestResourceBuilder.createHL7v2ContentSubscription(contentGroup, contentSpecifics);
        wupInterface.getSubscriptions().getContentSubscriptions().add(subscription);
        parentWUP.addInterface(wupInterface.getReference());
        DirectConsumerAdapter consumerAdapter = new DirectConsumerAdapter(wupInterface.getReference(), name+"-Ingres", documentation);
        wupInterface.getAdapters().add(consumerAdapter.getReference());
        uiTopologyCacheService.addComponent(wupInterface);
        uiTopologyCacheService.addComponent(consumerAdapter);
        return(wupInterface);
    }



    public WUPInterfaceBase createInternalEgressInterface(WorkUnitProcessor parentWUP, String name, String documentation){
        WUPInterfaceBase wupInterface = interfaceFactory.createEgressInterface(parentWUP.getReference(), name, documentation, InterfaceComponentTypeEnum.INTERNAL_CAMEL_SENDER_INTERFACE, new HashMap<>());
        parentWUP.addInterface(wupInterface.getReference());
        DirectProducerAdapter producerAdapter = new DirectProducerAdapter(wupInterface.getReference(), name+"-Egress", documentation);
        wupInterface.getAdapters().add(producerAdapter.getReference());
        uiTopologyCacheService.addComponent(wupInterface);
        uiTopologyCacheService.addComponent(producerAdapter);
        return(wupInterface);
    }


    public WUPInterfaceBase createExternalIngressInterface(WorkUnitProcessor parentWUP, String name, String documentation, InterfaceComponentTypeEnum interfaceType, WUPAdapterBase.WUPAdapterTypeEnum adapterType){
        WUPInterfaceBase wupInterface = interfaceFactory.createIngresInterface(parentWUP.getReference(), name, documentation, interfaceType, new HashMap<>());
        parentWUP.addInterface(wupInterface.getReference());
        WUPAdapterBase receiverAdapter = null;
        switch(adapterType){
            case HTTP_SERVER_ADAPTER:{
                receiverAdapter = new HTTPConsumerAdapter(wupInterface.getReference(), name+"-Server", documentation);
                break;
            }
            case MLLP_RECEIVER_ADAPTER:{
                receiverAdapter = new MLLPProducerAdapter(wupInterface.getReference(), name+"-MLLP-Receiver-Adapter", documentation );
                break;
            }
            default: {
                throw new UnsupportedOperationException("WUPAdapterBase.WUPAdapterTypeEnum not presently supported.");
            }
        }
        if(receiverAdapter != null) {
            wupInterface.getAdapters().add(receiverAdapter.getReference());
            uiTopologyCacheService.addComponent(receiverAdapter);
        }
        uiTopologyCacheService.addComponent(wupInterface);
        return(wupInterface);
    }

    public WUPInterfaceBase createExternalEgressInterface(WorkUnitProcessor parentWUP, String name, String documentation, InterfaceComponentTypeEnum interfaceType, WUPAdapterBase.WUPAdapterTypeEnum adapterType){
        WUPInterfaceBase wupInterface = interfaceFactory.createEgressInterface(parentWUP.getReference(), name, documentation, interfaceType, new HashMap<>());
        parentWUP.addInterface(wupInterface.getReference());
        WUPAdapterBase senderAdapter = null;
        switch(adapterType){
            case HTTP_CLIENT_ADAPTER:{
                senderAdapter = new HTTPProducerAdapter(wupInterface.getReference(), name+"-Client", documentation);
                break;
            }
            case MLLP_SENDER_ADAPTER:{
                senderAdapter = new MLLPConsumerAdapter(wupInterface.getReference(), name+"-MLLP-Sender-Adapter", documentation );
                break;
            }
            default: {
                throw new UnsupportedOperationException("WUPAdapterBase.WUPAdapterTypeEnum not presently supported.");
            }
        }
        if(senderAdapter != null) {
            wupInterface.getAdapters().add(senderAdapter.getReference());
            uiTopologyCacheService.addComponent(senderAdapter);
        }
        uiTopologyCacheService.addComponent(wupInterface);
        return(wupInterface);
    }


}
