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
package net.fhirfactory.dricats.middleware.oam.uitest;

import net.fhirfactory.dricats.middleware.oam.uitest.configuration.UITestServerConfiguration;
import net.fhirfactory.dricats.middleware.oam.uitest.configuration.UITestServerConfigurationLoader;
import net.fhirfactory.dricats.model.configuration.configurationfile.archetypes.BaseSubsystemConfigurationObject;
import org.apache.camel.main.Main;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UitestApplication {
    private static final Logger LOG = Logger.getLogger(UitestApplication.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting DRICaTS OAM UI Test Stub");
        if (LOG.isDebugEnabled()) {
       //     LOG.debug("Startup args (count={}): " + (args == null ? 0 : args.length), (Object) args);
        }

        UITestServerConfigurationLoader configurationLoader = new UITestServerConfigurationLoader();

        UITestServerConfiguration applicationConfiguration = (UITestServerConfiguration)configurationLoader.readPropertyFile();

        LOG.error("Starting DRICaTS OAM UI Test Stub with configuration: " + applicationConfiguration);

        Main main = new Main();
        // Add our routes
        main.configure().addRoutesBuilder(new UitestOamRestRoute());

        // Add shutdown hook for clearer lifecycle logs
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOG.info("Shutting down DRICaTS OAM UI Test Stub");
                main.stop();
            } catch (Exception e) {
                LOG.error("Error during shutdown", e);
            }
        }));

        try {
            // Start Camel (blocks)
            main.run(args);
        } catch (Exception e) {
            LOG.error("Fatal error running Camel Main", e);
            throw e;
        }
    }
}
