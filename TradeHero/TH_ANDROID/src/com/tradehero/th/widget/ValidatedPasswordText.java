package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 8/27/13 Time: 10:30 AM Copyright (c) TradeHero */
public class ValidatedPasswordText extends SelfValidatedText
{
    public ValidatedPasswordText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override public void onFocusChange(View view, boolean b)
    {
        super.onFocusChange(view, b);
    }
}
