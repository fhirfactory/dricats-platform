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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.configuration.segments.ports.internal.JGroupsInterfaceConfigurationObject;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.InterfaceImplementationBase;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

public class JGroupsInterface extends InterfaceImplementationBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900051L;

    //
    // Constants
    //
    public static final String INTERFACE_JGROUPS_MESSAGE = "JGROUPS_MESSAGE";
    public static final String INTERFACE_JGROUPS_RMI = "JGROUPS_RMI";

    //
    // Attributes
    //

    private JGroupsInterfaceConfigurationObject configurationObject;

    //
    // Constructor(s)
    //

    public JGroupsInterface(){
        super();
        configurationObject = new JGroupsInterfaceConfigurationObject();
    }

    public JGroupsInterface(String name, String documentation, String interfaceSpecialisation){
        super(name,documentation,interfaceSpecialisation);
        configurationObject = new JGroupsInterfaceConfigurationObject();
    }

    public JGroupsInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation){
        super(parent, name, documentation, interfaceSpecialisation);
        configurationObject = new JGroupsInterfaceConfigurationObject();
    }

    public JGroupsInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, JGroupsInterfaceConfigurationObject configurationObject){
        super(parent, name, documentation, interfaceSpecialisation);
        this.configurationObject = configurationObject;
    }

    public JGroupsInterface(DistributableObjectId parent, String name, String documentation, String interfaceSpecialisation, URI uri) {
        super(parent, name, documentation, interfaceSpecialisation, uri);
        configurationObject = new JGroupsInterfaceConfigurationObject();
        getLogger().trace("ApplicationInterface(parent, name, documentation, specialization, uri): constructed");
    }

    //
    // Bean Methods
    //

    public JGroupsInterfaceConfigurationObject getConfigurationObject() {
        return configurationObject;
    }

    public void setConfigurationObject(JGroupsInterfaceConfigurationObject configurationObject) {
        this.configurationObject = configurationObject;
    }

    //
    // Utility Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("configurationObject", configurationObject)
                .appendSuper(super.toString())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        getLogger().info("JGroupsInterface.equals(Object): called");
        if (o == null || getClass() != o.getClass()) return false;
        JGroupsInterface that = (JGroupsInterface) o;
        boolean ownerIsEqual = getOwner() != null ? getOwner().equals(that.getOwner()) : that.getOwner() == null;
        getLogger().info("JGroupsInterface.equals(Object): ownerIsEqual = {}", ownerIsEqual);
        boolean nameIsEqual = getName() != null ? getName().equals(that.getName()) : that.getName() == null;
        getLogger().info("JGroupsInterface.equals(Object): nameIsEqual = {}", nameIsEqual);
        boolean documentationIsEqual = getDocumentation() != null ? getDocumentation().equals(that.getDocumentation()) : that.getDocumentation() == null;
        getLogger().info("JGroupsInterface.equals(Object): documentationIsEqual = {}", documentationIsEqual);
        boolean specializationIsEqual = getSpecialization() != null ? getSpecialization().equals(that.getSpecialization()) : that.getSpecialization() == null;
        getLogger().info("JGroupsInterface.equals(Object): specializationIsEqual = {}", specializationIsEqual);
        boolean configurationObjectIsEqual = configurationObject != null ? configurationObject.equals(that.getConfigurationObject()) : that.getConfigurationObject() == null;
        getLogger().info("JGroupsInterface.equals(Object): configurationObjectIsEqual = {}", configurationObjectIsEqual);
        if (!nameIsEqual) return false;
        if (!documentationIsEqual) return false;
        if (!specializationIsEqual) return false;
        if (!ownerIsEqual) return false;
        if (!configurationObjectIsEqual) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), configurationObject);
    }
}
