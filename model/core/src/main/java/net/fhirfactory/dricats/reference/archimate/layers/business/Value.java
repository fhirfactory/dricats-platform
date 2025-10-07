/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class Value extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920114L;
    private static final Logger LOG = LoggerFactory.getLogger(Value.class);

    public Value(){
        super();
        setElementType(ElementTypeEnum.VALUE);
    }

    @Override
    protected Logger getLogger(){ return LOG; }
}
