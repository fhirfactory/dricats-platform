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
package net.fhirfactory.dricats.platform.middleware.jgroups.valuesets;

public enum JChannelTypeEnum {
	JGROUPS_ENDPOINT_TYPE_MESSAGING("Messaging", "MessagingChannelConfig"),
	JGROUPS_ENDPOINT_TYPE_TASKING("Tasking", "TaskingChannelConfig"),
    JGROUPS_ENDPOINT_TYPE_METRICS("Metrics", "MetricsChannelConfig");
	
    private final String shortName;
    private final String configObjectName;

    private JChannelTypeEnum(String endpointTypeName, String configObjectName) {
        this.shortName = endpointTypeName;
        this.configObjectName = configObjectName;
    }

    public String getEndpointTypeName() {
        return shortName;
    }

    public String getConfigObjectName() {
        return configObjectName;
    }
}
