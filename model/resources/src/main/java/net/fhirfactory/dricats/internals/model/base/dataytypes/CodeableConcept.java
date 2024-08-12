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
package net.fhirfactory.dricats.internals.model.base.dataytypes;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CodeableConcept extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900080L;

    //
    // Attributes
    //

    private List<CodeableConceptCode> code;
    private String display;
    private String system;

    //
    // Constructor(s)
    //

    public CodeableConcept() {
        setCode(new ArrayList<CodeableConceptCode>());
    }

    //
    // Bean Methods
    //

    public List<CodeableConceptCode> getCode() {
        return code;
    }

    public void setCode(List<CodeableConceptCode> code) {
        this.code = code;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }


    
    //
    // Utility Methods
    //
    
    
    
    
	@Override
	public int hashCode() {
		return Objects.hash(code, display, system);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CodeableConcept other = (CodeableConcept) obj;
		return Objects.equals(code, other.code) && Objects.equals(display, other.display)
				&& Objects.equals(system, other.system);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CodeableConcept [code=");
		builder.append(code);
		builder.append(", display=");
		builder.append(display);
		builder.append(", system=");
		builder.append(system);
		builder.append(", objectID=");
		builder.append(getObjectID());
		builder.append("]");
		return builder.toString();
	}
    
}
