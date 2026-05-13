/*
 * Copyright (c) 2026 Mark A. Hunter
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
package net.fhirfactory.dricats.components.hl7.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.components.common.SingleInOutEndpoint;
import net.fhirfactory.dricats.internals.common.identifiers.ElementReference;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class HL7v2xReceiverBase extends SingleInOutEndpoint {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(HL7v2xReceiverBase.class);

    //
    // Attributes
    //




    //
    // Constructor(s)
    //

    public HL7v2xReceiverBase(){
        super();
    }

    public HL7v2xReceiverBase(ElementReference parent, String name, String documentation){
        super(parent, name, documentation);
    }

    //
    // Methods
    //

    @JsonIgnore
    public boolean getValidateMessageStructure(){
        String validateMessageFlag = getExtensionValue(HL7v2xBase.MLLPComponentOptionsEnum.MLLP_COMPONENT_VALIDATE_FLAG.getName());
        if(StringUtils.isEmpty(validateMessageFlag)){
            getExtensions().put(HL7v2xBase.MLLPComponentOptionsEnum.MLLP_COMPONENT_VALIDATE_FLAG.getName(), "false");
            validateMessageFlag = "false";
        }
        return validateMessageFlag.equalsIgnoreCase("true");
    }

    @JsonIgnore
    public void setValidateMessageStructure(boolean validateMessage){
        getExtensions().put(HL7v2xBase.MLLPComponentOptionsEnum.MLLP_COMPONENT_VALIDATE_FLAG.getName(), String.valueOf(validateMessage));
    }

    @JsonIgnore
    public URI getEgressEndpointURI(){
        URI uri = null;
        if(getEgressEndpoint() == null || getEgressEndpoint().getURI() == null){
            try {
                uri = new URI("mllp://");
            } catch( URISyntaxException e ){
                LOG.warn("Failed to create URI for egress endpoint: {}", e.getMessage());
            }
        } else {
            uri = getEgressEndpoint().getURI();
        }
        return(uri);
    }

}
