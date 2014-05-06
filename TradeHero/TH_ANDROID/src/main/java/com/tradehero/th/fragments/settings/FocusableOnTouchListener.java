package com.tradehero.th.fragments.settings;

import android.view.MotionEvent;
import android.view.View;

// HACK: force this email to focus instead of the TabHost stealing focus..
// http://stackoverflow.com/questions/15669152/android-tabhost-tabs-steal-focus-when-using-hardware-keyboard

public class FocusableOnTouchListener implements View.OnTouchListener
{
    @Override public boolean onTouch(View view, MotionEvent motionEvent)
    {
        view.requestFocusFromTouch();
        return false;
    }
}
