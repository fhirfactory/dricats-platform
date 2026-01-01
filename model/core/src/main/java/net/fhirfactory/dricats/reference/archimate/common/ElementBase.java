/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.id.ObjectId;
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.common.object.DistributableObject;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.datatypes.EffectiveDate;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Common reference for ArchiMate Elements.
 * Provides common ArchiMate attributes across elements per ArchiMate standard:
 * - name (mandatory)
 * - documentation (aka description)
 * - specialization (stereotype)
 * - properties (key/value)
 */
public abstract class ElementBase extends DistributableObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -730450129990001L;
    private static final Logger LOG = LoggerFactory.getLogger(ElementBase.class);

    //
    // Attributes
    //
    private String name;
    private String documentation;
    private String specialization;
    private Map<String, String> extensions;
    private ElementTypeEnum elementType;

    //
    // Constructor(s)
    //
    public ElementBase(){
        super();
        this.extensions = new HashMap<>();
        getLogger().trace("ElementBase(): constructed");
    }

    public ElementBase(ElementReference parent, String name, String documentation, String specialization, Map<String, String> extensions, ElementTypeEnum elementType) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.specialization = specialization;
        this.extensions = extensions;
        this.elementType = elementType;
        ObjectId id = new ObjectId();
        FullyDistinguishedName fdn = new FullyDistinguishedName(id);
        RelativeDistinguishedName rdn = new RelativeDistinguishedName(specialization, name);
        fdn.appendUnqualifiedName(rdn);
        id.setQualifier(fdn.getCommonQualifier());
        id.setName(fdn.getCommonName());
        id.setEffectivePeriod(new EffectiveDate());
        getLogger().trace("ElementBase(name, documentation, specialization, properties, elementType): constructed");
    }
    public ElementBase( ObjectId objectId, String documentation, String specialization, ElementTypeEnum elementType, Map<String, String> extensions) {
        super();
        this.name = objectId.getName().getValue();
        this.documentation = documentation;
        this.specialization = specialization;
        this.extensions = new HashMap<>(extensions);
        this.elementType = elementType;
        this.setObjectId(objectId);
        getLogger().trace("ElementBase(name, documentation, specialization, properties, elementType): constructed");
    }


    public ElementBase(ElementBase ori) {
        super(ori);
        this.name = ori.name;
        this.documentation = ori.documentation;
        this.specialization = ori.specialization;
        this.extensions = ori.extensions;
        this.elementType = ori.elementType;
        getLogger().trace("ElementBase(ori): constructed");
    }

    public ElementBase(String name, String documentation, String specialization, ElementTypeEnum elementType) {
        super();
        this.name = name;
        this.documentation = documentation;
        this.specialization = specialization;
        this.extensions = new HashMap<>();
        this.elementType = elementType;
        getLogger().trace("ElementBase(name, documentation, specialization, elementType): constructed");
    }

    //
    // Bean Methods
    //

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    public ElementTypeEnum getElementType() {
        return elementType;
    }

    public void setElementType(ElementTypeEnum elementType) {
        this.elementType = elementType;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Map<String, String> getExtensions() { return extensions; }
    public void setExtensions(Map<String, String> extensions) { this.extensions = extensions; }

    public String getExtensionValue(String key) {
        return extensions.get(key);
    }

    public void setExtensionValue(String key, String value) {
        extensions.put(key, value);
    }

    //
    // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementBase that = (ElementBase) o;
        return Objects.equals(getObjectId(), that.getObjectId()) &&
                Objects.equals(name, that.name) &&
                elementType == that.elementType &&
                Objects.equals(documentation, that.documentation) &&
                Objects.equals(specialization, that.specialization) &&
                Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId(), elementType, name, documentation, specialization, extensions);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{"+
                "name='"+name+'\''+
                ", elementType="+elementType+
                ", documentation='"+documentation+'\''+
                ", specialization='"+specialization+'\''+
                ", properties="+ extensions +
                ", id="+ getObjectId()+
                ", identifiers="+getIdentifiers()+
                ", metadata="+getMetadata()+
                ", securityLabels="+getSecurityLabels()+
                '}';
    }

    //
     // Helper Methods
    //

    @JsonIgnore
    public ElementReference getReference(){
        ElementReference reference = new ElementReference();
        reference.setLocalObjectId(getObjectId());
        String specialization = getSpecialization();
        if(specialization == null || specialization.isEmpty()){
            specialization = "unknown";
        }
        reference.setObjectSpecialisation(specialization);
        String elementType = getElementType().toString();
        if(elementType == null || elementType.isEmpty()){
            elementType = "unknown";
        }
        reference.setObjectType(elementType);
        StringBuilder sb = new StringBuilder();
        sb.append(elementType);
        sb.append("(").append(specialization).append(")");
        sb.append(":").append(getCommonName());
        reference.setReferenceDescription(sb.toString());
        return reference;
    }

    //
    // Sorting
    //

    public static Comparator< ElementBase> commonNameComparator = new Comparator< ElementBase>() {
        @Override
        public int compare( ElementBase o1,  ElementBase o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getObjectId() == null && o2.getObjectId() == null){
                return(0);
            }
            if(o1.getObjectId() == null && !(o2.getObjectId() == null)){
                return(1);
            }
            if(!(o1.getObjectId() == null) && o2.getObjectId() ==  null){
                return(-1);
            }
            if(o1.getObjectId().getName() == null && o2.getObjectId().getName() == null){
                return(0);
            }
            if(o1.getObjectId().getName() == null && !(o2.getObjectId().getName() == null)){
                return(1);
            }
            if(!(o1.getObjectId().getName() == null) && o2.getObjectId().getName() ==  null){
                return(-1);
            }
            String commonName1 = o1.getObjectId().getName().getValue();
            String commonName2 = o2.getObjectId().getName().getValue();
            int comparison = commonName1.compareTo(commonName2);
            return(comparison);
        }
    };

    public static Comparator< ElementBase> objectIdComparator = new Comparator< ElementBase>() {
        @Override
        public int compare( ElementBase o1,  ElementBase o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getObjectId() == null && o2.getObjectId() == null){
                return(0);
            }
            if(o1.getObjectId() == null && !(o2.getObjectId() == null)){
                return(1);
            }
            if(!(o1.getObjectId()  == null) && o2.getObjectId() ==  null){
                return(-1);
            }
            int idComparison = o1.getObjectId().compareTo(o2.getObjectId());
            return(idComparison);
        }
    };

    //
    // Identifier Type based Comparator
    //

    public static Comparator< ElementBase> nameComparator = new Comparator< ElementBase>() {
        @Override
        public int compare( ElementBase o1,  ElementBase o2) {
            if (o1 == null && o2 == null) {
                return (0);
            }
            if (o1 == null && o2 != null) {
                return (1);
            }
            if (o1 != null && o2 == null) {
                return (-1);
            }
            if(o1.getName() == null && o2.getName() == null){
                return(0);
            }
            if(o1.getName() == null && !(o2.getName() == null)){
                return(1);
            }
            if(!(o1.getName() == null) && o2.getName() ==  null){
                return(-1);
            }
            String testValue1 = o1.getName();
            String testValue2 = o2.getName();
            int comparison = testValue1.compareTo(testValue2);
            return(comparison);
        }
    };

    public static Comparator< ElementBase> extensionComparator = new Comparator< ElementBase>() {
        @Override
        public int compare( ElementBase o1,  ElementBase o2) {
            if(o1 == null && o2 == null) {
                return (0);
            }
            if(o1 == null && o2 != null) {
                return(1);
            }
            if(o1 != null && o2 == null){
                return(-1);
            }
            if(o1.getExtensions() == null && o2.getExtensions() == null){
                return(0);
            }
            if(o1.getExtensions() == null && !(o2.getExtensions() == null)){
                return(1);
            }
            if(!(o1.getExtensions() == null) && o2.getExtensions() ==  null){
                return(-1);
            }
            if(o1.getExtensions().isEmpty() && o2.getExtensions().isEmpty()){
                return(0);
            }
            if(o1.getExtensions().isEmpty() && !o2.getExtensions().isEmpty()){
                return(1);
            }
            if(!o1.getExtensions().isEmpty() && o2.getExtensions().isEmpty()){
                return(-1);
            }
            if(o1.getExtensions().size() != o2.getExtensions().size()){
                return(o1.getExtensions().size() - o2.getExtensions().size());
            }
            for(String key : o1.getExtensions().keySet()){
                String testValue1 = o1.getExtensions().get(key);
                String testValue2 = o2.getExtensions().get(key);
                int comparison = testValue1.compareTo(testValue2);
                if(comparison != 0){
                    return(comparison);
                }
            }
            return(0);
        }
    };
}

