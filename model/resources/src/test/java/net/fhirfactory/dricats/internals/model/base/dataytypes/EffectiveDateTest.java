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

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.SerializationUtils;
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
class EffectiveDateTest {
	private static final Logger LOG = LoggerFactory.getLogger(EffectiveDateTest.class);
	
	@Test
	void testGetEffectiveStartDate() {
		getLogger().debug(".testGetEffectiveStartDate(): Testing getEffectiveStartDate() Method -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testDateTime = LocalDateTime.now();
		testEffectiveDate.setEffectiveStartDate(testDateTime);
		if(testEffectiveDate.getEffectiveStartDate() == null) {
			fail("Unable to retrieve Start Date");
		}
		getLogger().debug(".testGetEffectiveStartDate(): Testing getEffectiveStartDate() Method -> Finish, Pass");
	}

	@Test
	void testSetEffectiveStartDate() {
		getLogger().debug(".testSetEffectiveStartDate(): Testing setEffectiveStartDate() Method -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testDateTime = LocalDateTime.now();
		testEffectiveDate.setEffectiveStartDate(testDateTime);
		if(!testDateTime.isEqual(testEffectiveDate.getEffectiveStartDate())) {
			fail("Failed to update Start Date");
		}
		getLogger().debug(".testSetEffectiveStartDate(): Testing setEffectiveStartDate() Method -> Finish, Pass");
	}

	@Test
	void testGetEffectiveEndDate() {
		getLogger().debug(".testGetEffectiveEndDate(): Testing getEffectiveStartDate() Method -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testDateTime = LocalDateTime.now();
		testEffectiveDate.setEffectiveEndDate(testDateTime);
		if(testEffectiveDate.getEffectiveEndDate() == null) {
			fail("Unable to retrieve End Date");
		}
		getLogger().debug(".testGetEffectiveEndDate(): Testing getEffectiveStartDate() Method -> Finish, Pass");
	}

	@Test
	void testSetEffectiveEndDate() {
		getLogger().debug(".testSetEffectiveEndDate(): Testing setEffectiveEndDate() Method -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testDateTime = LocalDateTime.now();
		testEffectiveDate.setEffectiveEndDate(testDateTime);
		if(!testDateTime.isEqual(testEffectiveDate.getEffectiveEndDate())) {
			fail("Failed to update End Date");
		}
		getLogger().debug(".testSetEffectiveEndDate(): Testing setEffectiveEndDate() Method -> Finish, Pass");
	}
	
	@Test
	void testJavaBasedSerialisation() {
		getLogger().debug(".testJavaBasedSerialisation(): Testing Java Based Serialisation -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testStartDate = LocalDateTime.now();
		LocalDateTime testEndDate = LocalDateTime.now(ZoneId.of("Australia/Sydney"));
		testEffectiveDate.setEffectiveEndDate(testEndDate);
		testEffectiveDate.setEffectiveStartDate(testStartDate);
		
		byte[] serialisedObject = SerializationUtils.serialize(testEffectiveDate);
		
		EffectiveDate testDeserialisedEffectiveDate = (EffectiveDate)SerializationUtils.deserialize(serialisedObject);
		
		if(!testEffectiveDate.equals(testDeserialisedEffectiveDate)) {
			fail("Java Based Serialisation Failure");
		}
		getLogger().debug(".testJavaBasedSerialisation(): Testing Java Based Serialisation -> Finish, Pass");
	}
	
	@Test
	void testJSONBasedSerialisation() {
		getLogger().debug(".testJSONBasedSerialisation(): Testing Java Based Serialisation -> Start");
		EffectiveDate testEffectiveDate = new EffectiveDate();
		LocalDateTime testStartDate = LocalDateTime.now();
		LocalDateTime testEndDate = LocalDateTime.now(ZoneId.of("Australia/Sydney"));
		testEffectiveDate.setEffectiveEndDate(testEndDate);
		testEffectiveDate.setEffectiveStartDate(testStartDate);
		
		getLogger().trace(".testJSONBasedSerialisation(): testEffectiveDate ->{}", testEffectiveDate);

		
		JSONMapperUtility jsonMapperUitlity = new JSONMapperUtility();
		ObjectMapper jsonMapper = jsonMapperUitlity.getJSONMapper();
		
		String serialisedObject = null;
		try {
			serialisedObject = jsonMapper.writeValueAsString(testEffectiveDate);
		} catch (JsonProcessingException e) {
			fail("Unable to serialise Object, error -> " + e.toString());
		}
		
		getLogger().trace(".testJSONBasedSerialisation(): serialisedObject -> {}", serialisedObject );
		
		EffectiveDate testDeserialisedEffectiveDate = null;
		try {
			testDeserialisedEffectiveDate = jsonMapper.readValue(serialisedObject, EffectiveDate.class);
		} catch (JsonProcessingException e) {
			fail("Unable to deserialise Object");
		}
		
		getLogger().trace(".testJSONBasedSerialisation(): testDeserialisedEffectiveDate ->{}", testDeserialisedEffectiveDate);
		
		if(!testEffectiveDate.equals(testDeserialisedEffectiveDate)) {
			fail("JSON (Jackson) Based Serialisation Failure");
		}
		getLogger().debug(".testJSONBasedSerialisation(): Testing Java Based Serialisation -> Finish, Pass");
	}
	
	//
	// Utility Methods
	//
	
	protected Logger getLogger() {
		return(LOG);
	}

}
