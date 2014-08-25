package com.tradehero.th.fragments.onboarding;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OnBoardViewPager extends ViewPager
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public OnBoardViewPager(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public OnBoardViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0)
    {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override public boolean onTouchEvent(MotionEvent event)
    {
        // Never allow swiping to switch between pages
        return false;
    }
}
