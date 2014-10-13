package com.tradehero.common.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeableViewPager extends ViewPager
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public NonSwipeableViewPager(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public NonSwipeableViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return false;
    }

    @Override public void setCurrentItem(int item)
    {
        super.setCurrentItem(item, false);
    }
}
