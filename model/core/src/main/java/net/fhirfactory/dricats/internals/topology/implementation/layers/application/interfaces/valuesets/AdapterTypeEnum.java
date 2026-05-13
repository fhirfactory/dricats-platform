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
package net.fhirfactory.dricats.internals.topology.implementation.layers.application.interfaces.valuesets;

import net.fhirfactory.dricats.internals.datatypes.CodeableConcept;
import net.fhirfactory.dricats.internals.datatypes.CodeableConceptCode;

import java.net.URI;

public enum AdapterTypeEnum {
    CAMEL_DIRECT_PRODUCER("Camel-Direct-Producer", "Apache Camel - Direct - Producer", "dricats.adapter-type.camel-direct-producer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_DIRECT_CONSUMER("Camel-Direct-Consumer", "Apache Camel - Direct - Consumer", "dricats.adapter-type.camel-direct-consumer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_MLLP_PRODUCER("Camel-MLLP-Producer", "Apache Camel - MLLP - Producer", "dricats.adapter-type.camel-mllp-producer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_MLLP_CONSUMER ("Camel-MLLP-Consumer","Apache Camel - MLLP - Consumer", "dricats.adapter-type.camel-mllp-consumer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_REST_PRODUCER ("Camel-REST-Producer","Apache Camel - REST - Producer", "dricats.adapter-type.camel-rest-producer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_REST_CONSUMER("Camel-REST-Consumer","Apache Camel - REST - Consumer", "dricats.adapter-type.camel-rest-consumer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_HTTP_PRODUCER("Camel-HTTP-Producer","Apache Camel - HTTP - Producer", "dricats.adapter-type.camel-http-producer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    CAMEL_HTTP_CONSUMER("Camel-HTTP-Consumer","Apache Camel - HTTP - Consumer", "dricats.adapter-type.camel-http-consumer", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    JGROUPS_MESSAGING_ADAPTER ("JGroups-Messaging-Adapter","JGroups Adapter - Message", "dricats.adapter-type.camel-jgroups-messaging", "https://fhirfactory.net/CodeSystems/AdapterTypes"),
    INFINISPAN_ENDPOINT ("InfiniSpan-Endpoint", "Infinispan Adapter", "dricats.adapter-type.infinispan-adapter", "https://fhirfactory.net/CodeSystems/AdapterTypes");


    private final String name;
    private final String code;
    private final String display;
    private final URI system;

    private AdapterTypeEnum(String name, String display, String code, String system){
        this.name = name;
        this.code = code;
        this.display = display;
        this.system = URI.create(system);
    }

    public String getName(){
        return(name);
    }

    public String getCode() {
        return code;
    }

    public String getDisplay() {
        return display;
    }

    public URI getSystem() {
        return system;
    }

    public CodeableConcept toDistributableObjectType() {
        CodeableConcept codeableConcept = new CodeableConcept();
        CodeableConceptCode codeableConceptCode = new CodeableConceptCode();
        codeableConceptCode.setCode(getCode());
        codeableConceptCode.setDisplay(getDisplay());
        codeableConceptCode.setSystem(getSystem().toString());
        codeableConcept.getCoding().add(codeableConceptCode);
        return(codeableConcept);
    }

    public static AdapterTypeEnum fromName(String testName){
        for(AdapterTypeEnum b : AdapterTypeEnum.values()){
            if(b.getName().equals(testName)){
                return(b);
            }
        }
        return(null);
    }

    public static AdapterTypeEnum fromCode(String testCode){
        for(AdapterTypeEnum b : AdapterTypeEnum.values()){
            if(b.getCode().equals(testCode)){
                return(b);
            }
        }
        return(null);
    }
}
