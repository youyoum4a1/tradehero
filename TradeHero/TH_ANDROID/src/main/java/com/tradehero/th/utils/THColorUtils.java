package com.tradehero.th.utils;

import android.support.annotation.ColorRes;
import com.tradehero.common.application.PApplication;
import com.tradehero.th.R;

public class THColorUtils
{
    public static final int MAX_RED_VALUE = 255;
    public static final int MAX_GREEN_VALUE = 200;

    public static int getProperColorForNumber(float percentage)
    {
        return PApplication.context().getResources().getColor(getColorResourceIdForNumber(percentage));
    }

    public static int getColorResourceIdForNumber(double n)
    {
        return getColorResourceIdForNumber(n, R.color.black);
    }

    public static int getColorResourceIdForNumber(double n, @ColorRes int defaultColorResId)
    {
        return n < 0 ? R.color.number_down : n > 0 ? R.color.number_up : defaultColorResId;
    }
}
