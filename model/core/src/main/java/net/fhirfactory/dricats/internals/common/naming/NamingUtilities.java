package net.fhirfactory.dricats.internals.common.naming;

import net.fhirfactory.dricats.internals.common.id.ObjectKey;
import net.fhirfactory.dricats.internals.common.naming.datatypes.DistinguishedNameEntry;

import java.util.Map;

public class NamingUtilities {

    public static ObjectKey toObjectToken(FullyDistinguishedName fdn){
        if (fdn.getUnqualifiedNameEntries() == null) {
            throw new IllegalArgumentException("qualifiedName is null");
        }
        StringBuilder builder = new StringBuilder();
        Map<Integer, DistinguishedNameEntry> entries = fdn.getUnqualifiedNameEntries();
        for (int i = 0; i < entries.size(); i++) {
            RelativeDistinguishedName current = entries.get(i);
            String qualifier = current.getQualifier();
            String value = current.getValue();
            builder.append("<").append(i).append(":").append(qualifier).append(">");
            builder.append(value);
            builder.append("</").append(i).append(":").append(qualifier).append(">");
        }
        builder.append("<effectiveDate.effectiveStartDate></effectiveDate.effectiveStartDate>");
        builder.append("<effectiveDate.effectiveEndDate></effectiveDate.effectiveEndDate>");
        ObjectKey objectToken = new ObjectKey(builder.toString());
        return (objectToken);
    }
}
