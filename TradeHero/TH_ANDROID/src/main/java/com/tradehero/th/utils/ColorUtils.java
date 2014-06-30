package com.tradehero.th.utils;

import com.tradehero.common.application.PApplication;
import com.tradehero.thm.R;

public class ColorUtils
{
    public static final int MAX_RED_VALUE = 255;
    public static final int MAX_GREEN_VALUE = 200;

    public static int getProperColorForNumber(float percentage)
    {
        return PApplication.context().getResources().getColor(getColorResourceIdForNumber(percentage));
    }

    public static int getColorResourceIdForNumber(double n)
    {
        return n < 0 ? R.color.number_down : n > 0 ? R.color.number_up : R.color.black;
    }
}
