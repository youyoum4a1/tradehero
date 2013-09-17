package com.tradehero.common.application;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

/**
 * Created with IntelliJ IDEA. User: nguyentruongtho.sg@gmail.com Date: 6/29/13 Time: 9:10 PM
 * Copyright @ tradehero All Rights reserved
 */

public class PApplication extends Application
{
    private static final String PREFERENCE_KEY = "th";
    private static final String PERSIST_NAME = "th.persist";
    private static Context context;
    private static Resources res;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Context applicationContext = getApplicationContext();
        context = applicationContext;
        res = applicationContext.getResources();
        init();
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver broadcastReceiver)
    {
        super.unregisterReceiver(broadcastReceiver);
    }

    protected void init()
    {
    }

    public static SharedPreferences getPreferences()
    {
        return context().getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
    }

    public static SharedPreferences getPersistPreferences()
    {
        return context().getSharedPreferences(PERSIST_NAME, 0);
    }

    public static PApplication context()
    {
        return (PApplication) context;
    }

    public static String getResourceString(int resourceId)
    {
        return context().getResources().getString(resourceId);
    }

    public static int getResourceColor(int resourceId)
    {
        return context().getResources().getColor(resourceId);
    }
}
