/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class Contract extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920111L;
    private static final Logger LOG = LoggerFactory.getLogger(Contract.class);

    public Contract(){
        super();
        setElementType(ElementTypeEnum.CONTRACT);
    }

    @Override
    protected Logger getLogger(){ return LOG; }
}
