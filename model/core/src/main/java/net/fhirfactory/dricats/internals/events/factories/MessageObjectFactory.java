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
import net.fhirfactory.dricats.internals.events.messages.MessageObject;
import org.apache.commons.lang3.SerializationUtils;

import java.util.UUID;

public class MessageObjectFactory extends EventBaseFactory{

    public static MessageObject fromPrevious(MessageObject messageObject){
        if(messageObject == null){
            return null;
        }
        if(messageObject.getElementInstanceId() == null){
            return null;
        }
        MessageObject newMessageObject = new MessageObject(messageObject);
        newMessageObject.setMessageSequenceNumber(messageObject.getMessageSequenceNumber()+1);
        UUID uuid = UUID.randomUUID();
        StringBuilder messageShortNameBuilder = new StringBuilder();
        messageShortNameBuilder.append(messageObject.getIdentifier().getIdentifierValue().getUnqualifiedName().getQualifier());
        messageShortNameBuilder.append("->");
        messageShortNameBuilder.append(uuid.toString());
        newMessageObject.setShortName(messageShortNameBuilder.toString());
        ElementIdentifier newName = SerializationUtils.clone(messageObject.getIdentifier());
        newName.getIdentifierValue().getUnqualifiedName().setValue(uuid.toString());
        newMessageObject.setIdentifier(newName);
        newMessageObject.getHistory().put(messageObject.getHistory().size()+1, messageObject.getReference());
        return newMessageObject;
    }
}
