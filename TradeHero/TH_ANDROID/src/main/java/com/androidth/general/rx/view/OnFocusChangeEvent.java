package com.androidth.general.rx.view;

import android.view.View;

public class OnFocusChangeEvent
{
    public final View view;
    public final boolean hasFocus;

    public static OnFocusChangeEvent create(View input, boolean hasFocus)
    {
        return new OnFocusChangeEvent(input, hasFocus);
    }

    public OnFocusChangeEvent(View input, boolean hasFocus)
    {
        if (input == null)
        {
            throw new NullPointerException("Null view");
        }
        else
        {
            this.view = input;
            this.hasFocus = hasFocus;
        }
    }
}
