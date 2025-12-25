/*
 * Copyright (c) 2025 Mark A. Hunter
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
import net.fhirfactory.dricats.internals.common.naming.FullyDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * A simple token wrapper for QualifiedName values.
 *
 * The token content is a single String composed by concatenating each UnqualifiedName
 * as a pseudo-XML element in sequence order, e.g.:
 *   <0:qualifier>value</0:qualifier><1:next>value2</1:next>
 *
 * This class can:
 *  - Build such a String from a QualifiedName (toStringFromQualifiedName)
 *  - Parse that String back into a QualifiedName (toQualifiedName)
 */
public class ObjectToken implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String token;

    //
    // Constructors
    //

    public ObjectToken() {
        this.token = null;
    }

    public ObjectToken(String token) {
        this.token = token;
    }

    public ObjectToken(ObjectId objectId) {
        if(objectId == null){
            throw new IllegalArgumentException("objectId is null");
        }
        if(objectId.getFullyDistinguishedName() == null ){
            throw new IllegalArgumentException("objectId.getQualifiedName() or objectId.getInstanceQualifier() is null");
        }
        this.setToken(objectId.getFullyDistinguishedName().toObjectTokenString());
    }

    //
    // Accessors / Mutators
    //

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    //
    // Helper Methods
    //

    /**
     * Compose the token string from the provided QualifiedName.
     */
    public static String buildToken(FullyDistinguishedName qualifiedName, String instanceQualifier) {
        if (qualifiedName == null) {
            throw new IllegalArgumentException("qualifiedName is null");
        }
        StringBuilder builder = new StringBuilder();
        Map<Integer, DistinguishedNameEntry> entries = qualifiedName.getUnqualifiedNameEntries();
        for (int i = 0; i < entries.size(); i++) {
            RelativeDistinguishedName current = entries.get(i);
            String qualifier = current.getQualifier();
            String value = current.getValue();
            builder.append("<").append(i).append(":").append(qualifier).append(">");
            builder.append(value);
            builder.append("</").append(i).append(":").append(qualifier).append(">");
        }
        builder.append("<").append(entries.size()).append(":").append("instanceQualifier").append(">");
        builder.append(instanceQualifier);
        builder.append("</").append(entries.size()).append(":").append("instanceQualifier").append(">");
        return builder.toString();
    }

    /**
     * Parse the current token content back into a QualifiedName by explicitly mapping
     * the pseudo-XML produced by toStringFromQualifiedName().
     */

    @JsonIgnore
    public FullyDistinguishedName extractQualifiedName() {
        if (token == null || token.isEmpty()) {
            return new FullyDistinguishedName();
        }
        String[] parts = token.split("><");
        FullyDistinguishedName qualifiedName = new FullyDistinguishedName();
        if (parts.length == 0) {
            return qualifiedName;
        }
        // For each expected index, find the matching element that starts with "<i:" or "i:"
        for (int i = 0; i < parts.length-1; i++) {
            String current = null;
            for (int j = 0; j < parts.length-1; j++) {
                String p = parts[j];
                if (p.startsWith("<" + i + ":")) {
                    current = p;
                    break;
                }
                if (current == null && p.startsWith(i + ":")) {
                    current = p;
                    break;
                }
            }
            if (current == null) {
                // malformed token; stop parsing further
                break;
            }
            // Extract qualifier
            String qualifierWorking;
            if (current.startsWith("<" + i + ":")) {
                qualifierWorking = current.replace("<" + i + ":", "");
            } else {
                qualifierWorking = current.replace(i + ":", "");
            }
            int endQualifier = qualifierWorking.indexOf(">");
            if (endQualifier < 0) {
                break; // malformed
            }
            String qualifier = qualifierWorking.substring(0, endQualifier);
            // Extract value region
            String valueRegion;
            if (current.startsWith("<")) {
                valueRegion = current.substring(1, current.length() - 1);
            } else {
                valueRegion = current;
            }
            int start = valueRegion.indexOf(">");
            int end = valueRegion.indexOf("<");
            if (start < 0 || end < 0 || end <= start) {
                break; // malformed
            }
            String value = valueRegion.substring(start + 1, end);
            // Append in order
            DistinguishedNameEntry entry = new DistinguishedNameEntry(qualifier, value);
            qualifiedName.appendUnqualifiedName(entry);
        }
        return qualifiedName;
    }

    @JsonIgnore
    public String extractInstanceQualifier() {
        if (token == null || token.isEmpty()) {
            return "";
        }
        String[] parts = token.split("><");
        FullyDistinguishedName qualifiedName = new FullyDistinguishedName();
        if (parts.length == 0) {
            return "";
        }
        String instanceQualifierTokenInfo = parts[parts.length-1];
        if(instanceQualifierTokenInfo == null || instanceQualifierTokenInfo.isEmpty()){
            return "";
        }
        if(!instanceQualifierTokenInfo.startsWith("<") || !instanceQualifierTokenInfo.endsWith(">")){
            return "";
        }
        if(instanceQualifierTokenInfo.contains(":")){
            return "";
        }
        String instanceQualifier = instanceQualifierTokenInfo.substring(instanceQualifierTokenInfo.indexOf(">")+1, instanceQualifierTokenInfo.indexOf("<"));
        return instanceQualifier;
    }

    @Override
    public String toString() {
        return "QualifiedNameToken{" +
                "token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectToken that = (ObjectToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
