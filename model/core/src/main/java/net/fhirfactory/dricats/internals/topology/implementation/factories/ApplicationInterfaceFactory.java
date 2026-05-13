package net.fhirfactory.dricats.internals.topology.implementation.factories;

import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.EgressApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.GridApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.IngresApplicationInterface;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPInterfaceBase;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets.InterfaceComponentTypeEnum;
import net.fhirfactory.dricats.reference.archimate.layers.application.ApplicationComponent;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ApplicationScoped
public class ApplicationInterfaceFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInterfaceFactory.class);

    public IngresApplicationInterface createIngresInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum interfaceType, Map<String, String> configurationParameters){
        if(parent == null || name.isEmpty()){
            throw new IllegalArgumentException("Invalid IngresApplicationInterface creation: parent component cannot be null and name cannot be empty");
        }
        IngresApplicationInterface ingresInterface = new IngresApplicationInterface(parent, name, documentation, interfaceType, configurationParameters);
        return(ingresInterface);
    }

    public EgressApplicationInterface createEgressInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum interfaceType, Map<String, String> configurationParameters){
        if(parent == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid EgressApplicationInterface creation: parent component cannot be null and name cannot be empty");
        }
        EgressApplicationInterface egressInterface = new EgressApplicationInterface(parent, name, documentation, interfaceType, configurationParameters);
        return(egressInterface);
    }

    public GridApplicationInterface createGridInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum interfaceType, Map<String, String> configurationParameters){
        if(parent == null || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid EgressApplicationInterface creation: parent component cannot be null and name cannot be empty");
        }
        GridApplicationInterface gridInterface = new GridApplicationInterface(parent, name, documentation, interfaceType, configurationParameters);
        return(gridInterface);
    }

    public WUPInterfaceBase createInterface(ApplicationComponent parent, String name, String documentation, InterfaceComponentTypeEnum interfaceType, Map<String, String> configurationParameters){
        WUPInterfaceBase createdInterface = createInterface(parent.getReference(), name, documentation, interfaceType, configurationParameters);
        parent.addInterface(createdInterface.getReference());
        return(createdInterface);
    }

    public WUPInterfaceBase createInterface(ElementReference parent, String name, String documentation, InterfaceComponentTypeEnum interfaceType, Map<String, String> configurationParameters){
        WUPInterfaceBase createdInterface;
        switch(interfaceType){
            case GENERIC_SENDER_INTERFACE:
            case HTTP_CLIENT_INTERFACE:
            case MLLP_SENDER_INTERFACE:
            case INTERNAL_CAMEL_SENDER_INTERFACE:
                createdInterface = createEgressInterface(parent, name, documentation, interfaceType, configurationParameters);
                break;
            case GENERIC_RECEIVER_INTERFACE:
            case HTTP_SERVER_INTERFACE:
            case MLLP_RECEIVER_INTERFACE:
            case INTERNAL_CAMEL_RECEIVER_INTERFACE:
                createdInterface = createIngresInterface(parent, name, documentation, interfaceType, configurationParameters);
                break;
            case JGROUPS_RMI_INTERFACE:
            case GENERIC_GRID_INTERFACE:
            case INTERNAL_DATAGRID:
            case JGROUPS_MESSAGING_INTERFACE:
                createdInterface = createGridInterface(parent, name, documentation, interfaceType, configurationParameters);
                break;
            default:
                throw new IllegalArgumentException("Invalid interface type: " + interfaceType);
        }
        return(createdInterface);
    }
}
