package net.fhirfactory.dricats.internals.common.object.datatypes;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ObjectType implements Serializable {
    //
    // Housekeeping
    //
    @Serial
    private static final Long serialVersionUID = 1L;

    //
    // Attributes
    //

    private String objectType;
    private String objectSpecialisation;

    //
    // Bean Methods
    //

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectSpecialisation() {
        return objectSpecialisation;
    }

    public void setObjectSpecialisation(String objectSpecialisation) {
        this.objectSpecialisation = objectSpecialisation;
    }

    //
    // Standard Methods
    //

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ObjectType that = (ObjectType) o;
        return Objects.equals(objectType, that.objectType) && Objects.equals(objectSpecialisation, that.objectSpecialisation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectType, objectSpecialisation);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("objectType", getObjectType())
                .append("objectSpecialisation", getObjectSpecialisation())
                .toString();
    }
}
