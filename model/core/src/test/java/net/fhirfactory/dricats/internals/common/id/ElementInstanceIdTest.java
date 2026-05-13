package net.fhirfactory.dricats.internals.common.id;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

public class ElementInstanceIdTest {
    private static final Logger LOG = LoggerFactory.getLogger(ElementInstanceIdTest.class);

    @Test
    public void testFromHexId() {
        LOG.info(".testFromHexId(): Entry");
        ElementInstanceId key = new ElementInstanceId();
        key.setUpperBits(0x1234567890abcdefL);
        key.setLowerBits(0xfedcba0987654321L);
        key.setVersion(0x0000000000000001L);

        String hexId = "1234567890abcdeffedcba09876543210000000000000001";
        LOG.info(".testFromHexId(): hexId length -> {}", hexId.length());
        assertEquals(48, hexId.length());

        ElementInstanceId newKey = new ElementInstanceId();
        newKey.fromHexId(hexId);

        assertEquals(key.getUpperBits(), newKey.getUpperBits());
        assertEquals(key.getLowerBits(), newKey.getLowerBits());
        assertEquals(key.getVersion(), newKey.getVersion());
        assertEquals(key, newKey);
        LOG.info(".testFromHexId(): Exit");
    }

    @Test
    public void testToHexId() {
        ElementInstanceId key = new ElementInstanceId();
        key.setUpperBits(0x1234567890abcdefL);
        key.setLowerBits(0xfedcba0987654321L);
        key.setVersion(0x0000000000000001L);

        String hexId = key.toHexId();
        // toHexId no longer uses colons
        assertEquals("1234567890abcdeffedcba09876543210000000000000001", hexId);
    }

    @Test
    public void testConstructorLongId() {
        String hexId = "1234567890abcdeffedcba09876543210000000000000001";

        ElementInstanceId key = new ElementInstanceId(hexId);

        assertEquals(0x1234567890abcdefL, key.getUpperBits());
        assertEquals(0xfedcba0987654321L, key.getLowerBits());
        assertEquals(0x0000000000000001L, key.getVersion());
    }

    @Test
    public void testNonHexId() {
        String nonHexId = "<Subsystem>SubsystemOne</Subsystem>";
        ElementInstanceId key = new ElementInstanceId(nonHexId);
        assertEquals(nonHexId, key.getIdValue());
        // Should not throw exception
    }
}
