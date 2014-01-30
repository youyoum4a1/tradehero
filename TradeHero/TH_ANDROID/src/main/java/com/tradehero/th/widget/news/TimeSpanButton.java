package com.tradehero.th.widget.news;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.R;
import com.tradehero.th.models.chart.yahoo.YahooTimeSpan;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 5:40 PM To change this template use File | Settings | File Templates. */
public class TimeSpanButton extends Button
{
    public static final String TAG = TimeSpanButton.class.getSimpleName();
    public static final float ALPHA_DISABLED = 0.5f;

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

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeSpanButton);
        setTimeSpan(a.getString(R.styleable.TimeSpanButton_timeSpan));
        a.recycle();
    }

    //<editor-fold desc="Accessors">
    public YahooTimeSpan getTimeSpan()
    {
        String timeSpanString = (String) getTag(R.string.key_time_span);
        if (timeSpanString == null)
        {
            return null;
        }
        return YahooTimeSpan.valueOf(timeSpanString);
    }

    public void setTimeSpan(String timeSpan)
    {
        setTag(R.string.key_time_span, timeSpan);
    }

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : ALPHA_DISABLED);
    }
    //</editor-fold>

}
