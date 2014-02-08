package com.tradehero.th.widget.news;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.models.chart.ChartTimeSpan;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 5:40 PM To change this template use File | Settings | File Templates. */
abstract public class TimeSpanButton extends Button
{
    public static final String TAG = TimeSpanButton.class.getSimpleName();
    public static final float ALPHA_DISABLED = 0.5f;

    protected ChartTimeSpan chartTimeSpan;

    //<editor-fold desc="Constructors">
    public TimeSpanButton(Context context)
    {
        super(context);
    }

    public TimeSpanButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public TimeSpanButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //</editor-fold>

    abstract protected void init(Context context, AttributeSet attrs);

    //<editor-fold desc="Accessors">
    public void setChartTimeSpan(ChartTimeSpan chartTimeSpan)
    {
        this.chartTimeSpan = chartTimeSpan;
    }

    public ChartTimeSpan getTimeSpan()
    {
        return this.chartTimeSpan;
    }

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : ALPHA_DISABLED);
    }
    //</editor-fold>

}
