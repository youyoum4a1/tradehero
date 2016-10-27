package com.androidth.general.api.live1b;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class PositionsResponseDTO extends RealmObject {

    public String RequestId;
    public String RequestCompletedAtUtcTicks;
    public Double ErrorCode;
    public String Description;

    public RealmList<LivePositionDTO> Positions;
    public Boolean IsFullReport;

    @Override
    public String toString() {
        return "PositionsResponseDTO{" +
                "Positions=" + Positions +
                ", IsFullReport=" + IsFullReport +
                '}';
    }
}
