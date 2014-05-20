package com.tradehero.th.utils;

import android.graphics.Color;
import com.tradehero.common.application.PApplication;
import com.tradehero.th.R;
import com.tradehero.th.models.push.DeviceTokenHelper;

public class ColorUtils
{
    public static final int MAX_RED_VALUE = 255;
    public static final int MAX_GREEN_VALUE = 200;

    /**
     * @param percentage, a value from -1 to 1
     * @return
     */
    public static int getColorForPercentage(float percentage)
    {
        if(DeviceTokenHelper.isChineseVersion()/*MetaHelper.isChineseLocale(PApplication.context()*/)
        {
            return PApplication.context().getResources().getColor(getColorResourceForNumber(percentage));
        }
        else
        {
            return Color.rgb(
                    (int) (MAX_RED_VALUE * Math.abs(MathUtils.clamp(percentage, -1, 0))),
                    (int) (MAX_GREEN_VALUE * MathUtils.clamp(percentage, 0, 1)),
                    0);
        }


    }

    public static int getColorResourceForNumber(double n)
    {
        return n < 0 ? R.color.number_down : n > 0 ? R.color.number_up : R.color.black;
    }


}
