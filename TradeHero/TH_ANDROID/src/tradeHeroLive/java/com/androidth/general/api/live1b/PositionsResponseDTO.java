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
        String positionListString = "";

        for(LivePositionDTO pos : Positions)
        {
            positionListString = positionListString + ",LivePositionDTO{Product=" + pos.Product.toString() + ", OrderId=" + pos.OrderId + "}";
        }
        return "PositionsResponseDTO{" +
                "Positions=" + positionListString +
                ", IsFullReport=" + IsFullReport +
                '}';
    }
}
