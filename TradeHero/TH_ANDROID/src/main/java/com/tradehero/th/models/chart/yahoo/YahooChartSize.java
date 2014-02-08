package com.tradehero.th.models.chart.yahoo;

import com.tradehero.th.models.chart.ChartSize;

/**
 * Created by julien on 9/10/13
 */

public enum YahooChartSize
{
    small("s", 350, 205),
    medium("m", 512, 288),
    large("l", 800, 355);

    public final String code;
    public final int yahooPixelWidth;
    public final int yahooPixelHeight;

    private YahooChartSize(String c, int yahooPixelWidth, int yahooPixelHeight)
    {
        code = c;
        this.yahooPixelWidth = yahooPixelWidth;
        this.yahooPixelHeight = yahooPixelHeight;
    }

    public boolean equalsCode(String otherCode)
    {
        return (otherCode != null) && otherCode.equals(code);
    }

    public ChartSize getChartSize()
    {
        return new ChartSize(yahooPixelWidth, yahooPixelHeight);
    }

    public String toString()
    {
        return code;
    }

    public static YahooChartSize getPreferredSize(int pixelWidth, int pixelHeight)
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