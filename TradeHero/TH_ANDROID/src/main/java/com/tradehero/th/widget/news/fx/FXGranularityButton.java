package com.tradehero.th.widget.news.fx;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.fx.FXChartGranularity;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.widget.news.TimeSpanButton;

public class FXGranularityButton extends TimeSpanButton
{
    //<editor-fold desc="Constructors">
    public FXGranularityButton(Context context)
    {
        super(context);
    }

    public FXGranularityButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FXGranularityButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeSpanButton);
        FXChartGranularity granularity = FXChartGranularity.valueOf(a.getString(R.styleable.TimeSpanButton_timeSpan));
        setChartTimeSpan(new ChartTimeSpan(granularity.chartTimeSpanDuration));
        a.recycle();
    }
}
