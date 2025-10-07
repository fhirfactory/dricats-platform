/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.layers.business;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.datatypes.CodeableConcept;
import net.fhirfactory.dricats.reference.archimate.common.SimpleElementBase;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.Objects;

/**
 * ArchiMate Representation element.
 * Element-specific attributes added in addition to SimpleElementBase:
 * - format: representation format (e.g., PDF, HL7 v2, UML)
 * - subject: reference to the thing being represented (e.g., product, data object)
 */
public class Representation extends SimpleElementBase {
    @Serial private static final long serialVersionUID = -12345678920112L;
    private static final Logger LOG = LoggerFactory.getLogger(Representation.class);

    private CodeableConcept format;
    private DistributableObjectId subject;

    public Representation(){
        super();
        setElementType(ElementTypeEnum.REPRESENTATION);
        this.format = new CodeableConcept();
    }

    public Representation(Representation ori){
        super(ori);
        this.format = ori.getFormat();
        this.subject = ori.getSubject();
    }

    public CodeableConcept getFormat() { return format; }
    public void setFormat(CodeableConcept format) { this.format = format; }

    public DistributableObjectId getSubject() { return subject; }
    public void setSubject(DistributableObjectId subject) { this.subject = subject; }

    @Override
    protected Logger getLogger(){ return LOG; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Representation that = (Representation) o;
        return Objects.equals(format, that.format) && Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), format, subject);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("format", format)
                .append("subject", subject)
                .appendSuper(super.toString())
                .toString();
    }
}
