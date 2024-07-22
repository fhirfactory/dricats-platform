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
package net.fhirfactory.dricats.deployment.contants;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mark A. Hunter
 */

public enum DefaultDeploymentConstantsEnum {
    DEPLOYMENT_TIMEZONE("DEPLOYMENT_TIMEZONE", "Australia/Sydney");

    private final String name;
    private final String value;

    DefaultDeploymentConstantsEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static String getPropertyValue(String propertyName) {
        if(StringUtils.isEmpty(propertyName)) {
            return(null);
        }
        for(DefaultDeploymentConstantsEnum constant : DefaultDeploymentConstantsEnum.values()) {
            if(StringUtils.equalsIgnoreCase(propertyName, constant.getName())) {
                return(constant.value);
            }
        }
        return(null);
    }




}
