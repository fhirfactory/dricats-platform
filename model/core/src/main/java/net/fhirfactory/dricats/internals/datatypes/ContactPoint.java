package net.fhirfactory.dricats.internals.datatypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * ContactPoint aligns with the HL7 FHIR ContactPoint datatype.
 * Fields:
 * - system: The type of contact point (e.g., phone, fax, email, pager, url, sms, other)
 * - value: The actual contact point details
 * - use: The purpose of this contact (e.g., home, work, temp, old, mobile)
 * - rank: Preferred order of use (1 = highest)
 * - period: When this contact point is/was valid
 */
public class ContactPoint implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String system;
    private String value;
    private String use;
    private Integer rank;
    private EffectiveDate period;

    public ContactPoint() {
        super();
        this.period = new EffectiveDate();
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public EffectiveDate getPeriod() {
        return period;
    }

    public void setPeriod(EffectiveDate period) {
        this.period = period;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContactPoint.class.getSimpleName() + "[", "]")
                .add("system='" + getSystem() + "'")
                .add("value='" + getValue() + "'")
                .add("use='" + getUse() + "'")
                .add("rank=" + getRank())
                .add("period=" + getPeriod())
                .toString();
    }
}
