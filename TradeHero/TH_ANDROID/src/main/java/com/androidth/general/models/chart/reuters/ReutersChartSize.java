package com.androidth.general.models.chart.reuters;

import android.support.annotation.NonNull;
import com.androidth.general.models.chart.ChartSize;

public enum ReutersChartSize
{
    small("s", 350, 205),
    medium("m", 512, 288),
    large("l", 800, 355);

    @NonNull public final String code;
    public final int reutersPixelWidth;
    public final int reutersPixelHeight;

    //<editor-fold desc="Constructors">
    private ReutersChartSize(@NonNull String c, int reutersPixelWidth, int reutersPixelHeight)
    {
        code = c;
        this.reutersPixelWidth = reutersPixelWidth;
        this.reutersPixelHeight = reutersPixelHeight;
    }
    //</editor-fold>

    @NonNull public ChartSize getChartSize()
    {
        return new ChartSize(reutersPixelWidth, reutersPixelHeight);
    }

    @NonNull public String toString()
    {
        return code;
    }

    @NonNull public static ReutersChartSize getPreferredSize(int pixelWidth, int pixelHeight)
    {
        // TODO refine
        if (pixelWidth >= large.reutersPixelWidth && pixelHeight >= large.reutersPixelHeight)
        {
            return large;
        }
        if (pixelWidth >= medium.reutersPixelWidth && pixelHeight >= medium.reutersPixelHeight)
        {
            return medium;
        }
        return small;
    }
}