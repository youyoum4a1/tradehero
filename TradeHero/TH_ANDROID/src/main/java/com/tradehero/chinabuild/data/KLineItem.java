package com.tradehero.chinabuild.data;

/**
 * Created by liangyx on 6/9/15.
 */
public class KLineItem {
    public Double open;
    public Double close;
    public Double high;
    public Double low;
    public Double preclose;
    public Long vol;
    public String time;

    public String toString() {
        return String.format("time[%s], open[%f], close[%f], high[%f], low[%f], preClose[%f], volume[%d]",
                time, open, close, high, low, preclose, vol);
    }

    public String getDate()
    {
        return time;
    }

    public Double getOpen()
    {
        return open;
    }

    public Double getClose()
    {
        return close;
    }

    public Double getHigh()
    {
        return high;
    }

    public Double getLow()
    {
        return low;
    }

    public Double getPreclose()
    {
        return preclose;
    }

    public Long getVol()
    {
        return vol;
    }
}
