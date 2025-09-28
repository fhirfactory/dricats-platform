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

import java.io.Serial;
import java.io.Serializable;

public class ApplicationConfigurationObject implements Serializable{
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678943391L;
    
    //
    // Attributes
    //
    private String vendorName;
    private String applicationName;
    private String applicationVersion;
    
    //
    // Getters and Setters
    //
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}


    //
    // Utility Methods
    //
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApplicationConfigurationObject [vendorName=");
		builder.append(vendorName);
		builder.append(", applicationName=");
		builder.append(applicationName);
		builder.append(", applicationVersion=");
		builder.append(applicationVersion);
		builder.append("]");
		return builder.toString();
	}
}
