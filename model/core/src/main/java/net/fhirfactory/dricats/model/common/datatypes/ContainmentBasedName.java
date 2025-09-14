/*
 * Copyright (c) 2025 Mark A. Hunter (ACT Health)
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
package net.fhirfactory.dricats.model.common.datatypes;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContainmentBasedName implements Serializable {
    //
    // Housekeeping
    //
    private static final long serialVersionUID = 1L;
    
    //
    // Attributes
    //
    private List<Map.Entry<String, String>> nameSegments;

    //
    // Constructor(s)
    //
    public ContainmentBasedName() {
        nameSegments = new ArrayList<>();
    }

    /**
     * Creates a ContainmentBasedName from a string representation.
     * The string should be in the format "key1=value1.key2=value2.key3=value3"
     *
     * @param nameString The string representation of the name
     * @throws IllegalArgumentException if the string format is invalid
     */
    public ContainmentBasedName(String nameString) {
        this();
        if (nameString == null || nameString.trim().isEmpty()) {
            throw new IllegalArgumentException("Name string cannot be null or empty");
        }

        String[] segments = nameString.split("\\.");
        for (String segment : segments) {
            String[] keyValue = segment.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid segment format: " + segment);
            }
            addSegment(keyValue[0], keyValue[1]);
        }
    }


    //
    // Bean Methods
    //

    public List<Map.Entry<String, String>> getNameSegments() {
        return nameSegments;
    }

    public void setNameSegments(List<Map.Entry<String, String>> nameSegments) {
        this.nameSegments = nameSegments;
    }

    //
    // Business Functions
    //

    public void addSegment(String key, String value) {
        if (key != null && value != null) {
            nameSegments.add(new AbstractMap.SimpleEntry<>(key, value));
        }
    }

    public void insertSegment(int index, String key, String value) {
        if (key != null && value != null && index >= 0 && index <= nameSegments.size()) {
            nameSegments.add(index, new AbstractMap.SimpleEntry<>(key, value));
        }
    }

    public Map.Entry<String, String> removeSegment(int index) {
        if (index >= 0 && index < nameSegments.size()) {
            return nameSegments.remove(index);
        }
        return null;
    }

    public boolean removeSegment(String key, String value) {
        if (key != null && value != null) {
            return nameSegments.remove(new AbstractMap.SimpleEntry<>(key, value));
        }
        return false;
    }

    public Map.Entry<String, String> getSegment(int index) {
        if (index >= 0 && index < nameSegments.size()) {
            return nameSegments.get(index);
        }
        return null;
    }

    public boolean modifySegmentValue(int index, String newValue) {
        if (index >= 0 && index < nameSegments.size() && newValue != null) {
            String key = nameSegments.get(index).getKey();
            nameSegments.set(index, new AbstractMap.SimpleEntry<>(key, newValue));
            return true;
        }
        return false;
    }

    public boolean modifySegmentKey(int index, String newKey) {
        if (index >= 0 && index < nameSegments.size() && newKey != null) {
            String value = nameSegments.get(index).getValue();
            nameSegments.set(index, new AbstractMap.SimpleEntry<>(newKey, value));
            return true;
        }
        return false;
    }

    public boolean modifySegment(String oldKey, String oldValue, String newKey, String newValue) {
        if (oldKey != null && oldValue != null && newKey != null && newValue != null) {
            int index = nameSegments.indexOf(new AbstractMap.SimpleEntry<>(oldKey, oldValue));
            if (index != -1) {
                nameSegments.set(index, new AbstractMap.SimpleEntry<>(newKey, newValue));
                return true;
            }
        }
        return false;
    }

    public List<Map.Entry<String, String>> getSegments() {
        return new ArrayList<>(nameSegments);
    }

    public void clear() {
        nameSegments.clear();
    }

    public int size() {
        return nameSegments.size();
    }
    
    public String getFullName() {
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : nameSegments) {
            values.add(entry.getKey() + "=" + entry.getValue());
        }
        return String.join(".", values);
    }

    public void modifySegmentByKey(String key, String newValue) {
        for (int i = 0; i < nameSegments.size(); i++) {
            if (nameSegments.get(i).getKey().equals(key)) {
                nameSegments.set(i, new AbstractMap.SimpleEntry<>(key, newValue));
                return;
            }
        }
        throw new IllegalArgumentException("Key not found: " + key);
    }

    /**
     * Adds all segments from another ContainmentBasedName at the beginning of this name
     *
     * @param prefixName The ContainmentBasedName to be added as a prefix
     * @throws IllegalArgumentException if prefixName is null
     */
    public void addPrefixName(ContainmentBasedName prefixName) {
        if (prefixName == null) {
            throw new IllegalArgumentException("Prefix name cannot be null");
        }
        List<Map.Entry<String, String>> prefixSegments = new ArrayList<>(prefixName.getNameSegments());
        prefixSegments.addAll(this.nameSegments);
        this.nameSegments = prefixSegments;
    }

    public void addPrefixName(String prefixName) {
        if (prefixName == null) {
            throw new IllegalArgumentException("Prefix name cannot be null");
        }
        ContainmentBasedName prefix = new ContainmentBasedName(prefixName);
        this.addPrefixName(prefix);
    }

    public String toDotName(){
        StringBuilder sb = new StringBuilder();
        int size = nameSegments.size();
        for(Map.Entry<String, String> entry : nameSegments){
            sb.append(entry.getValue());
            if(--size > 0){
                sb.append('.');
            }
        }
        return sb.toString();
    }


    //
    // Utility Classes
    //

    @Override
    public String toString() {
        return "ContainmentBasedName{" +
                "nameSegments=" + nameSegments +
                '}';
    }

}
