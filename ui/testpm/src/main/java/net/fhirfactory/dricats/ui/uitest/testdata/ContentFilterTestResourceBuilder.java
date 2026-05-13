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
package net.fhirfactory.dricats.ui.uitest.testdata;

import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.common.naming.RelativeDistinguishedName;
import net.fhirfactory.dricats.internals.data.valuesets.MimeTypeEnum;
import net.fhirfactory.dricats.internals.pubsub.common.ApplicationComponentIdMask;
import net.fhirfactory.dricats.internals.pubsub.common.EventTemporalWindow;
import net.fhirfactory.dricats.internals.pubsub.common.QualifiedNameMask;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilter;
import net.fhirfactory.dricats.internals.pubsub.content.ContentFilterMask;
import net.fhirfactory.dricats.internals.pubsub.topics.TopicFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ContentFilterTestResourceBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(ContentFilterTestResourceBuilder.class);

    protected ContentFilter createHL7v2ContentFilter(String messageType, String triggerEvent){
        ContentFilterMask newFilterMask = new ContentFilterMask();
        newFilterMask.getSupportedMediaTypes().add(MimeTypeEnum.TXT);
        TopicFilter topicFilter = new TopicFilter();
        DistinguishedName qualifiedNameMask = new DistinguishedName();
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Domain", "Health"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Standard", "HL7v2"));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Group", messageType));
        qualifiedNameMask.appendUnqualifiedName(new RelativeDistinguishedName("Trigger", triggerEvent));
        topicFilter.setMask(qualifiedNameMask);
        topicFilter.setIncludeContained(true);
        ApplicationComponentIdMask idMask = new ApplicationComponentIdMask();
        QualifiedNameMask sourceMask = new QualifiedNameMask();
        DistinguishedName qualifiedName = new DistinguishedName();
        qualifiedName.appendUnqualifiedName(new RelativeDistinguishedName("CamelRoute", "*"));
        sourceMask.setMask(qualifiedName);
        sourceMask.setIncludeContained(true);
        idMask.setComponentIdMask(sourceMask);
        newFilterMask.setInternalEventSource(idMask);
        newFilterMask.setEventFinalDestination("*");
        newFilterMask.setEventOrigin("*");
        newFilterMask.setTemporalWindow(new EventTemporalWindow());
        newFilterMask.getEventTopicFilters().add(topicFilter);
        ContentFilter contentFilter = new ContentFilter();
        contentFilter.setContentFilterMask(newFilterMask);
        contentFilter.setShortName(messageType+"."+triggerEvent);
        DistinguishedName subscriptionDN = new DistinguishedName();
        subscriptionDN.appendUnqualifiedName(new RelativeDistinguishedName("Filter", messageType + "." + triggerEvent));
        ElementIdentifier identifier = new ElementIdentifier(subscriptionDN);
        contentFilter.setIdentifier(identifier);
        return contentFilter;
    }
}
