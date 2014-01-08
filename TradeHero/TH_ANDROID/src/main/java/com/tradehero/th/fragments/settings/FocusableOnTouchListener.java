package com.tradehero.th.fragments.settings;

import android.view.MotionEvent;
import android.view.View;

// HACK: force this email to focus instead of the TabHost stealing focus..
// http://stackoverflow.com/questions/15669152/android-tabhost-tabs-steal-focus-when-using-hardware-keyboard

/** Created with IntelliJ IDEA. User: admin Date: 10/24/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public class FocusableOnTouchListener implements View.OnTouchListener
{
    @Override public boolean onTouch(View view, MotionEvent motionEvent)
    {
        view.requestFocusFromTouch();
        return false;
    }
}
