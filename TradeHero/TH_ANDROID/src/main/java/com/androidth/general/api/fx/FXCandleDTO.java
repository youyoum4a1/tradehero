package com.androidth.general.api.fx;

import com.androidth.general.common.persistence.DTO;

public class FXCandleDTO implements DTO
{
    public String time;
    public float openMid;
    public float highMid;
    public float lowMid;
    public float closeMid;
    public int volume;
    public boolean complete;

    public float getOpen() {
        return openMid;
    }

    public float getHigh() {
        return highMid;
    }

    public float getLow() {
        return lowMid;
    }

    public float getClose() {
        return closeMid;
    }

    public String getDate() {
        return time;
    }

    @Override
    public String toString() {
        return "FXCandleDTO{" +
                "time='" + time + '\'' +
                ", openMid=" + openMid +
                ", highMid=" + highMid +
                ", lowMid=" + lowMid +
                ", closeMid=" + closeMid +
                '}';
    }
}
