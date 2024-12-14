/*
 * Copyright (c) 2024 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
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
package net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base;

import net.fhirfactory.dricats.internals.model.oam.interfaces.LocalMetricsServerInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class JChannelWatchdog {
    //
    // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(JChannelWatchdog.class);

    //
    // Attributes
    //
    private JChannelEndpoint endpoint;
    private LocalMetricsServerInterface metricsServer;
    private LocalDateTime startInstant;
    private LocalDateTime lastRunInstant;

    //
    // Constants
    //
    private static final Integer WATCHDOG_FAILURE_COUNT_MAY = 5;
    private static final Long WATCHDOG_EXECUTION_PERIOD = 10000L;

    //
    // Constructor(s)
    //
    public JChannelWatchdog(JChannelEndpoint endpoint, LocalMetricsServerInterface metricsServer) {
        this.endpoint = endpoint;
        this.metricsServer = metricsServer;
        this.startInstant = LocalDateTime.now();
    }

    //
    // Business Methods
    //

    public void startWatchdog() {
        getLogger().debug(".startWatchdog(): Entry");
        setLastRunInstant(LocalDateTime.now());

        getLogger().debug(".startWatchdog(): Exit");
    }

    public void scheduleWatchdog() {
        getLogger().debug(".scheduleWatchdog(): Entry");

        getLogger().debug(".scheduleWatchdog(): Exit");
    }


    //
    // Getters and Setters
    //
    protected Logger getLogger() {
        return LOG;
    }

    protected JChannelEndpoint getEndpoint() {
        return endpoint;
    }

    protected LocalMetricsServerInterface getMetricsServer() {
        return metricsServer;
    }

    protected LocalDateTime getStartInstant() {
        return startInstant;
    }

    protected LocalDateTime getLastRunInstant() {
        return lastRunInstant;
    }

    protected void setLastRunInstant(LocalDateTime lastRunInstant) {
        this.lastRunInstant = lastRunInstant;
    }
}
