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
package net.fhirfactory.dricats.ui.uitest;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.fhirfactory.dricats.ui.uitest.configuration.UITestServerConfiguration;
import net.fhirfactory.dricats.ui.uitest.configuration.UITestServerConfigurationLoader;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class UitestApplication extends RouteBuilder {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UitestApplication.class);

    //
     // Attributes
    //

    private boolean initialized = false;
    private UITestServerConfiguration uiTestServerConfiguration;

    //
     // Constructor
    //

    public UitestApplication() {

    }

    // Post Construct
    @PostConstruct
    public void initialise(){
        getLogger().debug(".initalise(): Entry");
        if (!isInitialized()) {
            getLogger().info("UitestApplication::initialise(): Initialising....");
            getLogger().info("UitestApplication::initialise(): [Load Configuration File] Start");
            UITestServerConfigurationLoader configurationLoader = new UITestServerConfigurationLoader();
            this.uiTestServerConfiguration = (UITestServerConfiguration) configurationLoader.readPropertyFile();
            getLogger().info("UitestApplication::initialise(): [Load Configuration File] Finish");
            System.out.println("UitestApplication::initialise(): Booted!!!!!");
            getLogger().info("UitestApplication::initialise(): [Finish Initialisation] Start");
            setInitialized(true);
            getLogger().info("UitestApplication::initialise(): [Finish Initialisation] Finish");
            getLogger().info("UitestApplication::initialise(): Initialising.... Done!");
        }
        getLogger().info(".initialise(): Exit");

    }

    // Getters and Setters

    protected Logger getLogger(){
        return(LOG);
    }

    public UITestServerConfiguration getUiTestServerConfiguration() {
        return uiTestServerConfiguration;
    }

    public void setUiTestServerConfiguration(UITestServerConfiguration uiTestServerConfiguration) {
        this.uiTestServerConfiguration = uiTestServerConfiguration;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    // Simple Route

    @Override
    public void configure() throws Exception {
        String processingPlantName = getClass().getSimpleName();

        from("timer://"+processingPlantName+"?delay=1000&repeatCount=1")
                .routeId("ProcessingPlant::"+processingPlantName)
                .log(LoggingLevel.DEBUG, "Starting....");
    }
}


