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
package net.fhirfactory.dricats.internals.configuration;

import java.io.Serial;

import org.apache.commons.lang3.StringUtils;

import net.fhirfactory.dricats.internals.configuration.base.ApplicationElementConfigurationBase;

public class ApplicationInstanceConfigurationObject extends ApplicationElementConfigurationBase {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678903391L;
    
    //
    // Attributes
    //
    private String externalisedServiceName;
    private String externalisedServiceDNSName;
    private String externalisedServiceEndpointName;
    private String clusterServiceName;
    private String clusterServiceDNSName;
    private String applicationInstanceId;
    private String applicationDNSName;


    /* an update */

    public ApplicationInstanceConfigurationObject(){
        externalisedServiceEndpointName = null;
        externalisedServiceDNSName = null;
        clusterServiceName = null;
        clusterServiceDNSName = null;
        applicationInstanceId = null;
        applicationDNSName = null;
    }

    public void mergeOverrides(ApplicationInstanceConfigurationObject overrides){

        if (overrides.hasExternalisedServiceName()) {
            setExternalisedServiceName(overrides.getExternalisedServiceName());
        }
        if (overrides.hasExternalisedServiceEndpointName()) {
            setExternalisedServiceEndpointName(overrides.getExternalisedServiceEndpointName());
        }
        if (overrides.hasExternalisedServiceDNSName()) {
            setExternalisedServiceDNSName(overrides.getExternalisedServiceDNSName());
        }
        if (overrides.hasClusterServiceName()) {
            setClusterServiceName(overrides.getClusterServiceName());
        }
        if (overrides.hasClusterServiceDNSName()) {
            setClusterServiceDNSName(overrides.getClusterServiceDNSName());
        }
        if (overrides.hasProcessingPlantName()) {
            setProcessingPlantName(overrides.getProcessingPlantName());
        }
        if (overrides.hasProcessingPlantDNSName()) {
            setProcessingPlantDNSName(overrides.getProcessingPlantDNSName());
        }
    }


    public boolean hasProcessingPlantDNSName(){
        boolean has = !(StringUtils.isEmpty(this.applicationDNSName));
        return(has);
    }

    public boolean hasClusterServiceDNSName(){
        boolean has = !(StringUtils.isEmpty(clusterServiceDNSName));
        return(has);
    }

    public boolean hasProcessingPlantName(){
        boolean has = !(StringUtils.isEmpty(applicationInstanceId));
        return(has);
    }

    public boolean hasClusterServiceName(){
        boolean has = !(StringUtils.isEmpty(clusterServiceName));
        return(has);
    }

    public boolean hasExternalisedServiceDNSName(){
        boolean has = !(StringUtils.isEmpty(externalisedServiceDNSName));
        return(has);
    }

    public boolean hasExternalisedServiceEndpointName(){
        boolean has = !(StringUtils.isEmpty(externalisedServiceEndpointName));
        return(has);
    }

    public boolean hasExternalisedServiceName(){
        boolean has = !(StringUtils.isEmpty(externalisedServiceName));
        return(has);
    }

    public String getExternalisedServiceName() {
        return externalisedServiceName;
    }

    public void setExternalisedServiceName(String externalisedServiceName) {
        this.externalisedServiceName = externalisedServiceName;
    }

    public String getExternalisedServiceDNSName() {
        return externalisedServiceDNSName;
    }

    public void setExternalisedServiceDNSName(String externalisedServiceDNSName) {
        this.externalisedServiceDNSName = externalisedServiceDNSName;
    }

    public String getExternalisedServiceEndpointName() {
        return externalisedServiceEndpointName;
    }

    public void setExternalisedServiceEndpointName(String externalisedServiceEndpointName) {
        this.externalisedServiceEndpointName = externalisedServiceEndpointName;
    }

    public String getClusterServiceName() {
        return clusterServiceName;
    }

    public void setClusterServiceName(String clusterServiceName) {
        this.clusterServiceName = clusterServiceName;
    }

    public String getClusterServiceDNSName() {
        return clusterServiceDNSName;
    }

    public void setClusterServiceDNSName(String clusterServiceDNSName) {
        this.clusterServiceDNSName = clusterServiceDNSName;
    }

    public String getProcessingPlantName() {
        return applicationInstanceId;
    }

    public void setProcessingPlantName(String processingPlantName) {
        this.applicationInstanceId = processingPlantName;
    }

    public String getProcessingPlantDNSName() {
        return applicationDNSName;
    }

    public void setProcessingPlantDNSName(String processingPlantDNSName) {
        this.applicationDNSName = processingPlantDNSName;
    }

    @Override
    public String toString() {
        return "ApplicationInstanceConfigurationObject{" +
                "externalisedServiceName=" + externalisedServiceName +
                ", externalisedServiceDNSName=" + externalisedServiceDNSName +
                ", externalisedServiceEndpointName=" + externalisedServiceEndpointName +
                ", clusterServiceName=" + clusterServiceName +
                ", clusterServiceDNSName=" + clusterServiceDNSName +
                ", applicationInstanceId=" + applicationInstanceId +
                ", applicationDNSName=" + applicationDNSName +
                '}';
    }
}
