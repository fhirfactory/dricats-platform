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
package net.fhirfactory.dricats.model.messaging.interfaces;

import net.fhirfactory.dricats.model.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.model.messaging.NotificationObject;
import net.fhirfactory.dricats.model.messaging.NotificationSet;

import java.time.LocalDateTime;
import java.util.Map;

public interface ILocalNotificationService {
    public LocalDateTime postNotification(NotificationObject notification);
    public NotificationObject peekNextNotification(QualifiedNameToken consumer);
    public NotificationObject pollNextNotification(QualifiedNameToken consumer);
    public NotificationSet pollNextNotification(QualifiedNameToken consumer, Integer size);
}
