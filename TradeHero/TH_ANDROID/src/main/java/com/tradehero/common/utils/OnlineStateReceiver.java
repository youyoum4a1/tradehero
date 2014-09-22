package com.tradehero.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class OnlineStateReceiver extends BroadcastReceiver
{
    @Nullable private static Boolean online = null;

    public static boolean isOnline(@NotNull Context context)
    {
        if (online == null)
        {
            online = isConnected(context);
        }
        return online;
    }

    @Override public void onReceive(@NotNull Context context, @NotNull Intent intent)
    {
        online = isConnected(context);
    }

    private static boolean isConnected(@NotNull Context context)
    {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try
        {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        }
        catch (Exception e)
        {
            Timber.d(e.getMessage());
        }
        return connected;
    }

    public static boolean isWiFiConnected(@NotNull Context context)
    {
        boolean haveConnectedWifi = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getType() == ConnectivityManager.TYPE_WIFI
                && ni.isConnected())
            {
                haveConnectedWifi = true;
            }
        }
        return haveConnectedWifi;
    }

    public static boolean isMobileNetworkConnected(@NotNull Context context)
    {
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getType() == ConnectivityManager.TYPE_MOBILE
                && ni.isConnected())
            {
                haveConnectedMobile = true;
            }
        }
        return haveConnectedMobile;
    }
}
