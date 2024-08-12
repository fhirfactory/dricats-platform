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
package net.fhirfactory.dricats.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JSONMapperUtility {
	//
	// Housekeeping
	//
	private static final Logger LOG = LoggerFactory.getLogger(JSONMapperUtility.class);
	
	//
	// Attributes
	//
    ObjectMapper jsonMapper = null;
    
    //
    // Constructor(s)
    //
    
    public JSONMapperUtility() {
	
	    this.jsonMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	    JavaTimeModule module = new JavaTimeModule();
	    this.jsonMapper.registerModule(module);
	    this.jsonMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	    this.jsonMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
	    this.jsonMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
    }
    
    //
    // Bean Methods
    //
    
    public ObjectMapper getJSONMapper() {
    	return(jsonMapper);
    }
    
    //
    // Utility Methods
    //
    
    protected Logger getLogger() {
    	return(LOG);
    }
    
}
