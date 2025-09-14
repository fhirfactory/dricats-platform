/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.layers.application;

import net.fhirfactory.dricats.model.topology.reference.common.ElementBase;
import net.fhirfactory.dricats.model.common.DistributableObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationProcess extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678910105L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationProcess.class);

    // ArchiMate attributes for Application Process (by reference IDs)
    // - components: references to participating ApplicationComponent(s)
    // - usedServices: references to ApplicationService(s) used by this process
    // - accessDataObjects: references to ApplicationDataObject(s) accessed by this process

    private List<DistributableObjectId> components;
    private List<DistributableObjectId> usedServices;
    private List<DistributableObjectId> accessDataObjects;

    public ApplicationProcess() {
        super();
        this.components = new ArrayList<>();
        this.usedServices = new ArrayList<>();
        this.accessDataObjects = new ArrayList<>();
        getLogger().trace("ApplicationProcess(): constructed");
    }

    protected Logger getLogger(){ return LOG; }

    public List<DistributableObjectId> getComponents() { return components; }
    public void setComponents(List<DistributableObjectId> components) { this.components = components == null ? new ArrayList<>() : components; }
    public void addComponent(DistributableObjectId componentId) {
        if(componentId == null){ return; }
        if(this.components == null){ this.components = new ArrayList<>(); }
        this.components.add(componentId);
    }

    public List<DistributableObjectId> getUsedServices() { return usedServices; }
    public void setUsedServices(List<DistributableObjectId> usedServices) { this.usedServices = usedServices == null ? new ArrayList<>() : usedServices; }
    public void addUsedService(DistributableObjectId serviceId) {
        if(serviceId == null){ return; }
        if(this.usedServices == null){ this.usedServices = new ArrayList<>(); }
        this.usedServices.add(serviceId);
    }

    public List<DistributableObjectId> getAccessDataObjects() { return accessDataObjects; }
    public void setAccessDataObjects(List<DistributableObjectId> accessDataObjects) { this.accessDataObjects = accessDataObjects == null ? new ArrayList<>() : accessDataObjects; }
    public void addAccessDataObject(DistributableObjectId dataObjectId) {
        if(dataObjectId == null){ return; }
        if(this.accessDataObjects == null){ this.accessDataObjects = new ArrayList<>(); }
        this.accessDataObjects.add(dataObjectId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApplicationProcess that = (ApplicationProcess) o;
        return Objects.equals(components, that.components) &&
                Objects.equals(usedServices, that.usedServices) &&
                Objects.equals(accessDataObjects, that.accessDataObjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), components, usedServices, accessDataObjects);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+getName()+'\''+
                ", componentsCount="+(components==null?0:components.size())+
                ", usedServicesCount="+(usedServices==null?0:usedServices.size())+
                ", accessDataObjectsCount="+(accessDataObjects==null?0:accessDataObjects.size())+
                ", documentation='"+getDocumentation()+'\''+
                ", specialization='"+getSpecialization()+'\''+
                ", properties="+getProperties()+
                ", objectID="+getObjectID()+
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }
}
