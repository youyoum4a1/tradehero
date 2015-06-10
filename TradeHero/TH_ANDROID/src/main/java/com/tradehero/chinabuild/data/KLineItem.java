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
    public Long volume;
    public String time;

    public String toString() {
        return String.format("time[%s], open[%f], close[%f], high[%f], low[%f], preClose[%f], volume[%d]",
                time, open, close, high, low, preclose, volume);
    }
}
