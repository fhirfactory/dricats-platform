/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class Contract extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678920111L;
    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    //
    // Constants
    //

    //
    // Attributes
    //

    //
    // Constructors
    //
    public Contract(){
        super();
        setElementType(ElementTypeEnum.CONTRACT);
    }

    //
     // Bean Methods
    //

    @Override
    protected Logger getLogger(){ return LOG; }
}
