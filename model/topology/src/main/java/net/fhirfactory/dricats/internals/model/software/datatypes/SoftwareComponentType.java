/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.internals.model.software.datatypes;

import net.fhirfactory.dricats.internals.model.software.valuesets.SoftwareComponentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class SoftwareComponentType implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900094L;
    private static final Logger LOG = LoggerFactory.getLogger(SoftwareComponentType.class);

    //
    // Attributes
    //

    private SoftwareComponentTypeEnum componentType;
    private String componentSpecialisation;
    private String componentTypeVersion;

    //
    // Constructor(s)
    //

    //
    // Bean Methods
    //

    public SoftwareComponentTypeEnum getComponentType() {
        return componentType;
    }

    public void setComponentType(SoftwareComponentTypeEnum componentType) {
        this.componentType = componentType;
    }

    public String getComponentTypeVersion() {
        return componentTypeVersion;
    }

    public void setComponentTypeVersion(String componentTypeVersion) {
        this.componentTypeVersion = componentTypeVersion;
    }

    public String getComponentSpecialisation() {
        return componentSpecialisation;
    }

    public void setComponentSpecialisation(String componentSpecialisation) {
        this.componentSpecialisation = componentSpecialisation;
    }

    //
    // Utility Methods
    //

    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoftwareComponentType that = (SoftwareComponentType) o;
        return Objects.equals(getComponentSpecialisation(), that.getComponentSpecialisation()) && Objects.equals(getComponentType(), that.getComponentType()) && Objects.equals(getComponentTypeVersion(), that.getComponentTypeVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentSpecialisation(), getComponentType(), getComponentTypeVersion());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SoftwareComponentType{");
        sb.append("componentType=").append(getComponentType());
        sb.append(", componentTypeVersion='").append(getComponentTypeVersion()).append('\'');
        sb.append(", componentSpecialisation='").append(getComponentSpecialisation()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
