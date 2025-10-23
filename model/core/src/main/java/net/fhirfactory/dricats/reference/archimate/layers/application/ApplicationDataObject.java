/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.application;

import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.internals.topics.Topic;
import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ApplicationDataObject extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910109L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataObject.class);

    //
    // Attributes
    //

    private List<ApplicationFunction> accessingFunctions;
    private List<ApplicationService> accessingServices;
    private MimeTypeEnum dataFormat;
    private Topic dataTopic;

    //
    // Constructor(s)
    //

    public ApplicationDataObject(){
        super();
        setElementType(ElementTypeEnum.APPLICATION_DATA_OBJECT);
    }

    //
    // Bean Methods
    //

    @Override
    protected Logger getLogger(){ return LOG; }

    public List<ApplicationFunction> getAccessingFunctions() {
        return accessingFunctions;
    }

    public void setAccessingFunctions(List<ApplicationFunction> accessingFunctions) {
        this.accessingFunctions = accessingFunctions;
    }

    public List<ApplicationService> getAccessingServices() {
        return accessingServices;
    }

    public void setAccessingServices(List<ApplicationService> accessingServices) {
        this.accessingServices = accessingServices;
    }

    public MimeTypeEnum getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(MimeTypeEnum dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Topic getDataTopic() {
        return dataTopic;
    }

    public void setDataTopic(Topic dataTopic) {
        this.dataTopic = dataTopic;
    }

    //
    // Standard Methods
    //

    @Override
    public String       toString() {
        return new ToStringBuilder(this)
                .append("accessingFunctions", accessingFunctions)
                .append("accessingServices", accessingServices)
                .append("dataFormat", dataFormat)
                .append("dataTopic", dataTopic)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
