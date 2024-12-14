/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.model.core.configuration;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ConfigurationMapEntry implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 3403310128096663387L;

    //
    // Attributes
    //
    private LocalDateTime registrationChangeInstant;
    private LocalDateTime registrationInstant;
    private String configurationName;
    private Class configurationObjectType;
    private Object configurationObject;

    //
    // Bean Methods
    //

    public LocalDateTime getRegistrationChangeInstant() {
        return registrationChangeInstant;
    }

    public void setRegistrationChangeInstant(LocalDateTime registrationChangeInstant) {
        this.registrationChangeInstant = registrationChangeInstant;
    }

    public LocalDateTime getRegistrationInstant() {
        return registrationInstant;
    }

    public void setRegistrationInstant(LocalDateTime registrationInstant) {
        this.registrationInstant = registrationInstant;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public Class getConfigurationObjectType() {
        return configurationObjectType;
    }

    public void setConfigurationObjectType(Class configurationObjectType) {
        this.configurationObjectType = configurationObjectType;
    }

    public Object getConfigurationObject() {
        return configurationObject;
    }

    public void setConfigurationObject(Object configurationObject) {
        this.configurationObject = configurationObject;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return "ConfigurationMapEntry{" +
                "registrationChangeInstant=" + registrationChangeInstant +
                ", registrationInstant=" + registrationInstant +
                ", configurationName='" + configurationName + '\'' +
                ", configurationObjectType=" + configurationObjectType +
                ", configurationObject=" + configurationObject +
                '}';
    }
}
