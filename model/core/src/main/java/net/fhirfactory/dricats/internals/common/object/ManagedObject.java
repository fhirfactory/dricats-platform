/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.common.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.CommonQualifier;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.object.datatypes.ObjectMetadata;
import net.fhirfactory.dricats.internals.security.datatypes.SecurityLabels;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a base managed object within the DRICATS platform.
 * <p>
 * A ManagedObject provides fundamental capabilities for object identification, metadata tracking,
 * and security labeling. It extends {@link SerialisableObject} to include structured identifiers
 * (both primary and auxiliary), short names, and reference generation.
 * </p>
 * <p>
 * Key features include:
 * <ul>
 *     <li>Unique identification via {@link ElementIdentifier} and instance keys.</li>
 *     <li>Metadata management through {@link ObjectMetadata}.</li>
 *     <li>Security classification via {@link SecurityLabels}.</li>
 *     <li>Support for object references and long name resolution.</li>
 * </ul>
 * </p>
 *
 * @author Mark A. Hunter
 * @since 1.0.0
 */
public class ManagedObject extends SerialisableObject implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900002L;

    //
    // Attributes
    //

    private static final String MANAGED_OBJECT_IDENTIFIER_TYPE_CODE = "ManagedObjectIdentifier";
    private static final String MANAGED_OBJECT_IDENTIFIER_TYPE_DISPLAY = "Managed Object Identifier";
    private static final URI MANAGED_OBJECT_IDENTIFIER_TYPE_SYSTEM = URI.create("http://fhirfactory.net/dricats/valuesets/identifier_types");
    private static final String MANAGED_OBJECT_IDENTIFIER_TYPE_VALUE = "ManagedObjectIdentifier";

    private String shortName;
    private ElementIdentifier identifier;
    private Map<URI, ElementIdentifier> otherIdentifiers;
    private ObjectMetadata metadata;
    private SecurityLabels securityLabels;

    //
    // Constructor(s)
    //

    /**
     * Default constructor for ManagedObject.
     * Initializes a new ManagedObject with a default element instance identifier and basic properties.
     */
    public ManagedObject(){
    	super();
        this.identifier = new ElementIdentifier();
        setShortName(getElementInstanceId().getIdValue());
        CommonQualifier commonQualifier = new CommonQualifier("NO_SCOPE");
        CommonName commonName = new CommonName(getElementInstanceId().getIdValue());
        identifier.setIdentifierValue(new DistinguishedName(commonQualifier, commonName));
        setIdentifier(identifier);
        setOtherIdentifiers(new HashMap<URI, ElementIdentifier>());
        setMetadata(new ObjectMetadata());
        setSecurityLabels(new SecurityLabels());
    }

    /**
     * Constructor for ManagedObject with a specified qualified name.
     *
     * @param qualifiedName The DistinguishedName to be used for the object's identifier and short name.
     */
    public ManagedObject(DistinguishedName qualifiedName) {
        this();
        this.shortName = qualifiedName.getUnqualifiedName().getValue();
        ElementIdentifier identifier = new ElementIdentifier();
        identifier.setIdentifierValue(qualifiedName);
        setIdentifier(identifier);
    }

    /**
     * Copy constructor for ManagedObject.
     * Creates a new ManagedObject by copying the properties of an existing one.
     *
     * @param ori The original ManagedObject to copy from.
     */
    public ManagedObject(ManagedObject ori) {
    	super(ori.getElementInstanceId());
        setShortName(ori.getShortName());
        setIdentifier(ori.getIdentifier());
        this.otherIdentifiers = new HashMap<>();
        this.otherIdentifiers.putAll(ori.getOtherIdentifiers());
        setMetadata(new ObjectMetadata(ori.getMetadata()));
        setSecurityLabels(ori.getSecurityLabels());
    }

    //
    // Getters and Setters
    //

    /**
     * Gets the security labels associated with this object.
     *
     * @return The SecurityLabels for the object.
     */
    public SecurityLabels getSecurityLabels() {
        return securityLabels;
    }

    /**
     * Sets the security labels for this object.
     *
     * @param securityLabels The SecurityLabels to set.
     */
    public void setSecurityLabels(SecurityLabels securityLabels) {
        this.securityLabels = securityLabels;
    }

    /**
     * Gets the metadata for this object.
     *
     * @return The ObjectMetadata for the object.
     */
    public ObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the metadata for this object.
     *
     * @param metadata The ObjectMetadata to set.
     */
    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets the short name of this object.
     *
     * @return The short name string.
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of this object.
     *
     * @param shortName The short name string to set.
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the primary identifier of this object.
     *
     * @return The ElementIdentifier for the object.
     */
    public ElementIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Sets the primary identifier for this object.
     *
     * @param identifier The ElementIdentifier to set.
     */
    public void setIdentifier(ElementIdentifier identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets other identifiers associated with this object.
     *
     * @return A map of URI to ElementIdentifier for other identifiers.
     */
    public Map<URI, ElementIdentifier> getOtherIdentifiers() {
        return otherIdentifiers;
    }

    /**
     * Sets other identifiers associated with this object.
     *
     * @param otherIdentifiers A map of URI to ElementIdentifier to set.
     */
    public void setOtherIdentifiers(Map<URI, ElementIdentifier> otherIdentifiers) {
        this.otherIdentifiers = otherIdentifiers;
    }

    /**
     * Resolves the element instance key.
     * If an existing key cannot be resolved from the element instance identifier, a new one is generated.
     *
     * @return The resolved or generated element instance key.
     */
    public String resolveElementInstanceKey() {
        String key = null;
        try {
            key = getElementInstanceId().resolveKey();
        } catch (Exception e) {
            // ignore
        }
        if(key == null || key.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            getElementInstanceId().setUpperBits(uuid.getMostSignificantBits());
            getElementInstanceId().setLowerBits(uuid.getLeastSignificantBits());
            getElementInstanceId().setVersion(0L);
            key = getElementInstanceId().resolveKey();
        }
        return key;
    }

    //
    // Key Method
    //
    
    /**
     * Resolves the element key.
     * Attempts to retrieve the name from the identifier, falling back to the short name if unavailable.
     *
     * @return The resolved element key.
     */
    public String resolveElementKey() {
        String key = null;
        try {
            key = getIdentifier().getIdentifierValue().getCommonName().getName();
        } catch (Exception e) {
            // ignore
        }
        if(key == null || key.isEmpty()) {
            key = getShortName();
        }
        return key;
    }

    //
    // Long Name Method
    //

    /**
     * Gets the long name of the object.
     *
     * @return The resolved element long name.
     */
    @JsonIgnore
    public String getLongName(){
        return(resolveElementLongName());
    }

    /**
     * Resolves the long name of the element.
     * Retrieves the name from the object's primary identifier.
     *
     * @return The resolved long name string.
     */
    public String resolveElementLongName(){
        return(getIdentifier().getIdentifierValue().getCommonName().getName());
    }

    /**
     * Generates an ElementReference for this object.
     * The reference includes instance ID, identifier, and description.
     *
     * @return An ElementReference representing this ManagedObject.
     */
    public ElementReference getReference(){
        ElementReference reference = new ElementReference();
        reference.setElementInstanceId(getElementInstanceId());
        reference.setElementIdentifier(getIdentifier());
        if(getIdentifier() != null && getIdentifier().getIdentifierValue() != null && getIdentifier().getIdentifierValue().getCommonName() != null){
            reference.setReferenceDescription(getIdentifier().getIdentifierValue().getCommonName().getName());
        } else {
            reference.setReferenceDescription(getShortName());
        }
        reference.setElementType("ManagedObject");
        return reference;
    }

    //
    // Standard Methods
    //

    /**
     * Compares this ManagedObject with another object for equality.
     * Objects are considered equal if they have the same short name, instance ID, and other identifiers.
     *
     * @param o The object to compare with.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ManagedObject that = (ManagedObject) o;
        return Objects.equals(getShortName(), that.getShortName()) && Objects.equals(getElementInstanceId(), that.getElementInstanceId()) && Objects.equals(getOtherIdentifiers(), that.getOtherIdentifiers());
    }

    /**
     * Generates a hash code for this ManagedObject.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getShortName(), getElementInstanceId(), getOtherIdentifiers());
    }

    /**
     * Returns a string representation of this ManagedObject.
     *
     * @return A string containing key properties of the object.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("shortName", getShortName())
                .append("longName", getIdentifier())
                .append("otherIdentifiers", getOtherIdentifiers())
                .append("metadata", getMetadata())
                .toString();
    }
}
