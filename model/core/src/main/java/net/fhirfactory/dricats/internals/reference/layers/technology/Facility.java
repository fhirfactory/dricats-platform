/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.internals.reference.layers.technology;

import net.fhirfactory.dricats.internals.reference.common.ElementBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serial;
import java.util.Objects;

public class Facility extends ElementBase {
    @Serial private static final long serialVersionUID = -22345678910114L;
    private static final Logger LOG = LoggerFactory.getLogger(Facility.class);

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
