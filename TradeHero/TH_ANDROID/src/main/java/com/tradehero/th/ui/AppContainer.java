package com.tradehero.th.ui;

import android.app.Activity;
import android.view.ViewGroup;


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
