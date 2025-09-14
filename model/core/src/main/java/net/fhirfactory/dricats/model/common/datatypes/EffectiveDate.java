/*
 * Copyright (c) 2024 Mark A. Hunter
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

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import net.fhirfactory.dricats.common.DateUtility;
import net.fhirfactory.dricats.deployment.contants.DefaultDeploymentConstants;
import net.fhirfactory.dricats.model.common.SerialisableObject;

public class EffectiveDate extends SerialisableObject {
    //
    // Housekeeping
    //

    @Serial
    private static final long serialVersionUID = -12345678900006L;

    //
    // Attributes
    //

    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime effectiveStartDate;
    @JsonFormat(pattern = DateUtility.DEFAULT_JSON_FORMAT,  timezone = DefaultDeploymentConstants.DEPLOYMENT_TIMEZONE)
    private LocalDateTime effectiveEndDate;

	//
	// Constructor(s)
	//

	public EffectiveDate(){
		setEffectiveStartDate(LocalDateTime.now());
		setEffectiveEndDate(LocalDateTime.MAX);
	}

	public EffectiveDate(LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate) {
		setEffectiveStartDate(effectiveStartDate);
		setEffectiveEndDate(effectiveEndDate);
	}

    //
    // Bean Methods
    //

    public LocalDateTime getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDateTime effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public LocalDateTime getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDateTime effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    //
    // Utility Methods
    //
    

	@Override
	public int hashCode() {
		return Objects.hash(effectiveEndDate, effectiveStartDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EffectiveDate other = (EffectiveDate) obj;
        boolean startYearOK = effectiveStartDate.getYear() >= other.effectiveStartDate.getYear();
        boolean startMonthOK = effectiveStartDate.getMonthValue() >= other.effectiveStartDate.getMonthValue();
        boolean startDayOK = effectiveStartDate.getDayOfMonth() >= other.effectiveStartDate.getDayOfMonth();
        boolean startHourOK = effectiveStartDate.getHour() >= other.effectiveStartDate.getHour();
        boolean startMinuteOK = effectiveStartDate.getMinute() >= other.effectiveStartDate.getMinute();
        boolean startSecondOK = effectiveStartDate.getSecond() >= other.effectiveStartDate.getSecond();
        boolean endYearOK = effectiveEndDate.getYear() <= other.effectiveEndDate.getYear();
        boolean endMonthOK = effectiveEndDate.getMonthValue() <= other.effectiveEndDate.getMonthValue();
        boolean endDayOK = effectiveEndDate.getDayOfMonth() <= other.effectiveEndDate.getDayOfMonth();
        boolean endHourOK = effectiveEndDate.getHour() <= other.effectiveEndDate.getHour();
        boolean endMinuteOK = effectiveEndDate.getMinute() <= other.effectiveEndDate.getMinute();
        boolean endSecondOK = effectiveEndDate.getSecond() <= other.effectiveEndDate.getSecond();
        return startYearOK && startMonthOK && startDayOK && endYearOK && endMonthOK && endDayOK && startHourOK && startMinuteOK && startSecondOK && endHourOK && endMinuteOK && endSecondOK;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EffectiveDate [effectiveStartDate=");
		builder.append(effectiveStartDate);
		builder.append(", effectiveEndDate=");
		builder.append(effectiveEndDate);
		builder.append(", id=");
		builder.append(getId());
		builder.append("]");
		return builder.toString();
	}
    
}
