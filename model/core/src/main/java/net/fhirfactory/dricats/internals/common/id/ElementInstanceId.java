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

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ElementInstanceId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonFormat()
    private String idValue;
    private Long upperBits;
    private Long lowerBits;
    private Long version;

    //
    // Constructors
    //

    public ElementInstanceId() {
        UUID value = UUID.randomUUID();
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = 0L;
        idValue = toHexId();
    }

    public ElementInstanceId(UUID value) {
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = 0L;
        idValue = toHexId();
    }

    public ElementInstanceId(UUID value, Long version) {
        this.upperBits = value.getMostSignificantBits();
        this.lowerBits = value.getLeastSignificantBits();
        this.version = version;
        idValue = toHexId();
    }

    public ElementInstanceId(Long upperBits, Long lowerBits, Long version) {
        this.upperBits = upperBits;
        this.lowerBits = lowerBits;
        this.version = version;
        idValue = toHexId();
    }

    public ElementInstanceId(String idValue){
        this();
        if(idValue.isEmpty()){
            throw new IllegalArgumentException("idValue is empty");
        }
        fromHexId(idValue);
        this.idValue = idValue;
    }

    //
    // Accessors / Mutators
    //

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        fromHexId(idValue);
        this.idValue = idValue;
    }

    public Long getUpperBits() {
        return upperBits;
    }

    public void setUpperBits(Long upperBits) {
        this.upperBits = upperBits;
        this.idValue = toHexId();
    }

    public Long getLowerBits() {
        return lowerBits;
    }

    public void setLowerBits(Long lowerBits) {
        this.lowerBits = lowerBits;
        this.idValue = toHexId();
    }

    public Long getVersion(){
        return(this.version);
    }

    public void setVersion(Long version){
        this.version = version;
        this.idValue = toHexId();
    }

    //
    // Helper Methods
    //

    public String resolveKey(){
        return(getIdValue());
    }

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
            if(hexId != null) {
                this.idValue = hexId;
                return;
            } else {
                throw new IllegalArgumentException("hexId cannot be null");
            }
        }
        String upperStr = hexId.substring(0, 16);
        String lowerStr = hexId.substring(16, 32);
        String versionStr = hexId.substring(32, 48);

        this.upperBits = Long.parseUnsignedLong(upperStr, 16);
        this.lowerBits = Long.parseUnsignedLong(lowerStr, 16);
        this.version = Long.parseUnsignedLong(versionStr, 16);
        this.idValue = hexId;
    }

    @Override
    public String toString() {
        return "ElementInstanceId{" +
                "upperBits=" + String.format("%016x", upperBits) +
                ", lowerBits=" + String.format("%016x", lowerBits) +
                ", version=" + String.format("%016x", version) +
                ", localName='" + idValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementInstanceId elementInstanceId = (ElementInstanceId) o;
        return Objects.equals(upperBits, elementInstanceId.upperBits) &&
                Objects.equals(lowerBits, elementInstanceId.lowerBits) &&
                Objects.equals(version, elementInstanceId.version) &&
                Objects.equals(idValue, elementInstanceId.idValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upperBits, lowerBits, version, idValue);
    }

}
