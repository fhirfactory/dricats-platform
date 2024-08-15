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

public enum JGroupsEndpointStatusEnum {
	JGROUPS_ENDPOINT_STATUS_UNINITIALISED("jgroups_endpoint.status.uninitialised"),
	JGROUPS_ENDPOINT_STATUS_INITIALISED("jgroups_endpoint.status.initialised"),
	JGROUPS_ENDPOINT_STATUS_SAME("jgroups_endpoint.status.same"),
    JGROUPS_ENDPOINT_STATUS_DETECTED("jgroups_endpoint.status.detected"),
    JGROUPS_ENDPOINT_STATUS_REACHABLE("jgroups_endpoint.status.reachable"),
    JGROUPS_ENDPOINT_STATUS_UNREACHABLE("jgroups_endpoint.status.unreachable"),
    JGROUPS_ENDPOINT_STATUS_STARTED("jgroups_endpoint.status.started"),
    JGROUPS_ENDPOINT_STATUS_OPERATIONAL("jgroups_endpoint.status.operational"),
    JGROUPS_ENDPOINT_STATUS_SUSPECT("jgroups_endpoint.status.suspect"),
    JGROUPS_ENDPOINT_STATUS_FAILED("jgroups_endpoint.status.failed");

    private String endpointStatus;

    private JGroupsEndpointStatusEnum(String status){
        this.endpointStatus = status;
    }

    public String getEndpointStatus() {
        return endpointStatus;
    }
}
