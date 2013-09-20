/**
 * TrendUtils.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Aug 10, 2013
 */
package com.tradehero.th.utills;

import android.graphics.Color;

@Deprecated public class TrendUtils
{

    public static int colorForPercentage(int percentage)
    {

        final int grayValue = 140;
        final int maxGreenValue = 200;
        final int maxRedValue = 255;

        int redValue = 0;
        int greenValue = 0;
        int blueValue = 0;
        float pct = CLAMP(Math.abs(percentage) / 100, 0, 1);
        if (percentage > 0)
        {
            greenValue = (int) (50 + (pct * (maxGreenValue - 50)));
            blueValue = (int) (greenValue * .3f);
            redValue = (int) (greenValue * .3f);
        }
        else if (percentage < 0)
        {
            redValue = (int) (50 + (pct * (maxRedValue - 50)));
            blueValue = (int) (redValue * .2f);
        }
        else
        {
            redValue = grayValue;
            greenValue = grayValue;
            blueValue = grayValue;
        }

        return Color.rgb(redValue, greenValue, blueValue);
    }

    private static float CLAMP(float n, int min, int max)
    {
        return ((n < min) ? min : (n > max) ? max : n);
    }
}
