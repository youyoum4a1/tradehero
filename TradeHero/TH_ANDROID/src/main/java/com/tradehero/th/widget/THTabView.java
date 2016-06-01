package com.ayondo.academy.widget;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.ayondo.academy.R;

public class THTabView extends LinearLayout
{
    @Bind(android.R.id.icon) ImageView icon;
    @Bind(android.R.id.title) TextView titleView;
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
        ButterKnife.bind(this);
    }


    public void setIcon(int drawableResId)
    {
        icon.setVisibility(View.VISIBLE);
        icon.setImageResource(drawableResId);
    }

    public void setTitle(@StringRes int textResId)
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
