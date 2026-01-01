/*
 * Copyright (c) 2025 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.common.id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ObjectKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long upperBits;
    private Long lowerBits;
    private Long version;

    //
    // Constructors
    //

    public ObjectKey() {
        UUID value = UUID.randomUUID();
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = 0L;
    }

    public ObjectKey(UUID value) {
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = 0L;
    }

    public ObjectKey(UUID value, Long version) {
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = version;
    }

    public ObjectKey(Long upperBits, Long lowerBits, Long version) {
        this.upperBits = upperBits;
        this.lowerBits = lowerBits;
        this.version = version;
    }

    public ObjectKey(String longId){
        if(longId.isEmpty()){
            throw new IllegalArgumentException("longId is empty");
        }
        fromHexId(longId);
    }

    //
    // Accessors / Mutators
    //

    @JsonIgnore
    public String getKeyValue() {
        return toHexId();
    }

    @JsonIgnore
    public void setKeyValue(String value) {
        fromHexId(value);
    }

    public Long getUpperBits() {
        return upperBits;
    }

    public void setUpperBits(Long upperBits) {
        this.upperBits = upperBits;
    }

    public Long getLowerBits() {
        return lowerBits;
    }

    public void setLowerBits(Long lowerBits) {
        this.lowerBits = lowerBits;
    }

    public Long getVersion(){
        return(this.version);
    }

    public void setVersion(Long version){
        this.version = version;
    }

    //
    // Helper Methods
    //

    /**
     * Returns the key as three 16-digit hexadecimal strings.
     * Format: upperBitslowerBitsversion
     */
    public String toHexId() {
        return String.format("%016x%016x%016x", upperBits, lowerBits, version);
    }

    /**
     * Takes a 48-character string, splits it into 3 x 16 character blocks
     * and assigns them to upperBits, lowerBits and version.
     * @param hexId 48 character hex string
     */
    public void fromHexId(String hexId) {
        if (hexId == null || hexId.length() != 48) {
            throw new IllegalArgumentException("hexId must be 48 characters long");
        }
        String upperStr = hexId.substring(0, 16);
        String lowerStr = hexId.substring(16, 32);
        String versionStr = hexId.substring(32, 48);

        this.upperBits = Long.parseUnsignedLong(upperStr, 16);
        this.lowerBits = Long.parseUnsignedLong(lowerStr, 16);
        this.version = Long.parseUnsignedLong(versionStr, 16);
    }

    @Override
    public String toString() {
        return "ObjectKey{" +
                "upperBits=" + String.format("%016x", upperBits) +
                ", lowerBits=" + String.format("%016x", lowerBits) +
                ", version=" + String.format("%016x", version) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectKey objectKey = (ObjectKey) o;
        return Objects.equals(upperBits, objectKey.upperBits) &&
                Objects.equals(lowerBits, objectKey.lowerBits) &&
                Objects.equals(version, objectKey.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upperBits, lowerBits, version);
    }

}
