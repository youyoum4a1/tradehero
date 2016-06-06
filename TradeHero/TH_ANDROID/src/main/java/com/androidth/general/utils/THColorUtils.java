package com.androidth.general.utils;

import android.support.annotation.ColorRes;
import com.androidth.general.R;
import com.androidth.general.base.THApp;

public class THColorUtils
{
    public static final int MAX_RED_VALUE = 255;
    public static final int MAX_GREEN_VALUE = 200;

    public static int getProperColorForNumber(float percentage)
    {
        return THApp.context().getResources().getColor(getColorResourceIdForNumber(percentage));
    }

    @ColorRes public static int getColorResourceIdForNumber(double n)
    {
        return getColorResourceIdForNumber(n, R.color.black);
    }

    @ColorRes public static int getColorResourceIdForNumber(double n, @ColorRes int defaultColorResId)
    {
        return n < 0 ? R.color.number_down : n > 0 ? R.color.number_up : defaultColorResId;
    }
}
