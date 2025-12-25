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
package net.fhirfactory.dricats.internals.common.naming;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.id.ObjectToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Mark A. Hunter
 */
public class RelativeDistinguishedName implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(RelativeDistinguishedName.class);

    //
    // Member Variables
    //

    public static final String NAME_QUALIFIED_SEPARATOR = "=";

    private String qualifier;
    private String value;

    //
    // Constructors
    //

    public RelativeDistinguishedName() {
        qualifier = new String();
        value = new String();
        convertToString();
        toToken();
        convertToConciseString();
    }

    public RelativeDistinguishedName(String qualifier, String nameValue) {
        getLogger().trace(".UnqualifiedName(String, String): Entry, Qualifier --> {}, Value --> {}", qualifier, nameValue);
        if ((qualifier == null) || (nameValue == null)) {
            throw (new IllegalArgumentException("null name or nameValue passed to Constructor"));
        }
        if ((qualifier.isEmpty()) || (nameValue.isEmpty())) {
            throw (new IllegalArgumentException("Empty name or nameValue passed to Constructor"));
        }
        this.qualifier = qualifier;
        this.value = nameValue;
    }

    public RelativeDistinguishedName(RelativeDistinguishedName otherUnqualifiedName) {
        if (otherUnqualifiedName == null) {
            throw (new IllegalArgumentException("null otherUnqualifiedName passed to copy Constructor"));
        }
        this.value = otherUnqualifiedName.getValue();
        this.qualifier = otherUnqualifiedName.getQualifier();
    }

    public RelativeDistinguishedName(ObjectToken token) {
        getLogger().trace(".UnqualifiedName(UnqualifiedNameToken): Entry, token --> {}", token);
        if (token == null) {
            throw (new IllegalArgumentException("null UnqualifiedNameToken passed to Constructor"));
        }
        String tokenContent = token.getToken();
        String[] tokenSplit = tokenContent.split("><");
        String qualifierWorking = tokenSplit[0];
        String qualifier = qualifierWorking.substring(1, qualifierWorking.length() - 1);
        setQualifier(qualifier);
        String valueWorking = tokenSplit[1];
        String value = valueWorking.substring(0, valueWorking.length() - 2);
        setValue(value);
        getLogger().trace(".UnqualifiedName(UnqualifiedNameToken): new UnqualifiedName created, now building different String values!");
    }

    //
    // Accessor Methods
    //
    protected Logger getLogger() {
        return (LOG);
    }

    public String getValue() {
        return (this.value);
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getQualifier() {
        return (this.qualifier);
    }

    public void setQualifier(String newQaulifier) {
        this.qualifier = newQaulifier;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "UnqualifiedName{" +
                "qualifier='" + qualifier + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RelativeDistinguishedName that = (RelativeDistinguishedName) o;
        return Objects.equals(getQualifier(), that.getQualifier()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQualifier(), getValue());
    }

    //
    // Business Methods
    //

    @JsonIgnore
    private String convertToString() {
        String singleString = "[UnqualifiedName=(" + this.getQualifier() + NAME_QUALIFIED_SEPARATOR + this.getValue() + ")]";
        return(singleString);
    }

    @JsonIgnore
    private ObjectToken toToken() {
        ObjectToken token = new ObjectToken(pseudoXMLAttribute(getQualifier(), getValue()));
        return token;
    }

    @JsonIgnore
    public ObjectToken getToken() {
        ObjectToken token = toToken();
        return (token);
    }

    @JsonIgnore
    public String getConciseString() {
        return ( convertToConciseString());
    }

    @JsonIgnore
    private String convertToConciseString() {
        String conciseString = "(" + this.getQualifier() + NAME_QUALIFIED_SEPARATOR  + this.getValue() + ")";
        return( conciseString);
    }

    @JsonIgnore
    public String getUnqualifiedValue() {
        return (getValue());
    }

    @JsonIgnore
    private String pseudoXMLAttribute(String attributeName, String attributeValue) {
        StringBuilder xmlAttributeBuilder = new StringBuilder();
        xmlAttributeBuilder.append("<");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        xmlAttributeBuilder.append(attributeValue);
        xmlAttributeBuilder.append("</");
        xmlAttributeBuilder.append(attributeName);
        xmlAttributeBuilder.append(">");
        return (xmlAttributeBuilder.toString());
    }
}
