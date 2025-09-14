package net.fhirfactory.dricats.model.common;

import net.fhirfactory.dricats.model.common.naming.QualifiedName;
import net.fhirfactory.dricats.model.common.naming.QualifiedNameToken;
import net.fhirfactory.dricats.model.common.naming.UnqualifiedName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class DistributableObjectIdTest {
    private static final Logger LOG = LoggerFactory.getLogger(DistributableObjectIdTest.class);

    @Test
    void defaultConstructorAndBeanMethods() {
        LOG.debug(".defaultConstructorAndBeanMethods(): Testing default constructor and bean methods: Start");
        LOG.trace(".defaultConstructorAndBeanMethods(): Creating DistributableObjectId Start");
        DistributableObjectId id = new DistributableObjectId();
        LOG.trace(".defaultConstructorAndBeanMethods(): Creating DistributableObjectId Finish");
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing Not Null Id: Start");
        assertNotNull(id.getQualifiedName());
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing Not Null Id: Finish");
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing Not Null Effective Date: Start");
        assertNotNull(id.getEffectiveDate());
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing Not Null Effective Date: Finish");

        LOG.trace(".defaultConstructorAndBeanMethods(): Testing setQualifiedName: Start");
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName("A","a"));
        id.setQualifiedName(qn);
        assertEquals(qn, id.getQualifiedName());
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing setQualifiedName: Finish");

        LOG.trace(".defaultConstructorAndBeanMethods(): Testing toString: Start");
        assertNotNull(id.toString());
        LOG.trace(".defaultConstructorAndBeanMethods(): Testing toString: Finish");
        LOG.debug(".defaultConstructorAndBeanMethods(): Testing default constructor and bean methods: Finish");
    }

    @Test
    void constructorWithQualifiedNameSetsEffectiveDates() {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName("Q","v"));
        DistributableObjectId id = new DistributableObjectId(qn);
        assertEquals(qn, id.getQualifiedName());
        assertNotNull(id.getEffectiveDate());
        assertNotNull(id.getEffectiveDate().getEffectiveEndDate());
    }

    @Test
    void constructorWithTokenParsesQualifiedName() {
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName("T","x"));
        qn.appendUnqualifiedName(new UnqualifiedName("U","y"));
        String token = new QualifiedNameToken(qn).getContent();

        DistributableObjectId id = new DistributableObjectId(token);
        assertNotNull(id.getQualifiedName());
        assertEquals(qn, id.getQualifiedName());
        assertNotNull(id.getEffectiveDate());
        assertNotNull(id.getEffectiveDate().getEffectiveEndDate());
    }

    @Test
    void equalsAndHashCode() {
        QualifiedName q = new QualifiedName();
        q.appendUnqualifiedName(new UnqualifiedName("A","1"));

        DistributableObjectId a = new DistributableObjectId(q);
        DistributableObjectId b = new DistributableObjectId(new QualifiedName(q));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        // Mutate b
        b.getQualifiedName().appendUnqualifiedName(new UnqualifiedName("B","2"));
        assertNotEquals(a, b);
    }
}
