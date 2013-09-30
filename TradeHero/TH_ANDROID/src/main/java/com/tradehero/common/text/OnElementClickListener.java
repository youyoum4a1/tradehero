package com.tradehero.common.text;

import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 9/19/13 Time: 7:34 PM Copyright (c) TradeHero */
public interface OnElementClickListener
{
    void onClick(View textView, String data, String key, String[] matchStrings);
}
