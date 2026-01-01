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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.adapters;

import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPAdapterBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;
import java.io.Serializable;

public class HTTPServerAdapter extends WUPAdapterBase implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = -12345678900052L;
    private static final Logger LOG = LoggerFactory.getLogger(HTTPServerAdapter.class);


    //
    // Constructor(s)
    //

    public HTTPServerAdapter() {
        super();
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.HTTP_SERVER_ADAPTER.name());
        getLogger().trace("ApplicationInterface(): constructed");
    }

    public HTTPServerAdapter(String name, String documentation, String interfaceSpecialisation) {
        super(name, documentation);
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.HTTP_SERVER_ADAPTER.name());
        getLogger().trace("ApplicationInterface(name, documentation, interfaceSpecialisation): constructed");
    }


    //
    // Accessors / Mutators
    //


    protected Logger getLogger() {
        return LOG;
    }


    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("parent", getParent())
                .append("metricsData", getMetricsData())
                .append("elementType", getElementType())
                .append("name", getName())
                .append("documentation", getDocumentation())
                .append("specialization", getSpecialization())
                .append("extensions", getExtensions())
                .append("identifiers", getIdentifiers())
                .append("securityLabels", getSecurityLabels())
                .append("metadata", getMetadata())
                .append("id", getObjectId())
                .toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof HTTPServerAdapter)) {
            return false;
        }
        return true;
    }
}
