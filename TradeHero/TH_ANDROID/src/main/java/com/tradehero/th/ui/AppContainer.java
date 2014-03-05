package com.tradehero.th.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 10:42 AM Copyright (c) TradeHero
 */
public interface AppContainer
{
    ViewGroup get(Activity activity);

    public static AppContainer DEFAULT = new AppContainer()
    {
        @Override public ViewGroup get(Activity activity)
        {
            return (ViewGroup) activity.findViewById(android.R.id.content);
        }
    };
}
