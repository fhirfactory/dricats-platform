/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.model.topology.reference.layers.technology;

import net.fhirfactory.dricats.model.topology.reference.common.ElementBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.util.Objects;

public class Device extends ElementBase {
    @Serial private static final long serialVersionUID = -22345678910102L;
    private static final Logger LOG = LoggerFactory.getLogger(Device.class);

    protected Logger getLogger(){ return LOG; }

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
