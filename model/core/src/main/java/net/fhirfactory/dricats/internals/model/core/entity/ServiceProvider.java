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
package net.fhirfactory.dricats.internals.model.core.entity;

import net.fhirfactory.dricats.internals.model.base.DistributableObject;
import net.fhirfactory.dricats.internals.model.core.entity.valuesets.ServiceProviderRoleTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.util.Objects;

public class ServiceProvider extends DistributableObject {
    //
    // Utility Methods
    //

    @Serial
    private static final long serialVersionUID = -12345678900013L;
    private static final Logger LOG = LoggerFactory.getLogger(ServiceProvider.class);

    //
    // Attributes
    //

    private ServiceProviderRoleTypeEnum roleType;

    //
    // Bean Methods
    //

    public ServiceProviderRoleTypeEnum getRoleType() {
        return roleType;
    }

    public void setRoleType(ServiceProviderRoleTypeEnum roleType) {
        this.roleType = roleType;
    }

    //
    // Utility Methods
    //

    @Override
    protected Logger getLogger(){
        return LOG;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceProvider{");
        sb.append("roleType=").append(getRoleType());
        sb.append(", metadata=").append(getMetadata());
        sb.append(", id=").append(getId());
        sb.append(", identifiers=").append(getIdentifiers());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServiceProvider that = (ServiceProvider) o;
        return getRoleType() == that.getRoleType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleType(),super.hashCode());
    }
}
