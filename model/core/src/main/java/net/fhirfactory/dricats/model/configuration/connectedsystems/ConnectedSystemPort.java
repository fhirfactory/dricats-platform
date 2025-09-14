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
package net.fhirfactory.dricats.model.configuration.connectedsystems;

import net.fhirfactory.dricats.model.configuration.ports.base.InterfaceDefinitionConfigurationObject;
import net.fhirfactory.dricats.model.configuration.ports.datatypes.IPPortInterface;

import java.util.StringJoiner;

public class ConnectedSystemPort {
    private InterfaceDefinitionConfigurationObject targetInterfaceDefinition;
    private Boolean encryptionRequired;
    private IPPortInterface targetPort;
    private String targetPortDNSName;
    private String targetPath;
    
    public ConnectedSystemPort(){
        this.targetInterfaceDefinition = null;
        this.encryptionRequired = false;
        setTargetPort(new IPPortInterface());
        this.targetPortDNSName = null;
        this.targetPath = null;
    }

    public InterfaceDefinitionConfigurationObject getTargetInterfaceDefinition() {
        return targetInterfaceDefinition;
    }

    public void setTargetInterfaceDefinition(InterfaceDefinitionConfigurationObject targetInterfaceDefinition) {
        this.targetInterfaceDefinition = targetInterfaceDefinition;
    }

    public Boolean getEncryptionRequired() {
        return encryptionRequired;
    }

    public void setEncryptionRequired(Boolean encryptionRequired) {
        this.encryptionRequired = encryptionRequired;
    }

    public String getTargetPortDNSName() {
        return targetPortDNSName;
    }

    public void setTargetPortDNSName(String targetPortDNSName) {
        this.targetPortDNSName = targetPortDNSName;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public IPPortInterface getTargetPort() {
        return targetPort;
    }
    public void setTargetPort(IPPortInterface targetPort) {
        this.targetPort = targetPort;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", ConnectedSystemPort.class.getSimpleName() + "[", "]")
                .add("targetInterfaceDefinition=" + getTargetInterfaceDefinition())
                .add("encryptionRequired=" + getEncryptionRequired())
                .add("targetPort=" + getTargetPort())
                .add("targetPortDNSName='" + getTargetPortDNSName() + "'")
                .add("targetPath='" + getTargetPath() + "'")
                .toString();
    }
}
