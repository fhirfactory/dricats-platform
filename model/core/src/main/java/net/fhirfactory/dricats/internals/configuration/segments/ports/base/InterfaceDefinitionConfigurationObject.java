/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments.ports.base;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;

public class InterfaceDefinitionConfigurationObject implements Serializable {
    //
     // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
     // Attributes
    //

    String interfaceDefinitionName;
    String interfaceDefinitionVersion;

    //
     // Constructor(s)
    //


    //
     // Bean Methods
    //

    public String getInterfaceDefinitionName() {
        return interfaceDefinitionName;
    }

    public void setInterfaceDefinitionName(String interfaceDefinitionName) {
        this.interfaceDefinitionName = interfaceDefinitionName;
    }

    public String getInterfaceDefinitionVersion() {
        return interfaceDefinitionVersion;
    }

    public void setInterfaceDefinitionVersion(String interfaceDefinitionVersion) {
        this.interfaceDefinitionVersion = interfaceDefinitionVersion;
    }

    //
     // Standard Methods
    //


    @Override
    public String toString() {
        return new StringJoiner(", ", InterfaceDefinitionConfigurationObject.class.getSimpleName() + "[", "]")
                .add("interfaceDefinitionName='" + getInterfaceDefinitionName() + "'")
                .add("interfaceDefinitionVersion='" + getInterfaceDefinitionVersion() + "'")
                .toString();
    }
}
