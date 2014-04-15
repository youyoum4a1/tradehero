package com.tradehero.th.ui;

import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 10:57 AM Copyright (c) TradeHero
 */
public interface ViewWrapper
{
    ViewGroup get(ViewGroup viewGroup);

    public static ViewWrapper DEFAULT = new ViewWrapper()
    {
        @Override public ViewGroup get(ViewGroup viewGroup)
        {
            return viewGroup;
        }
    };
}
