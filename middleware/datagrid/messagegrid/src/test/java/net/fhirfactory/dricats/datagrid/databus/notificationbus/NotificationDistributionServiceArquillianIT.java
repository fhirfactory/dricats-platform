package net.fhirfactory.dricats.datagrid.databus.notificationbus;

import net.fhirfactory.dricats.datagrid.DataBusServicesGroup;
import net.fhirfactory.dricats.datagrid.notificationbus.NotificationDistributionService;
import net.fhirfactory.dricats.deployment.valuesets.SubsystemInternalServiceNamesEnum;
import net.fhirfactory.dricats.internals.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.messaging.NotificationObject;
import net.fhirfactory.dricats.internals.messaging.NotificationSet;
import net.fhirfactory.dricats.internals.messaging.interfaces.ILocalNotificationService;
import net.fhirfactory.dricats.internals.oam.ApplicationComponentMetricsData;
import net.fhirfactory.dricats.internals.oam.interfaces.ILocalMetricsServerInterface;
import net.fhirfactory.dricats.internals.reference.layers.application.ApplicationComponent;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.model.topology.implementation.layers.application.TestSubsystemForTests;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;
import net.fhirfactory.dricats.internals.topology.interfaces.ISubsystem;
import net.fhirfactory.dricats.platform.configuration.LocalConfigurationServer;
import net.fhirfactory.dricats.platform.middleware.jgroups.JChannelInterface;
import net.fhirfactory.dricats.platform.middleware.jgroups.JGroupsNamingServices;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelConfiguration;
import net.fhirfactory.dricats.platform.middleware.jgroups.configuration.JChannelTCPConfiguration;
import net.fhirfactory.dricats.platform.middleware.jgroups.jchannel.base.JChannelFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RunWith(Arquillian.class)
@org.junit.Ignore("Temporarily disabled while stabilizing Weld Embedded Arquillian configuration (no WildFly required)")
public class NotificationDistributionServiceArquillianIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "notification-dist-service-it.jar")
                // Service under test and its group component
                .addClasses(NotificationDistributionService.class, DataBusServicesGroup.class)
                // Stubs and test-only producers
                .addClasses(LocalNotificationServiceStub.class,
                        MetricsServerStub.class,
                        SubsystemStub.class,
                        NamingServicesStub.class,
                        ChannelFactoryStub.class,
                        TestConfigProducer.class)
                // JGroups/JChannel minimal config classes used by stubs
                .addClasses(JChannelConfiguration.class, JChannelTCPConfiguration.class, JChannelInterface.class)
                // Model/value classes used during wiring
                .addClasses(DistributableObjectId.class, QualifiedName.class, UnqualifiedName.class,
                        Subsystem.class, SoftwareComponentTypeEnum.class, TestSubsystemForTests.class)
                // beans.xml to enable CDI
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private NotificationDistributionService notificationDistributionService;

    @Inject
    private ILocalNotificationService localNotificationService;

    @Inject
    private DataBusServicesGroup dataBusServicesGroup;

    @Inject
    private LocalConfigurationServer configurationServer;

    @Inject
    private ISubsystem subsystem;

    @Inject
    private ILocalMetricsServerInterface metricsServer;

    @Inject
    private JGroupsNamingServices namingServices;

    @Inject
    private JChannelFactory channelFactory;

    @Test
    public void should_Inject_All_Dependencies_And_List_Them() {
        Assert.assertNotNull("NotificationDistributionService should be injected", notificationDistributionService);
        Assert.assertNotNull("ILocalNotificationService should be injected", localNotificationService);
        Assert.assertNotNull("DataBusServicesGroup should be injected", dataBusServicesGroup);
        Assert.assertNotNull("LocalConfigurationServer should be injected", configurationServer);
        Assert.assertNotNull("ISubsystem should be injected", subsystem);
        Assert.assertNotNull("ILocalMetricsServerInterface should be injected", metricsServer);
        Assert.assertNotNull("JGroupsNamingServices should be injected", namingServices);
        Assert.assertNotNull("JChannelFactory should be injected", channelFactory);

        // List dependent injectable services required by NotificationDistributionService
        System.out.println("[ArquillianIT] Dependent injectable services for NotificationDistributionService:");
        System.out.println(" - ILocalNotificationService: " + localNotificationService.getClass().getName());
        System.out.println(" - DataBusServicesGroup: " + dataBusServicesGroup.getClass().getName());
        System.out.println(" - LocalConfigurationServer: " + configurationServer.getClass().getName());
        System.out.println(" - ISubsystem: " + subsystem.getClass().getName());
        System.out.println(" - ILocalMetricsServerInterface: " + metricsServer.getClass().getName());
        System.out.println(" - JGroupsNamingServices: " + namingServices.getClass().getName());
        System.out.println(" - JChannelFactory: " + channelFactory.getClass().getName());
    }

    // ---- Test stubs ----

    @ApplicationScoped
    public static class LocalNotificationServiceStub implements ILocalNotificationService {
        @Override
        public LocalDateTime postNotification(NotificationObject notification) { return LocalDateTime.now(); }
        @Override
        public NotificationObject peekNextNotification(QualifiedNameToken consumer) { return null; }
        @Override
        public NotificationObject pollNextNotification(QualifiedNameToken consumer) { return null; }
        @Override
        public NotificationSet pollNextNotification(QualifiedNameToken consumer, Integer size) { return null; }
    }

    @ApplicationScoped
    public static class MetricsServerStub implements ILocalMetricsServerInterface {
        @Override
        public void addMetrics(ApplicationComponent softwareComponent, ApplicationComponentMetricsData metricsData) { }
        @Override
        public ApplicationComponentMetricsData getMetrics(ApplicationComponent softwareComponent) { return null; }
        @Override
        public List<ApplicationComponentMetricsData> getMetrics(LocalDateTime metricsStartTime, LocalDateTime metricsEndTime) { return Collections.emptyList(); }
        @Override
        public void registerFailedComponent(ApplicationComponent softwareComponent, String failureDescription) { }
    }

    @ApplicationScoped
    public static class SubsystemStub implements ISubsystem {
        private final Subsystem subsystem;
        public SubsystemStub() {
            // Provide a minimal Subsystem with an object ID so DataBusServicesGroup can form its ID
            this.subsystem = new TestSubsystemForTests("TestSubsystem");
            QualifiedName qn = new QualifiedName();
            qn.appendUnqualifiedName(new UnqualifiedName(SoftwareComponentTypeEnum.SUBSYSTEM.getType(), "TestSubsystem"));
            this.subsystem.setObjectID(new DistributableObjectId(qn));
        }
        @Override
        public Subsystem getSubsystem() { return subsystem; }
    }

    @ApplicationScoped
    public static class NamingServicesStub extends JGroupsNamingServices { }

    @ApplicationScoped
    public static class ChannelFactoryStub extends JChannelFactory {
        // Use default behavior; it will attempt to create a JChannel using provided configuration.
        // For embedded container, ensure config is lightweight (localhost) via TestConfigProducer below.
    }

    @Singleton
    public static class TestConfigProducer {
        @Inject
        LocalConfigurationServer configurationServer;

        @PostConstruct
        public void init() {
            // Prepare a minimal JChannel endpoint config and register it with the expected name
            JChannelTCPConfiguration cfg = new JChannelTCPConfiguration();
            cfg.setEndpointHost("127.0.0.1");
            cfg.setEndpointPort(7800);
            cfg.setClusterName("test-cluster");
            cfg.setChannelName("notification-channel");

            DistributableObjectId owner = new DistributableObjectId("owner.test");
            JChannelInterface endpoint = new JChannelInterface(owner, cfg, null);
            endpoint.setConfiguration(cfg);

            configurationServer.registerConfigurationObject(
                    SubsystemInternalServiceNamesEnum.NOTIFICATION_SERVICE_ENDPOINT.getName(),
                    endpoint
            );
        }
    }
}