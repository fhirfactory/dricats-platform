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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.dricats.internals.configuration.datatypes.ParameterNameValuePairType;

public class InterfaceConfigurationObject implements Serializable {
	//
	// Housekeeping
	//
	@Serial
	private static final long serialVersionUID = 7470709568612424300L;
	private static final Logger LOG = LoggerFactory.getLogger(InterfaceConfigurationObject.class);
	
	//
	// Attributes
	//
    private List<ParameterNameValuePairType> configurationParameters;
    private String id;
    
    //
    // Logger Access Method
    //
     protected Logger getLogger() {
    	 return(LOG);
     }

    //
    // Constructor(s)
    //

    public InterfaceConfigurationObject(){
        this.configurationParameters = new ArrayList<>();
        this.id = null;
    }

    public InterfaceConfigurationObject(String id){
         this.configurationParameters = new ArrayList<>();
         this.id = id;
    }

    //
    // Getters and Setters (Bean Methods)
    //

    public List<ParameterNameValuePairType> getConfigurationParameters() {
        return configurationParameters;
    }

    public void setConfigurationParameters(List<ParameterNameValuePairType> otherConfigurationParameters) {
        this.configurationParameters = otherConfigurationParameters;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //
    // To String
    //

    @Override
    public String toString() {
        return "InterfaceConfigurationObject{" +
                "configurationParameters=" + configurationParameters +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InterfaceConfigurationObject that = (InterfaceConfigurationObject) o;
        return Objects.equals(getConfigurationParameters(), that.getConfigurationParameters()) && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getConfigurationParameters(), getId());
    }
}
