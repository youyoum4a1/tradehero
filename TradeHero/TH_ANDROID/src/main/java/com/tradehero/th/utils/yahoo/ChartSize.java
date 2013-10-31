package com.tradehero.th.utils.yahoo;

/**
 * Created by julien on 9/10/13
 */

public enum ChartSize
{
    small("s", 350, 205),
    medium("m", 512, 288),
    large("l", 800, 355);

    public final String code;
    public final int yahooPixelWidth;
    public final int yahooPixelHeight;

    private ChartSize(String c, int yahooPixelWidth, int yahooPixelHeight)
    {
        code = c;
        this.yahooPixelWidth = yahooPixelWidth;
        this.yahooPixelHeight = yahooPixelHeight;
    }

    public boolean equalsCode(String otherCode)
    {
        return (otherCode != null) && otherCode.equals(code);
    }

    public String toString()
    {
        return code;
    }
}