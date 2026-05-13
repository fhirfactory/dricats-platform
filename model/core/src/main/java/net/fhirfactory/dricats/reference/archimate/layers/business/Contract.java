/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;

import java.io.Serial;
import java.io.Serializable;

public class Contract extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678920111L;


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

}
