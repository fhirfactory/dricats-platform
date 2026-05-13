/*
 * Copyright (c) 2024 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.datatypes;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.fhirfactory.dricats.internals.common.object.SerialisableObject;

public class CodeableConcept extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900080L;

    //
    // Attributes
    //

    private List<CodeableConceptCode> coding;
    private String display;
    private String system;

    //
    // Constructor(s)
    //

    public CodeableConcept() {
        setCoding(new ArrayList<CodeableConceptCode>());
    }

    //
    // Bean Methods
    //

    public List<CodeableConceptCode> getCoding() {
        return coding;
    }

    public void setCoding(List<CodeableConceptCode> coding) {
        this.coding = coding;
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
		return Objects.hash(coding, display, system);
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
		return Objects.equals(coding, other.coding) && Objects.equals(display, other.display)
				&& Objects.equals(system, other.system);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CodeableConcept [code=");
		builder.append(coding);
		builder.append(", display=");
		builder.append(display);
		builder.append(", system=");
		builder.append(system);
		builder.append(", id=");
		builder.append(getElementInstanceId());
		builder.append("]");
		return builder.toString();
	}
    
}
