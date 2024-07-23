package net.fhirfactory.dricats.internals.communicate.entities.rooms.datatypes;

import net.fhirfactory.dricats.internals.communicate.entities.rooms.valuesets.CommunicateRoomStatusEnum;
import net.fhirfactory.dricats.internals.model.base.dataytypes.EffectiveDate;

public class CommunicateRoomStatus {
    CommunicateRoomStatusEnum statusValue;
    EffectiveDate statusPeriod;

    public CommunicateRoomStatusEnum getStatusValue() {
        return statusValue;
    }

    public void setStatusValue(CommunicateRoomStatusEnum statusValue) {
        this.statusValue = statusValue;
    }

    public EffectiveDate getStatusPeriod() {
        return statusPeriod;
    }

    public void setStatusPeriod(EffectiveDate statusPeriod) {
        this.statusPeriod = statusPeriod;
    }

    @Override
    public String toString() {
        return "CommunicateRoomStatus{" +
                "statusValue=" + statusValue +
                ", statusPeriod=" + statusPeriod +
                '}';
    }
}
