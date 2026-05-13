/*
 * Copyright (c) 2025 Mark A. Hunter
 */
package net.fhirfactory.dricats.reference.archimate.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.common.object.ManagedObject;
import net.fhirfactory.dricats.internals.security.datatypes.SecurityLabels;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
public abstract class ElementBase extends ManagedObject implements Serializable {
    //
    // Housekeeping
    //
    @Serial private static final long serialVersionUID = -730450129990001L;
    private static final Logger LOG = LoggerFactory.getLogger(ElementBase.class);

    //
    // Attributes
    //

    public static final String DEFAULT_ELEMENT_SPECIALISATION = "NoSpecialisation";

    private String documentation;
    private String specialization;
    private Map<String, String> extensions;
    private ElementTypeEnum elementType;
    private SecurityLabels securityLabels;

    //
    // Constructor(s)
    //
    /**
     * Default constructor.
     * Initializes extensions and security labels.
     */
    public ElementBase(){
        super();
        this.extensions = new HashMap<>();
        this.securityLabels = new SecurityLabels();
    }

    /**
     * Constructs a new ElementBase with hierarchical context.
     *
     * @param parent The parent object reference, used to build the distinguished name.
     * @param name The short name of the element.
     * @param documentation Description or documentation for the element.
     * @param specialization The specialization or stereotype of the element.
     * @param extensions A map of key/value properties for the element.
     * @param elementType The specific type of ArchiMate element.
     */
    public ElementBase(ElementReference parent, String name, String documentation, String specialization, Map<String, String> extensions, ElementTypeEnum elementType) {
        super();
        setDocumentation(documentation);
        setSpecialization(specialization);
        setExtensions(extensions);
        setElementType(elementType);
        DistinguishedName fdn;
        if(specialization == null || specialization.isEmpty()) {
            specialization = DEFAULT_ELEMENT_SPECIALISATION;
        }
        RelativeDistinguishedName rdn = new RelativeDistinguishedName(specialization, name);
        if(parent != null) {
            fdn = new DistinguishedName(parent.getElementIdentifier().getIdentifierValue());
        } else {
            fdn = new DistinguishedName();
        }
        fdn.appendUnqualifiedName(rdn);
        ElementIdentifier elementIdentifier = new ElementIdentifier(fdn);
        setIdentifier(elementIdentifier);
        setShortName(name);
    }


    /**
     * Constructs a new ElementBase from an object identifier and attributes.
     *
     * @param elementIdentifier The object identifier, providing the long and short names.
     * @param documentation Description or documentation for the element.
     * @param specialization The specialization or stereotype of the element.
     * @param elementType The specific type of ArchiMate element.
     * @param extensions A map of key/value properties for the element.
     */
    public ElementBase(ElementIdentifier elementIdentifier, String documentation, String specialization, ElementTypeEnum elementType, Map<String, String> extensions) {
        super();
        setIdentifier(elementIdentifier);
        setShortName(elementIdentifier.getIdentifierValue().getUnqualifiedName().getUnqualifiedValue());
        this.documentation = documentation;
        this.specialization = specialization;
        this.extensions = new HashMap<>(extensions);
        this.elementType = elementType;
        this.securityLabels = new SecurityLabels();
    }


    /**
     * Copy constructor.
     *
     * @param ori The original ElementBase to copy from.
     */
    public ElementBase(ElementBase ori) {
        super(ori);
        setShortName(ori.getShortName());
        setIdentifier(ori.getIdentifier());
        this.documentation = ori.documentation;
        this.specialization = ori.specialization;
        this.extensions = new HashMap<>();
        this.extensions.putAll(ori.extensions);
        this.elementType = ori.elementType;
        setSecurityLabels(ori.getSecurityLabels());
    }

    //
    // Bean Methods
    //


    public SecurityLabels getSecurityLabels() {
        return securityLabels;
    }

    public void setSecurityLabels(SecurityLabels securityLabels) {
        this.securityLabels = securityLabels;
    }

    public ElementTypeEnum getElementType() {
        return elementType;
    }

    public void setElementType(ElementTypeEnum elementType) {
        this.elementType = elementType;
    }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) { this.documentation = documentation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Map<String, String> getExtensions() { return extensions; }
    public void setExtensions(Map<String, String> extensions) {
        if(this.extensions == null) {
            this.extensions = new HashMap<>();
        }
        this.extensions.clear();
        if(extensions != null && !extensions.isEmpty()) {
            this.extensions.putAll(extensions);
        }
    }

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
        return Objects.equals(getElementInstanceId(), that.getElementInstanceId()) &&
                Objects.equals(getShortName(), that.getShortName()) &&
                Objects.equals(getIdentifier(), that.getIdentifier()) &&
                elementType == that.elementType &&
                Objects.equals(documentation, that.documentation) &&
                Objects.equals(specialization, that.specialization) &&
                Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getElementInstanceId(), elementType, getShortName(), getIdentifier(), documentation, specialization, extensions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("elementType", getElementType())
                .append("securityLabels", getSecurityLabels())
                .append("localObjectId", getElementInstanceId())
                .append("metadata", getMetadata())
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .toString();
    }

    //
     // Helper Methods
    //

    @JsonIgnore
    public ElementReference getReference(){
        ElementReference reference = new ElementReference();
        String specialization = getSpecialization();
        if(specialization == null || specialization.isEmpty()){
            specialization = "unknown";
        }
        reference.setElementSpecialisation(specialization);
        String elementType = getElementType().toString();
        if(elementType == null || elementType.isEmpty()){
            elementType = "unknown";
        }
        reference.setElementType(elementType);
        StringBuilder sb = new StringBuilder();
        sb.append(elementType);
        sb.append("(").append(specialization).append(")");
        sb.append(":").append(getIdentifier().getIdentifierValue().getCommonName().getName());
        reference.setReferenceDescription(sb.toString());
        ElementIdentifier elementIdentifier = SerializationUtils.clone(getIdentifier());
        reference.setElementIdentifier(elementIdentifier);
        reference.setElementInstanceId(getElementInstanceId());
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
            if(o1.getElementInstanceId() == null && o2.getElementInstanceId() == null){
                return(0);
            }
            if(o1.getElementInstanceId() == null && !(o2.getElementInstanceId() == null)){
                return(1);
            }
            if(!(o1.getElementInstanceId() == null) && o2.getElementInstanceId() ==  null){
                return(-1);
            }
            String commonName1 = o1.getIdentifier().getIdentifierValue().getCommonName().getName();
            String commonName2 = o2.getIdentifier().getIdentifierValue().getCommonName().getName();
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
            if(o1.getElementInstanceId() == null && o2.getElementInstanceId() == null){
                return(0);
            }
            if(o1.getElementInstanceId() == null && !(o2.getElementInstanceId() == null)){
                return(1);
            }
            if(!(o1.getElementInstanceId()  == null) && o2.getElementInstanceId() ==  null){
                return(-1);
            }
            int idComparison = o1.getElementInstanceId().getIdValue().compareTo(o2.getElementInstanceId().getIdValue());
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
            if(o1.getIdentifier() == null && o2.getIdentifier() == null){
                return(0);
            }
            if(o1.getIdentifier() == null && !(o2.getIdentifier() == null)){
                return(1);
            }
            if(!(o1.getIdentifier() == null) && o2.getIdentifier() ==  null){
                return(-1);
            }
            String testValue3 = o1.getIdentifier().getIdentifierValue().getTokenString();
            String testValue4 = o2.getIdentifier().getIdentifierValue().getTokenString();
            int comparison2 = testValue3.compareTo(testValue4);
            if(comparison2 != 0){
                return(comparison2);
            }
            if(o1.getShortName() == null && o2.getShortName() == null){
                return(0);
            }
            if(o1.getShortName() == null && !(o2.getShortName() == null)){
                return(1);
            }
            if(!(o1.getShortName() == null) && o2.getShortName() ==  null){
                return(-1);
            }
            String testValue1 = o1.getShortName();
            String testValue2 = o2.getShortName();
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

