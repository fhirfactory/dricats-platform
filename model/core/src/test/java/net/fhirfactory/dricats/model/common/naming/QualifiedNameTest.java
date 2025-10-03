package net.fhirfactory.dricats.model.common.naming;

import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.IdToken;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class QualifiedNameTest {
    private static final Logger LOG = LoggerFactory.getLogger(QualifiedNameTest.class);

    @Test
    void defaultConstructorAndIsEmpty() {
        QualifiedName qn = new QualifiedName();
        assertTrue(qn.isEmpty());
        assertEquals(0, qn.getRelativeDNCount());
        assertNotNull(qn.getUnqualifiedNameEntries());
    }

    @Test
    void appendUnqualifiedNameAndGetters() {
        QualifiedName qn = new QualifiedName();
        UnqualifiedName a = new UnqualifiedName("Type","A");
        UnqualifiedName b = new UnqualifiedName("Type","B");
        qn.appendUnqualifiedName(a);
        qn.appendUnqualifiedName(b);
        assertEquals(2, qn.getRelativeDNCount());
        assertEquals("B", qn.getUnqualifiedName().getValue());
        assertEquals("Type", qn.getUnqualifiedName().getQualifier());
        assertFalse(qn.isEmpty());
    }

    @Test
    void copyConstructorEqualityAndHashCode() {
        LOG.debug(".copyConstructorEqualityAndHashCode(): Testing equals and hashCode methods for QualifiedName: Start");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Creating QualifiedName <q1> Start");
        QualifiedName q1 = new QualifiedName();
        LOG.trace(".copyConstructorEqualityAndHashCode(): Creating QualifiedName <q1> Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Appending <q1> with an UnqualifiedName <v1> Start");
        q1.appendUnqualifiedName(new UnqualifiedName("Q","v1"));
        LOG.trace(".copyConstructorEqualityAndHashCode(): Appending <q1> with an UnqualifiedName <v1> Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Appending <q1> with an UnqualifiedName <v2> Start");
        q1.appendUnqualifiedName(new UnqualifiedName("Q","v2"));
        LOG.trace(".copyConstructorEqualityAndHashCode(): Appending <q1> with an UnqualifiedName <v2> Finish");

        LOG.trace(".copyConstructorEqualityAndHashCode(): Creating QualifiedName <q2> as a clone of <q1> Start");
        QualifiedName q2 = new QualifiedName(q1);
        LOG.trace(".copyConstructorEqualityAndHashCode(): Creating QualifiedName <q2> as a clone of <q1> Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing equals <q1> & <q2>: Start");
        assertEquals(q1, q2);
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing equals <q1> & <q2>: Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing hashCode <q1> & <q2>: Start");
        assertEquals(q1.hashCode(), q2.hashCode());
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing hashCode <q1> & <q2>: Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing toString <q1> & <q2>: Start");
        assertEquals(q1.toString(), q2.toString());
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing toString <q1> & <q2>: Finish");
        LOG.trace(".copyConstructorEqualityAndHashCode(): Testing equals and hashCode methods for QualifiedName: Finish");
    }

    @Test
    void tokenConstructorRoundTrip() {
        QualifiedName original = new QualifiedName();
        original.appendUnqualifiedName(new UnqualifiedName("X","1"));
        original.appendUnqualifiedName(new UnqualifiedName("Y","2"));
        IdToken token = new IdToken(original);

        // Construct using QualifiedName(QualifiedNameToken)
        QualifiedName fromCtor = new QualifiedName(new IdToken(token.getContent()));
        assertEquals(original, fromCtor);

        // And via token conversion
        QualifiedName fromToken = new IdToken(token.getContent()).toQualifiedName();
        assertEquals(original, fromToken);
    }

    @Test
    void getParentQualifiedNameAndExtractors() {
        LOG.debug(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName and extractQualifiedNameForQualifier methods for QualifiedName: Start");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Creating QualifiedName <qn> Start");
        QualifiedName qn = new QualifiedName();
        LOG.trace(".getParentQualifiedNameAndExtractors(): Creating QualifiedName <qn> Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Appending <qn> with UnqualifiedNames Start");
        qn.appendUnqualifiedName(new UnqualifiedName("A","a"));
        qn.appendUnqualifiedName(new UnqualifiedName("B","b"));
        qn.appendUnqualifiedName(new UnqualifiedName("C","c"));
        LOG.trace(".getParentQualifiedNameAndExtractors(): Appending <qn> with UnqualifiedNames Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): QualifiedName <qn> --> {}", qn);

        // Parent should contain first two entries
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Start");
        QualifiedName parent = qn.getParentQualifiedName();
        LOG.trace(".getParentQualifiedNameAndExtractors(): Parent QualifiedName <qn.getParentQualifiedName()> --> {}", parent);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting not null: Start");
        assertNotNull(parent);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting not null: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting correct size: Start");
        assertEquals(2, parent.getRelativeDNCount());
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting correct size: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting correct values: Start");
        assertEquals("B", parent.getUnqualifiedName().getQualifier());
        assertEquals("b", parent.getUnqualifiedName().getValue());
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Asserting correct values: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedName(): Finish");

        // extractQualifiedNameForQualifier should return inclusive up to match
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Start");
        QualifiedName uptoB = qn.extractQualifiedNameForQualifier("B");
        LOG.trace(".getParentQualifiedNameAndExtractors(): extractQualifiedNameForQualifier(B) --> {}", uptoB);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting not null: Start");
        assertNotNull(uptoB);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting not null: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting correct size: Start");
        assertEquals(2, uptoB.getRelativeDNCount());
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting correct size: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting correct values: Start");
        assertEquals("B", uptoB.getUnqualifiedName().getQualifier());
        assertEquals("b", uptoB.getUnqualifiedName().getValue());
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Asserting correct values: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractQualifiedNameForQualifier(): Finish");

        // extractUnqualifiedNameWithQualifier should find exact match
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier(): Start");
        UnqualifiedName found = qn.extractUnqualifiedNameWithQualifier("C");
        LOG.trace(".getParentQualifiedNameAndExtractors(): extractUnqualifiedNameWithQualifier(C) --> {}", found);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier(): Asserting not null: Start");
        assertNotNull(found);
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier(): Asserting not null: Finish");
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier(): Asserting correct values: Start");
        assertEquals("c", found.getValue());
        assertEquals("C", found.getQualifier());
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier(): Asserting correct values: Finish");

        // Non-existent qualifier should return null
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier() that should fail: Start");
        assertNull(qn.extractUnqualifiedNameWithQualifier("Z"));
        LOG.trace(".getParentQualifiedNameAndExtractors(): Testing extractUnqualifiedNameWithQualifier() that should fail: Finish");
        LOG.debug(".getParentQualifiedNameAndExtractors(): Testing getParentQualifiedNameAndExtractors(): Finish");
    }

    @Test
    void appendQualifiedNameAndCommonName() {
        QualifiedName left = new QualifiedName();
        left.appendUnqualifiedName(new UnqualifiedName("L","1"));

        QualifiedName right = new QualifiedName();
        right.appendUnqualifiedName(new UnqualifiedName("R","2"));
        right.appendUnqualifiedName(new UnqualifiedName("S","3"));

        left.appendQualifiedName(right);
        assertEquals(3, left.getRelativeDNCount());
        assertNotNull(left.getCommonName());
        assertNotNull(left.getCommonNameValue());
        assertTrue(left.toString().contains("QualifiedName"));
    }
}
