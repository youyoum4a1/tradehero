package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;

public class THTabView extends LinearLayout
{
    @InjectView(android.R.id.icon) ImageView icon;
    @InjectView(android.R.id.title) TextView titleView;
    private String title;

    public THTabView(Context context)
    {
        super(context);
    }

    public THTabView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        ButterKnife.inject(this);
    }


    public void setIcon(int drawableResId)
    {
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(drawableResId);
    }

    public void setTitle(int textResId)
    {
        titleView.setText(getResources().getString(textResId));
    }

    public void setTitle(String title)
    {
        this.title = title;
        titleView.setText(title);
    }

    public void setNumber(int number)
    {
        if(number > 0)
        {
            String titleWithNumber = title + " (" + number + ")";
            titleView.setText(titleWithNumber);
        }
        else
        {
            titleView.setText(title);
        }
    }

    public static THTabView inflateWith(TabWidget container)
    {
        return (THTabView) LayoutInflater.from(container.getContext())
                .inflate(R.layout.th_tab_indicator, container, false);
    }
}
