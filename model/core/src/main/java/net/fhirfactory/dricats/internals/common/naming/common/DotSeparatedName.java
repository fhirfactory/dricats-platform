/*
 * Copyright (c) 2020 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.internals.common.naming.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class DotSeparatedName implements Serializable {
    //
    // housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Member Variables
    //

    public static String DEFAULT_NAME = "UNNAMED";
    public static String DEFAULT_SEPARATOR = ".";

    private Map<Integer, String> nameMap;
    private String name;

    //
    // Constructor(s)
    //

    public DotSeparatedName() {
        name = DEFAULT_NAME;
        nameMap = new HashMap<>();
        nameMap.put(0, DEFAULT_NAME);
    }

    @JsonCreator
    public DotSeparatedName(String value) {
        this.name = SerializationUtils.clone(value);
        stringToMap();
    }

    public DotSeparatedName(Map<Integer, String> values){
        nameMap = new HashMap<>();
        nameMap.putAll(values);
        mapToString();
    }

    public DotSeparatedName(String[] stringArray){
        nameMap = new HashMap<>();
        for(int i = 0; i < stringArray.length; i++){
            nameMap.put(i, stringArray[i]);
        }
        mapToString();
    }

    public DotSeparatedName(DotSeparatedName ori) {
        this.name = ori.getName();
        this.nameMap = ori.getNameMap();
    }


    //
    // Accessor(s)
    //
    @JsonValue
    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
        stringToMap();
    }

    public Map<Integer, String> getNameMap() {
        return (this.nameMap);
    }
    public void setNameMap(Map<Integer, String> nameMap) {
        if(this.nameMap == null) {
            this.nameMap = new HashMap<Integer, String>();
        }
        this.nameMap.putAll(nameMap);
        mapToString();
    }

    //
     // Business Methods
    //

    protected void stringToMap(){
        if(name.isEmpty()){
            return;
        }
        if(name.equals(DEFAULT_NAME)){
            if(nameMap == null){
                nameMap = new HashMap<>();
            }
            nameMap.clear();
            nameMap.put(0, DEFAULT_NAME);
            return;
        }
        if(nameMap == null){
            nameMap = new HashMap<>();
        }
        nameMap.clear();
        if(name.contains(DEFAULT_SEPARATOR)){
            String[] split = name.split(DEFAULT_SEPARATOR);
            for(int i = 0; i < split.length; i++){
                nameMap.put(i, split[i]);
            }
        } else {
            nameMap.put(0, name);
        }
    }

    protected void mapToString(){
        if(nameMap == null){
            nameMap = new HashMap<>();
            nameMap.put(0, DEFAULT_NAME);
            name = DEFAULT_NAME;
            return;
        }
        if(nameMap.isEmpty()){
            nameMap.clear();
            nameMap.put(0, DEFAULT_NAME);
            name = DEFAULT_NAME;
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < nameMap.size(); i++){
            sb.append(nameMap.get(i));
            if(i < nameMap.size() - 1){
                sb.append(DEFAULT_SEPARATOR);
            }
        }
        name = sb.toString();
    }

    @JsonIgnore
    public boolean isUnnamed() {
        boolean test = StringUtils.isEmpty(this.name) || DEFAULT_NAME.equals(this.name);
        return (test);
    }

    //
    // Standard Methods
    //


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("nameMap", getNameMap())
                .append("name", getName())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DotSeparatedName contextualName = (DotSeparatedName) o;
        return (contextualName.getName().contentEquals(this.getName()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}