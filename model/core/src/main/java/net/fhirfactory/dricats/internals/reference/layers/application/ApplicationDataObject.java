/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.internals.reference.layers.application;

import net.fhirfactory.dricats.internals.reference.common.ElementBase;
import net.fhirfactory.dricats.internals.reference.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.util.Objects;

public class ApplicationDataObject extends ElementBase {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -12345678910109L;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationDataObject.class);

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
    protected Logger getLogger(){ return LOG; }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
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
