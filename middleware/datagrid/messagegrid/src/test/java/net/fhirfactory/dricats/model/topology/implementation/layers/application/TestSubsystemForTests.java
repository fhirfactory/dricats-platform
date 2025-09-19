package net.fhirfactory.dricats.model.topology.implementation.layers.application;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.DistributableObjectIdentifier;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedNameEntry;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.Subsystem;
import net.fhirfactory.dricats.internals.topology.implementation.layers.application.valuesets.SoftwareComponentTypeEnum;

/**
 * Concrete test stub of Subsystem placed in the same package so it can implement
 * the package-private abstract method specifySubsystemIdentifier().
 */
public class TestSubsystemForTests extends Subsystem {
    public TestSubsystemForTests(String localSubsystemName) {
        super();
        DistributableObjectId id = new DistributableObjectId();
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedNameEntry(SoftwareComponentTypeEnum.SOLUTION.getType(), "dricats-test"));
        qn.appendUnqualifiedName(new UnqualifiedNameEntry(SoftwareComponentTypeEnum.SUBSYSTEM.getType(), "dricats-test-" + localSubsystemName));
        qn.appendUnqualifiedName(new UnqualifiedNameEntry(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_CLUSTER.getType(), "dricats-test-" + localSubsystemName + "-cluster"));
        qn.appendUnqualifiedName(new UnqualifiedNameEntry(SoftwareComponentTypeEnum.SUBSYSTEM_APPLICATION_INSTANCE.getType(), localSubsystemName));
        id.setQualifiedName(qn);
        this.setObjectID(id);
    }

    @Override
    public DistributableObjectIdentifier specifySubsystemIdentifier() {
        return new DistributableObjectIdentifier();
    }
}
