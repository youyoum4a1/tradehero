package com.tradehero.common.application;

import android.app.Application;
import android.content.Context;

public class PApplication extends Application
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

    public static int getResourceInteger(int resourceId)
    {
        return context().getResources().getInteger(resourceId);
    }
}
