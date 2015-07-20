package com.tradehero.common.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class PApplication extends MultiDexApplication
{
    private static Context context;

    @Override public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        init();
    }

    protected void init()
    {
    }

    public static PApplication context()
    {
        return (PApplication) context;
    }

    public static String getResourceString(int resourceId)
    {
        return context().getResources().getString(resourceId);
    }
}
