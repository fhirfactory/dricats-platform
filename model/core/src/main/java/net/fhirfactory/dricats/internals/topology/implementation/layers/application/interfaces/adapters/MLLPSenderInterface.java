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

import net.fhirfactory.dricats.internals.oam.metrics.base.CommonComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.metrics.interfaces.IMetricsExtractionService;
import net.fhirfactory.dricats.internals.topology.implementation.common.valuesets.TopologyExtensionTypeValueSet;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.base.WUPAdapterBase;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serial;

public class MLLPSenderInterface extends WUPAdapterBase implements IMetricsExtractionService {

    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900053L;
    private static final Logger LOG = LoggerFactory.getLogger(MLLPSenderInterface.class);

    //
    // Attributes
    //

    //
    // Constructor(s)
    //

    public MLLPSenderInterface(){
        super();
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.MLLP_SENDER_ADAPTER.name());
        getLogger().trace("MLLPInterface(): constructed");
    }

    public MLLPSenderInterface(String name, String documentation){
        super(name,documentation);
        setExtensionValue(TopologyExtensionTypeValueSet.EXTENSION_ADAPTER_TYPE.getExtensionName(), WUPAdapterTypeEnum.MLLP_SENDER_ADAPTER.name());
        getLogger().trace("MLLPInterface(name, documentation, interfaceSpecialisation): constructed");
    }
    
    //
    // Bean Methods
    //
    

	//
	// Metrics Export
	//
	
	@Override
	public CommonComponentMetricsData retrieveMetricsData() {
		return (getMetricsData());
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

    protected Logger getLogger(){
        return(LOG);
    }
}
