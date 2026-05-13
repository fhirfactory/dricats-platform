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
package net.fhirfactory.dricats.internals.events.factories;

import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.events.common.EventBase;
import org.apache.commons.lang3.SerializationUtils;

public class EventBaseFactory {

    public static EventBase fromPrevious(EventBase eventBase){
        if(eventBase == null){
            return null;
        }
        if(eventBase.getElementInstanceId() == null){
            return null;
        }
        EventBase newEventBase = new EventBase(eventBase);
        ElementIdentifier newName = SerializationUtils.clone(eventBase.getIdentifier());
        newName.getIdentifierValue().getUnqualifiedName().setValue(newEventBase.getElementInstanceId().getIdValue());
        newEventBase.setShortName(eventBase.getShortName());
        newEventBase.setIdentifier(newName);
        newEventBase.getHistory().put(eventBase.getHistory().size()+1, eventBase.getReference());
        return newEventBase;
    }
}
