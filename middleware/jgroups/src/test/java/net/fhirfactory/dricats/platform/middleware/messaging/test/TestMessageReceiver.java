package net.fhirfactory.dricats.platform.middleware.messaging.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMessageReceiver extends TestMessageServiceCommon{
    public static final Logger LOG = LoggerFactory.getLogger(TestMessageReceiver.class);

    protected Logger getLogger() {
        return LOG;
    }
}
