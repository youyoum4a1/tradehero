package com.tradehero.th.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.tradehero.common.utils.THLog;

public final class NetworkUtils
{
    private static final String TAG = NetworkUtils.class.getName();

    public static boolean isConnected(Context con)
    {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try
        {
            connectivityManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        }
        catch (Exception e)
        {
            THLog.d(TAG, e.getMessage());
        }
        return connected;
    }

    public static boolean isWiFiConnected(Context context)
    {
        boolean haveConnectedWifi = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
            {
                if (ni.isConnected())
                {
                    haveConnectedWifi = true;
                }
            }
        }
        return haveConnectedWifi;
    }

    public static boolean isMobileNetworkConnected(Context context)
    {
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo)
        {
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
            {
                if (ni.isConnected())
                {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedMobile;
    }
}
