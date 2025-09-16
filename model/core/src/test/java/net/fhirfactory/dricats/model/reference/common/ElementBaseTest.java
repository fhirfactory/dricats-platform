package net.fhirfactory.dricats.model.reference.common;

import net.fhirfactory.dricats.model.reference.common.valuesets.ElementTypeEnum;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ElementBaseTest {

    static class MyElement extends ElementBase { }

    @Test
    void gettersSettersAndEquality() {
        MyElement a = new MyElement();
        MyElement b = new MyElement();

        a.setName("n");
        a.setDocumentation("d");
        a.setSpecialization("s");
        a.setElementType(ElementTypeEnum.APPLICATION_COMPONENT);
        HashMap<String,String> props = new HashMap<>();
        props.put("k","v");
        a.setProperties(props);

        assertEquals("n", a.getName());
        assertEquals("d", a.getDocumentation());
        assertEquals("s", a.getSpecialization());
        assertEquals(ElementTypeEnum.APPLICATION_COMPONENT, a.getElementType());
        assertEquals("v", a.getProperties().get("k"));

        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());

        String s = a.toString();
        assertNotNull(s);
        assertTrue(s.contains("MyElement"));
    }
}
