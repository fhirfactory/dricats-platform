/*
 * Copyright (c) 2021 Mark A. Hunter
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
package net.fhirfactory.dricats.internals.common;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.fhirfactory.dricats.internals.common.datatypes.EffectiveDate;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.IdToken;

public class DistributableObjectId  implements Serializable {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900003L;

    //
    // Attributes
    //

    private QualifiedName qualifiedName;
    private EffectiveDate effectiveDate;

    //
     // Constructor(s)
    //

    public DistributableObjectId() {
        super();
        this.qualifiedName = new QualifiedName();
        this.effectiveDate = new EffectiveDate();
        this.effectiveDate.setEffectiveEndDate(LocalDateTime.MAX);
        this.effectiveDate.setEffectiveStartDate(LocalDateTime.now());
    }

    public DistributableObjectId(QualifiedName qualifiedName) {
        super();
        this.qualifiedName = qualifiedName;
        this.effectiveDate = new EffectiveDate();
        this.effectiveDate.setEffectiveEndDate(LocalDateTime.MAX);
        this.effectiveDate.setEffectiveStartDate(LocalDateTime.now());
    }

    public DistributableObjectId(String qualifiedNameToken){
        super();
        IdToken idTokenObject = new IdToken(qualifiedNameToken);
        this.qualifiedName = idTokenObject.toQualifiedName();
        this.effectiveDate = new EffectiveDate();
        this.effectiveDate.setEffectiveStartDate(LocalDateTime.now());
        this.effectiveDate.setEffectiveEndDate(LocalDateTime.MAX);
    }


    //
    // Bean Methods
    //

    public QualifiedName getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName( QualifiedName idValue) {
        this.qualifiedName = idValue;
    }

    public EffectiveDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(EffectiveDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    //
     // toToken
    //

    @JsonIgnore
    public IdToken getIdToken(){
        IdToken stringFromQualifiedName = getQualifiedName().getIdToken();
        return stringFromQualifiedName;
    }

    //
    // Utility Methods
    //

    @Override
    public String toString() {
        return new StringJoiner(", ", DistributableObjectId.class.getSimpleName() + "[", "]")
                .add("qualifiedName=" + getQualifiedName())
                .add("effectiveDate=" + getEffectiveDate())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistributableObjectId that = (DistributableObjectId) o;
        if (getQualifiedName() != null ? !getQualifiedName().equals(that.getQualifiedName()) : that.getQualifiedName() != null) return false;
        if (getEffectiveDate() != null ? !getEffectiveDate().equals(that.getEffectiveDate()) : that.getEffectiveDate() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getQualifiedName() != null ? getQualifiedName().hashCode() : 0;
        int resultYear = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getYear() : 0;
        int resultMonth = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getMonthValue() : 0;
        int resultDay = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getDayOfMonth() : 0;
        int resultHour = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getHour() : 0;
        int resultMinute = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getMinute() : 0;
        int resultSecond = getEffectiveDate() != null ? getEffectiveDate().getEffectiveStartDate().getSecond() : 0;
        result = 31 * result + resultYear;
        result = 31 * result + resultMonth;
        result = 31 * result + resultDay;
        result = 31 * result + resultHour;
        result = 31 * result + resultMinute;
        result = 31 * result + resultSecond;
        return result;
    }
}
