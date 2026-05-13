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

public class Product extends ElementBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678920113L;
    private static final Logger LOG = LoggerFactory.getLogger(Product.class);

    //
    // Attributes
    //

    //
    // Constructor(s)
    //
    public Product(){
        super();
        setElementType(ElementTypeEnum.PRODUCT);
    }
}
