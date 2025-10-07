/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.reference.archimate.common.ElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class BusinessObject extends ElementBase {
    @Serial private static final long serialVersionUID = -12345678920110L;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessObject.class);

    public BusinessObject(){
        super();
        setElementType(ElementTypeEnum.BUSINESS_OBJECT);
    }

    @Override
    protected Logger getLogger(){ return LOG; }
}
