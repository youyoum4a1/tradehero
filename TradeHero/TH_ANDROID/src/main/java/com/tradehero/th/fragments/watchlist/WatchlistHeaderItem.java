package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/14/14 Time: 12:04 PM Copyright (c) TradeHero
 */
public class WatchlistHeaderItem extends TwoStateView
{
    private TextView title;
    private TextView value;
    private String firstTitle;
    private String secondTitle;
    private String firstValue;
    private String secondValue;

    //<editor-fold desc="Constructors">
    public WatchlistHeaderItem(Context context)
    {
        super(context);
        init();
    }

    public WatchlistHeaderItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public WatchlistHeaderItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        title = (TextView) findViewById(R.id.title);
        value = (TextView) findViewById(R.id.value);

        post(new Runnable()
        {
            @Override public void run()
            {
                syncTextState();
            }
        });
    }

    public void setFirstTitle(String firstTitle)
    {
        this.firstTitle = firstTitle;
    }

    public void setSecondTitle(String secondTitle)
    {
        this.secondTitle = secondTitle;
    }

    public void setValue(String text)
    {
        if (value != null)
        {
            value.setText(text);
        }
    }

    @Override public void setChecked(boolean checked)
    {
        super.setChecked(checked);

        syncTextState();
    }

    @Override public void invalidate()
    {
        syncTextState();
        super.invalidate();
    }

    private void syncTextState()
    {
        if (title != null)
        {
            title.setText(isFirstState() ? firstTitle : secondTitle);
        }
        if (value != null)
        {
            value.setText(isFirstState() ? firstValue : secondValue);
        }
    }

    public void setTitle(String text)
    {
        setFirstTitle(text);
        setSecondTitle(text);
    }

    public void setFirstValue(String firstValue)
    {
        this.firstValue = firstValue;
    }

    public void setSecondValue(String secondValue)
    {
        this.secondValue = secondValue;
    }
}
