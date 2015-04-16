package com.tradehero.common.application;

import android.content.Context;
import com.baidu.frontia.FrontiaApplication;

public class PApplication extends FrontiaApplication
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
