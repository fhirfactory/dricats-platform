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
package net.fhirfactory.dricats.internals.model.base.dataytypes;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.fhirfactory.dricats.common.JSONMapperUtility;

/**
 * This test class establishes basic confidence in the functionality of the Object and the 
 * ability to serialise/deserialise it using both Java native and JSON (Jackson) based services.
 */
class CodeableConceptTest {
	private static final Logger LOG = LoggerFactory.getLogger(CodeableConceptTest.class);

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#CodeableConcept()}.
	 */
	@Test
	void testCodeableConcept() {
		getLogger().debug(".testCodeableConcept(): Testing Constructor -> Start");
		CodeableConcept concept = new CodeableConcept();
		if(concept.getCode() == null) {
			fail("Code list is not initialised");
		}
		getLogger().debug(".testCodeableConcept(): Testing Constructor -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getCode()}.
	 */
	@Test
	void testGetCode() {
		getLogger().debug(".testGetCode(): Testing getCode() Method -> Start");
		CodeableConcept concept = new CodeableConcept();
		List<CodeableConceptCode> codeList = concept.getCode();
		if(codeList == null) {
			fail("Code list is null, but shouldn't be!");
		}
		getLogger().debug(".testGetCode(): Testing getCode() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setCode(java.util.List)}.
	 */
	@Test
	void testSetCode() {
		getLogger().debug(".testSetCode(): Testing setCode() Method -> Start");
		CodeableConceptCode testCode = new CodeableConceptCode();
		List<CodeableConceptCode> testCodeList = new ArrayList<>();
		testCodeList.add(testCode);
		CodeableConcept concept = new CodeableConcept();
		concept.setCode(testCodeList);
		if(concept.getCode().size() < 1) {
			fail("Code list is empty, but shouldn't be!");
		}
		getLogger().debug(".testSetCode(): Testing setCode() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getDisplay()}.
	 */
	@Test
	void testGetDisplay() {
		getLogger().debug(".testGetDisplay(): Testing getDisplay() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testDisplayValue = "Test Value";
		testConcept.setDisplay(testDisplayValue);
		if(!StringUtils.equals(testDisplayValue, testConcept.getDisplay())) {
			fail("Test Display Name is not as expected, failure!");
		}
		getLogger().debug(".testGetDisplay(): Testing getDisplay() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setDisplay(java.lang.String)}.
	 */
	@Test
	void testSetDisplay() {
		getLogger().debug(".testSetDisplay(): Testing getDisplay() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testDisplayValue = "Test Value";
		testConcept.setDisplay(testDisplayValue);
		if(!StringUtils.equals(testDisplayValue, testConcept.getDisplay())) {
			fail("Test Display Name is not as expected, failure!");
		}
		getLogger().debug(".testSetDisplay(): Testing getDisplay() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getSystem()}.
	 */
	@Test
	void testGetSystem() {
		getLogger().debug(".testGetSystem(): Testing getSystem() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testSystemValue = "Test Value";
		testConcept.setSystem(testSystemValue);
		if(!StringUtils.equals(testSystemValue, testConcept.getSystem())) {
			fail("Test System Name is not as expected, failure!");
		}
		getLogger().debug(".testGetSystem(): Testing getSystem() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setSystem(java.lang.String)}.
	 */
	@Test
	void testSetSystem() {
		getLogger().debug(".testSetSystem(): Testing setSystem() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testSystemValue = "Test Value";
		testConcept.setSystem(testSystemValue);
		if(!StringUtils.equals(testSystemValue, testConcept.getSystem())) {
			fail("Test System Name is not as expected, failure!");
		}
		getLogger().debug(".testSetSystem(): Testing setSystem() Method -> Finish, Pass");
	}
	
	/*
	 * Test method for Java native serialisation of the object.
	 */
	@Test
	void testJavaSerialisation() {
		getLogger().debug(".testJavaSerialisation(): Testing Java Serialization of Object -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		CodeableConceptCode testCode1 = new CodeableConceptCode();
		testCode1.setCode("Test Code Value1");
		testCode1.setDisplay("Test Code Display Value1");
		testCode1.setSystem("Test Code System Value1");
		testConcept.getCode().add(testCode1);
		CodeableConceptCode testCode2 = new CodeableConceptCode();
		testCode2.setCode("Test Code Value2");
		testCode2.setDisplay("Test Code Display Value2");
		testCode2.setSystem("Test Code System Value2");
		testConcept.getCode().add(testCode2);
		testConcept.setDisplay("Test CodeableConcept Display Value");
		testConcept.setSystem("Test CodeableConcept System Value");
		
		byte[] serializedConcept = SerializationUtils.serialize(testConcept);
		
		CodeableConcept testDeserializedConcept = (CodeableConcept)SerializationUtils.deserialize(serializedConcept);
		if(!testDeserializedConcept.equals(testDeserializedConcept)) {
			fail("Serialization Failure, deserialized object not the same!");
		}
		getLogger().debug(".testJavaSerialisation(): Testing Java Serialization of Object -> Finish, Pass");
	}
	
	/*
	 * Test method for the JSON (Jackson) based serialisation of the object.
	 */
	@Test
	void testJSONSerialisation() {
		getLogger().debug(".testJSONSerialisation(): Testing JSON (Jackson) Serialization of Object -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		CodeableConceptCode testCode1 = new CodeableConceptCode();
		testCode1.setCode("Test Code Value1");
		testCode1.setDisplay("Test Code Display Value1");
		testCode1.setSystem("Test Code System Value1");
		testConcept.getCode().add(testCode1);
		CodeableConceptCode testCode2 = new CodeableConceptCode();
		testCode2.setCode("Test Code Value2");
		testCode2.setDisplay("Test Code Display Value2");
		testCode2.setSystem("Test Code System Value2");
		testConcept.getCode().add(testCode2);
		testConcept.setDisplay("Test CodeableConcept Display Value");
		testConcept.setSystem("Test CodeableConcept System Value");
		
		JSONMapperUtility jsonMapperUitlity = new JSONMapperUtility();
		ObjectMapper jsonMapper = jsonMapperUitlity.getJSONMapper();
		
		String serializedConcept = null;
		try {
			serializedConcept = jsonMapper.writeValueAsString(testConcept);
		} catch (JsonProcessingException e) {
			fail("Serialization Failure, object can not be JSON serialized!");
		}
		if(StringUtils.isEmpty(serializedConcept)) {
			fail("Serialization Failure, object can not be JSON serialized!");
		}
		getLogger().trace(".testJSONSerialisation(): serializedConcept -> {}", serializedConcept);
		CodeableConcept testDeserializedConcept = null;
		try {
			testDeserializedConcept = (CodeableConcept)jsonMapper.readValue(serializedConcept, CodeableConcept.class);
		} catch (JsonProcessingException e) {
			fail("Serialization Failure, object can not be JSON deserialized!");
		}
		if(!testDeserializedConcept.equals(testDeserializedConcept)) {
			fail("Serialization Failure, deserialized object not the same!");
		}
		getLogger().debug(".testJSONSerialisation(): Testing JSON (Jackson) Serialization of Object -> Finish, Pass");
	}
	
	//
	// Utility Methods
	//
	
	protected Logger getLogger() {
		return(LOG);
	}

}
