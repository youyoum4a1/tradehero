package com.tradehero.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

public class OnlineStateReceiver extends BroadcastReceiver
{
    public static final String ONLINE_STATE_CHANGED = "com.tradehero.th.network.ALERT";

    @Nullable private static Boolean online = null;

    public static boolean isOnline(@NonNull Context context)
    {
        if (online == null)
        {
            online = isConnected(context);
        }
        return online;
    }

    @Override public void onReceive(@NonNull Context context, @NonNull Intent intent)
    {
        online = isConnected(context);

        Intent online = new Intent(ONLINE_STATE_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(online);
    }

    private static boolean isConnected(@NonNull Context context)
    {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try
        {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e)
        {
            Timber.d(e.getMessage());
        }
        return connected;
    }

    public static boolean isWiFiConnected(@NonNull Context context)
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

    public static boolean isMobileNetworkConnected(@NonNull Context context)
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
