/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this applications and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.internals.common.id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.CommonQualifier;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ObjectId implements Serializable, Comparable<ObjectId>{
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Constants
    //
    public static final String ID_QUALIFIER_NAME_SEPARATOR = "/";

    //
    // Attributes
    //
    private CommonName name;
    private CommonQualifier qualifier;
    private EffectiveDate effectivePeriod;

    //
    // Constructor(s)
    //
    public ObjectId(){
        this.name = new CommonName();
        this.qualifier = new CommonQualifier();
        this.effectivePeriod = new EffectiveDate();
    }

    public ObjectId(CommonName name, CommonQualifier qualifier) {
        this.name = name;
        this.qualifier = qualifier;
        this.effectivePeriod = new EffectiveDate();
    }

    public ObjectId(String qualifier, String name) {
        this.name = new CommonName(name);
        this.qualifier = new CommonQualifier(qualifier);
        this.effectivePeriod = new EffectiveDate();
    }

    public ObjectId(FullyDistinguishedName fdn) {
        this.name = new CommonName(fdn);
        this.effectivePeriod = new EffectiveDate();
    }

    public ObjectId(CommonName name, CommonQualifier qualifier, EffectiveDate effectiveDate) {
        this.name = name;
        this.qualifier = qualifier;
        this.effectivePeriod = effectiveDate;
    }

    public ObjectId(String qualifier, String name, EffectiveDate effectiveDate) {
        this.name = new CommonName(name);
        this.qualifier = new CommonQualifier(qualifier);
        this.effectivePeriod = effectiveDate;
    }

    public ObjectId(FullyDistinguishedName fdn,  EffectiveDate effectiveDate) {
        this.name = new CommonName(fdn);
        this.effectivePeriod = effectiveDate;
    }

    //
    // Accessor(s)
    //
    public CommonName getName() {
        return name;
    }
    public void setName(CommonName name) {
        this.name = name;
    }
    public CommonQualifier getQualifier() {
        return qualifier;
    }
    public void setQualifier(CommonQualifier qualifier) {
        this.qualifier = qualifier;
    }

    @JsonIgnore
    public String getIdValue(){
        return( this.getQualifier().getValue() + ID_QUALIFIER_NAME_SEPARATOR + this.getName().getValue() );
    }

    @JsonIgnore
    public FullyDistinguishedName getFullyDistinguishedName(){
        return new FullyDistinguishedName(this.getQualifier(), this.getName());
    }

    public ObjectId appendRelativeDistinguishedName(RelativeDistinguishedName rdn){
        if(rdn == null){
            return(this);
        }
        if(StringUtils.isEmpty(rdn.getValue()) || StringUtils.isEmpty(rdn.getQualifier())){
            return(this);
        }
        FullyDistinguishedName fdn = new FullyDistinguishedName(getQualifier(), getName());
        fdn.appendUnqualifiedName(rdn);
        CommonName newCommonName = new CommonName(fdn);
        CommonQualifier newCommonQualifier = new CommonQualifier(fdn);
        this.setName(newCommonName);
        this.setQualifier(newCommonQualifier);
        return(this);
    }

    public ObjectId appendFullyDistinguishedName(FullyDistinguishedName fdn){
        if(fdn == null){
            return(this);
        }
        FullyDistinguishedName currentFDN = new FullyDistinguishedName(getQualifier(), getName());
        currentFDN.appendQualifiedName(fdn);
        CommonName newCommonName = new CommonName(fdn);
        CommonQualifier newCommonQualifier = new CommonQualifier(fdn);
        this.setName(newCommonName);
        this.setQualifier(newCommonQualifier);
        return(this);
    }

    public static ObjectId fromIdValueString(String idValue){
        if(idValue.isEmpty()){
            return(null);
        }
        CommonName commonName = CommonName.fromIdValueString(idValue);
        CommonQualifier commonqualifier = CommonQualifier.fromIdValueString(idValue);
        ObjectId id = new ObjectId(commonName, commonqualifier);
        return(id);
    }

    public EffectiveDate getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(EffectiveDate effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ObjectId{");
        sb.append("name=").append(name);
        sb.append(", qualifier=").append(qualifier);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectId objectId = (ObjectId) o;
        return Objects.equals(name, objectId.name) && Objects.equals(qualifier, objectId.qualifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, qualifier);
    }

    //
    // Comparable Interface
    //

    @Override
    public int compareTo(ObjectId o) {
        return this.getIdValue().compareTo(o.getIdValue());
    }
}
