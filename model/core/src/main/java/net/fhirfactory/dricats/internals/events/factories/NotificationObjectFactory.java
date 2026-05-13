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
import net.fhirfactory.dricats.internals.events.notifications.NotificationObject;
import org.apache.commons.lang3.SerializationUtils;

import java.util.UUID;

public class NotificationObjectFactory extends EventBaseFactory{

    public static NotificationObject fromPrevious(NotificationObject notificationObject){
        if(notificationObject == null){
            return null;
        }
        if(notificationObject.getElementInstanceId() == null){
            return null;
        }
        NotificationObject newNotificationObject = new NotificationObject(notificationObject);
        UUID uuid = UUID.randomUUID();
        StringBuilder notificationShortNameBuilder = new StringBuilder();
        notificationShortNameBuilder.append(notificationObject.getIdentifier().getIdentifierValue().getUnqualifiedName().getQualifier());
        notificationShortNameBuilder.append("->");
        notificationShortNameBuilder.append(uuid.toString());
        newNotificationObject.setShortName(notificationShortNameBuilder.toString());
        ElementIdentifier newName = SerializationUtils.clone(notificationObject.getIdentifier());
        newName.getIdentifierValue().getUnqualifiedName().setValue(uuid.toString());
        newNotificationObject.setIdentifier(newName);
        newNotificationObject.getHistory().put(notificationObject.getHistory().size()+1, notificationObject.getReference());
        return newNotificationObject;
    }
}
