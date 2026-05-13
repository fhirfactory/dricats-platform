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
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import net.fhirfactory.dricats.internals.common.identifiers.ElementIdentifier;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.CommonQualifier;
import net.fhirfactory.dricats.internals.common.naming.DistinguishedName;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.ApplicationComponentSpecialisationEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.reference.archimate.common.valuesets.ElementTypeEnum;
import net.fhirfactory.dricats.ui.uitest.configuration.UITestServerConfiguration;
import net.fhirfactory.dricats.ui.uitest.configuration.UITestServerConfigurationLoader;
import net.fhirfactory.dricats.ui.uitest.testdata.PathwayTestResourceSetBuilder;
import net.fhirfactory.dricats.ui.uitest.testdata.TopologyTestResourceSetBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ApplicationScoped
public class UitestApplication implements ISubsystem {
    //
     // Housekeeping
    //
    private static final Logger LOG = LoggerFactory.getLogger(UitestApplication.class);

    //
     // Attributes
    //

    private boolean initialized = false;
    private UITestServerConfiguration uiTestServerConfiguration;

    @Inject
    TopologyTestResourceSetBuilder topologyTestResourceSetBuilder;

    @Inject
    PathwayTestResourceSetBuilder pathwayTestResourceSetBuilder;

    //
     // Constructor
    //

    public UitestApplication() {

    }

    //
     // Camel Context
    //

    @Produces
    @ApplicationScoped
    public CamelContext createCamelContext() {
        return new DefaultCamelContext();
    }

    // Post Construct
    @PostConstruct
    public void initialise(){
        getLogger().debug(".initalise(): Entry");
        if (!isInitialized()) {
            getLogger().info("UitestApplication::initialise(): Initialising....");
            try {
                createCamelContext();
            } catch (Exception ex){
                getLogger().error(ex.getMessage());
            }
            getLogger().info("UitestApplication::initialise(): [Load Configuration File] Start");
            UITestServerConfigurationLoader configurationLoader = new UITestServerConfigurationLoader();
            this.uiTestServerConfiguration = (UITestServerConfiguration) configurationLoader.readPropertyFile();
            getLogger().info("UitestApplication::initialise(): [Load Configuration File] Finish");
            System.out.println("UitestApplication::initialise(): Booted!!!!!");
            topologyTestResourceSetBuilder.initialise();
            pathwayTestResourceSetBuilder.initialise();
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

    // Getters and Setters
    public Subsystem getSubsystem() {
        Subsystem subsystem = new Subsystem() {
            @Override
            protected ElementIdentifier specifySubsystemIdentifier() {
                CommonQualifier qualifier = new CommonQualifier();
                qualifier.getNameMap().put(0, ApplicationComponentSpecialisationEnum.SOLUTION.name());
                qualifier.getNameMap().put(1, ApplicationComponentSpecialisationEnum.SUBSYSTEM.name());
                CommonName name = new CommonName();
                name.getNameMap().put(0, "UITestHarness");
                name.getNameMap().put(1, "UITestPresentationManager");

                DistinguishedName dn = new DistinguishedName(qualifier, name);
                ElementIdentifier elementIdentifier = new ElementIdentifier(dn);
                return(elementIdentifier);
            }
        };
        return(subsystem);
    }
}


