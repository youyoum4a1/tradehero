package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.tradehero.th.R;
import com.tradehero.th.utils.ColorUtils;


public class ColorIndicator extends RelativeLayout
{
    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    //<editor-fold desc="Constructors">
    public ColorIndicator(Context context)
    {
        super(context);
    }

    public ColorIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ColorIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public void linkWith(Double percentage)
    {
        if (percentage == null || percentage == 0.0)
        {
            setBackgroundColor(getResources().getColor(R.color.gray_2));
        }
        else
        {
            setBackgroundColor(
                    ColorUtils.getColorForPercentage((float) percentage.doubleValue() * PERCENT_STRETCHING_FOR_COLOR));
        }
    }
}
