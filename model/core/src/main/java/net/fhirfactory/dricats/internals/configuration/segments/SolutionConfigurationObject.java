/*
 * Copyright (c) 2025 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.configuration.segments;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/*
 * Solution Configuration Object
 */
public class SolutionConfigurationObject implements Serializable{
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678923391L;
	
	
	//
	// Attributes
	//
    private String solutionName;
    private String solutionGroup;
    private String solutionDescription;
    private boolean encryptedAtRestRequired;
    private boolean encryptedAtTransitRequired;

    
    //
    // Getters and Setters
    //
    
    public String getSolutionName() {
    	return solutionName;
    }
    
    public void setSolutionName(String solutionName) {
    	this.solutionName = solutionName;
    }
    
	public String getSolutionGroup() {
		return solutionGroup;
	}
	
	public void setSolutionGroup(String solutionGroup) {
		this.solutionGroup = solutionGroup;
	}
	
	public String getSolutionDescription() {
		return solutionDescription;
	}
	
	public void setSolutionDescription(String solutionDescription) {
		this.solutionDescription = solutionDescription;
	}

    public boolean isEncryptedAtRestRequired() {
        return encryptedAtRestRequired;
    }

    public void setEncryptedAtRestRequired(boolean encryptedAtRestRequired) {
        this.encryptedAtRestRequired = encryptedAtRestRequired;
    }

    public boolean isEncryptedAtTransitRequired() {
        return encryptedAtTransitRequired;
    }

    public void setEncryptedAtTransitRequired(boolean encryptedAtTransitRequired) {
        this.encryptedAtTransitRequired = encryptedAtTransitRequired;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("solutionName", getSolutionName())
                .append("solutionGroup", getSolutionGroup())
                .append("solutionDescription", getSolutionDescription())
                .append("encryptedAtRestRequired", isEncryptedAtRestRequired())
                .append("encryptedAtTransitRequired", isEncryptedAtTransitRequired())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SolutionConfigurationObject that = (SolutionConfigurationObject) o;
        return encryptedAtRestRequired == that.encryptedAtRestRequired && encryptedAtTransitRequired == that.encryptedAtTransitRequired && Objects.equals(solutionName, that.solutionName) && Objects.equals(solutionGroup, that.solutionGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutionName, solutionGroup, encryptedAtRestRequired, encryptedAtTransitRequired);
    }
}
