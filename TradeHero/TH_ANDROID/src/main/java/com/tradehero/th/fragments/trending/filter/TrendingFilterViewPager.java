package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TrendingFilterViewPager extends ViewPager
{
    //<editor-fold desc="Constructors">
    public TrendingFilterViewPager(Context context)
    {
        super(context);
        //THLog.d(TAG, "Constructor context");
    }

    public TrendingFilterViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //THLog.d(TAG, "Constructor context, attrs");
    }
    //</editor-fold>

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        //THLog.d(TAG, "onAttachedToWindow");
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);
        //THLog.d(TAG, "onLayout");
    }

    @Override protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //THLog.d(TAG, "onDraw");
    }

    @Override protected void onDetachedFromWindow()
    {
        //THLog.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }
}
