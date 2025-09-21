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
package net.fhirfactory.dricats.internals.pubsub.common;

import net.fhirfactory.dricats.internals.common.datatypes.EffectiveDate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class EventTemporalWindow extends EffectiveDate implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final long serialVersionUID = 1L;

    //
    // Attributes
    //


    //
     // Constructor(s)
    //

    public EventTemporalWindow() {
        super();
        setEffectiveStartDate(LocalDateTime.MIN);
        setEffectiveEndDate(LocalDateTime.MAX);
    }

    //
     // Business Methods
    //

    public Boolean isWithinTemporalWindow(LocalDateTime testDate){
        return(testDate.isAfter(getEffectiveStartDate()) && testDate.isBefore(getEffectiveEndDate()));
    }

    public Boolean isWithinTemporalWindow(EffectiveDate testDate){
        return(testDate.getEffectiveStartDate().isAfter(getEffectiveStartDate()) && testDate.getEffectiveStartDate().isBefore(getEffectiveEndDate()));
    }

    //
    // Standard Methods
    //

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .toString();
    }
}
