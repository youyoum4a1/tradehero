package com.androidth.general.api.live1b;


import java.util.List;

public class PositionsResponseDTO  extends BaseMessageResponseDTO {

    public List<PositionDto> Positions;
    public Boolean IsFullReport;


    @Override
    public String toString() {
        return "PositionsResponseDTO{" +
                "Positions=" + Positions +
                ", IsFullReport=" + IsFullReport +
                '}';
    }
}
