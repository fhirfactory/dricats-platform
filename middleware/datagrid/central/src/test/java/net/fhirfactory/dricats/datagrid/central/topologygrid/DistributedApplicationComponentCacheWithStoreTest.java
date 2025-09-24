package net.fhirfactory.dricats.datagrid.central.topologygrid;

import net.fhirfactory.dricats.internals.common.DistributableObjectId;
import net.fhirfactory.dricats.internals.common.naming.CommonName;
import net.fhirfactory.dricats.internals.common.naming.QualifiedName;
import net.fhirfactory.dricats.internals.common.naming.UnqualifiedName;
import net.fhirfactory.dricats.internals.oam.topology.ApplicationComponentSummary;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DistributedApplicationComponentCacheWithStoreTest {

    static class TestableCache extends DistributedApplicationComponentCacheWithStore {
        @Override
        protected Optional<InMemoryApplicationComponentSummaryStore> repository() {
            // Not used in these unit tests
            return Optional.empty();
        }
    }

    private static ApplicationComponentSummary makeSummaryWithId(String idValue) {
        ApplicationComponentSummary s = new ApplicationComponentSummary();
        // set objectID with id common name value
        QualifiedName qn = new QualifiedName();
        qn.appendUnqualifiedName(new UnqualifiedName("Object", idValue));
        DistributableObjectId doi = new DistributableObjectId(qn);
        // ensure id matches expected
        doi.setId(new CommonName(idValue));
        s.setObjectID(doi);
        s.setName("comp-" + idValue);
        return s;
    }

    @Test
    void resolveKey_prefersObjectIdThenElementIdThenGenerates() {
        TestableCache tc = new TestableCache();

        // 1) With objectId id value
        ApplicationComponentSummary a = makeSummaryWithId("RID-1");
        String k1 = tc.resolveKey(a);
        assertEquals("RID-1", k1);

        // 2) Without objectId id but with element id
        ApplicationComponentSummary b = new ApplicationComponentSummary();
        b.setId(new CommonName("EL-1"));
        // Force objectId.id to be null so resolveKey falls back to element id
        if (b.getObjectID() != null) {
            b.getObjectID().setId(null);
        }
        String k2 = tc.resolveKey(b);
        assertEquals("EL-1", k2);

        // 3) With neither -> generates and sets element id
        ApplicationComponentSummary c = new ApplicationComponentSummary();
        String k3 = tc.resolveKey(c);
        assertNotNull(k3);
        assertEquals(k3, Optional.ofNullable(c.getId()).map(CommonName::getValue).orElse(null));
    }
}
