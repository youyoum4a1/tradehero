package com.tradehero.th.fragments.watchlist;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.widget.TwoStateView;
import com.tradehero.th.R;

public class WatchlistHeaderItem extends TwoStateView
{
    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.value) protected TextView value;
    private String firstTitle;
    private String secondTitle;
    private String firstValue;
    private String secondValue;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public WatchlistHeaderItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public WatchlistHeaderItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        init();
    }

    private void init()
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                syncTextState();
            }
        });
    }

    public void setTitle(@StringRes int title)
    {
        setTitle(getContext().getString(title));
    }

    public void setTitle(String text)
    {
        setFirstTitle(text);
        setSecondTitle(text);
    }

    public void setFirstTitle(@StringRes int firstTitle)
    {
        setFirstTitle(getContext().getString(firstTitle));
    }

    public void setFirstTitle(String firstTitle)
    {
        this.firstTitle = firstTitle;
    }

    public void setSecondTitle(@StringRes int secondTitle)
    {
        setSecondValue(getContext().getString(secondTitle));
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

    public void setFirstValue(String firstValue)
    {
        this.firstValue = firstValue;
    }

    public void setSecondValue(String secondValue)
    {
        this.secondValue = secondValue;
    }
}
