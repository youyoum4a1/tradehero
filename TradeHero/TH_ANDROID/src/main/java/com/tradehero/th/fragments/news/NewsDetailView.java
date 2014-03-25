package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/24/14 Time: 5:36 PM Copyright (c) TradeHero
 */
public class NewsDetailView extends LinearLayout
{
    //<editor-fold desc="Constructors">
    public NewsDetailView(Context context)
    {
        super(context);
    }

    public NewsDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsDetailView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        //DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }
}
