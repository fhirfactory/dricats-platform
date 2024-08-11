/**
 * 
 */
package net.fhirfactory.dricats.internals.model.base.dataytypes;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
class CodeableConceptTest {
	private static final Logger LOG = LoggerFactory.getLogger(CodeableConceptTest.class);

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#CodeableConcept()}.
	 */
	@Test
	void testCodeableConcept() {
		getLogger().info(".testCodeableConcept(): Testing Constructor -> Start");
		CodeableConcept concept = new CodeableConcept();
		if(concept.getCode() == null) {
			fail("Code list is not initialised");
		}
		getLogger().info(".testCodeableConcept(): Testing Constructor -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getCode()}.
	 */
	@Test
	void testGetCode() {
		getLogger().info(".testGetCode(): Testing getCode() Method -> Start");
		CodeableConcept concept = new CodeableConcept();
		List<CodeableConceptCode> codeList = concept.getCode();
		if(codeList == null) {
			fail("Code list is null, but shouldn't be!");
		}
		getLogger().info(".testGetCode(): Testing getCode() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setCode(java.util.List)}.
	 */
	@Test
	void testSetCode() {
		getLogger().info(".testSetCode(): Testing setCode() Method -> Start");
		CodeableConceptCode testCode = new CodeableConceptCode();
		List<CodeableConceptCode> testCodeList = new ArrayList<>();
		testCodeList.add(testCode);
		CodeableConcept concept = new CodeableConcept();
		concept.setCode(testCodeList);
		if(concept.getCode().size() < 1) {
			fail("Code list is empty, but shouldn't be!");
		}
		getLogger().info(".testSetCode(): Testing setCode() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getDisplay()}.
	 */
	@Test
	void testGetDisplay() {
		getLogger().info(".testGetDisplay(): Testing getDisplay() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testDisplayValue = "Test Value";
		testConcept.setDisplay(testDisplayValue);
		if(!StringUtils.equals(testDisplayValue, testConcept.getDisplay())) {
			fail("Test Display Name is not as expected, failure!");
		}
		getLogger().info(".testGetDisplay(): Testing getDisplay() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setDisplay(java.lang.String)}.
	 */
	@Test
	void testSetDisplay() {
		getLogger().info(".testSetDisplay(): Testing getDisplay() Method -> Start");
		CodeableConcept testConcept = new CodeableConcept();
		String testDisplayValue = "Test Value";
		testConcept.setDisplay(testDisplayValue);
		if(!StringUtils.equals(testDisplayValue, testConcept.getDisplay())) {
			fail("Test Display Name is not as expected, failure!");
		}
		getLogger().info(".testSetDisplay(): Testing getDisplay() Method -> Finish, Pass");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#getSystem()}.
	 */
	@Test
	void testGetSystem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.fhirfactory.dricats.internals.model.base.dataytypes.CodeableConcept#setSystem(java.lang.String)}.
	 */
	@Test
	void testSetSystem() {
		fail("Not yet implemented");
	}
	
	//
	// Utility Methods
	//
	
	protected Logger getLogger() {
		return(LOG);
	}

}
