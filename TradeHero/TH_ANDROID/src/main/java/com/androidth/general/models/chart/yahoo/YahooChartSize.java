package com.androidth.general.models.chart.yahoo;

import android.support.annotation.NonNull;
import com.androidth.general.models.chart.ChartSize;

public enum YahooChartSize
{
    small("s", 350, 205),
    medium("m", 512, 288),
    large("l", 800, 355);

    @NonNull public final String code;
    public final int yahooPixelWidth;
    public final int yahooPixelHeight;

    //<editor-fold desc="Constructors">
    private YahooChartSize(@NonNull String c, int yahooPixelWidth, int yahooPixelHeight)
    {
        code = c;
        this.yahooPixelWidth = yahooPixelWidth;
        this.yahooPixelHeight = yahooPixelHeight;
    }
    //</editor-fold>

    @NonNull public ChartSize getChartSize()
    {
        return new ChartSize(yahooPixelWidth, yahooPixelHeight);
    }

    @NonNull public String toString()
    {
        return code;
    }

    @NonNull public static YahooChartSize getPreferredSize(int pixelWidth, int pixelHeight)
    {
        // TODO refine
        if (pixelWidth >= large.yahooPixelWidth && pixelHeight >= large.yahooPixelHeight)
        {
            return large;
        }
        if (pixelWidth >= medium.yahooPixelWidth && pixelHeight >= medium.yahooPixelHeight)
        {
            return medium;
        }
        return small;
    }
}