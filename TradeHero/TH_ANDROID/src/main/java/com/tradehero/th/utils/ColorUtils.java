package com.tradehero.th.utils;

import android.graphics.Color;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 3:24 PM To change this template use File | Settings | File Templates. */
public class ColorUtils
{
    public static final int MAX_RED_VALUE = 255;
    public static final int MAX_GREEN_VALUE = 200;

    /**
     *
     * @param percentage, a value from -1 to 1
     * @return
     */
    public static int getColorForPercentage(float percentage)
    {
        return Color.rgb(
                (int) (MAX_RED_VALUE * Math.abs(MathUtils.clamp(percentage, -1, 0))),
                (int) (MAX_GREEN_VALUE * MathUtils.clamp(percentage, 0, 1)),
                0);
    }


}
