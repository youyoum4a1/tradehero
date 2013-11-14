package com.tradehero.th.utils;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.tradehero.th.utills.Constants;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 4:55 PM To change this template use File | Settings | File Templates. */
public class VersionUtils
{
    public static final String TAG = VersionUtils.class.getSimpleName();

    public static Intent getSupportEmailIntent(ContextWrapper contextWrapper)
    {
        return getSupportEmailIntent(contextWrapper, false);
    }

    public static Intent getSupportEmailIntent(ContextWrapper contextWrapper, boolean longInfo)
    {
        String deviceDetails = "\n\n-----\n" +
                StringUtils.join("\n", getSupportEmailTraceParameters(contextWrapper, longInfo)) +
                "\n-----\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - Support");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    public static List<String> getSupportEmailTraceParameters(ContextWrapper contextWrapper, boolean longInfo)
    {
        List<String> parameters = new ArrayList<>();
        parameters.add("TradeHero: " + getAppVersion(contextWrapper));
        parameters.add("Device Name: " + getDeviceName());
        if (longInfo)
        {
            parameters.add("Brand: " + Build.BRAND);
            parameters.add("Display: " + Build.DISPLAY);
        }
        parameters.add("Android Ver: " + Build.VERSION.SDK_INT);
        if (longInfo)
        {
            parameters.add("Android Release: " + Build.VERSION.RELEASE);
            parameters.add("Android Incremental: " + Build.VERSION.INCREMENTAL);
            parameters.add("Android CodeName: " + Build.VERSION.CODENAME);
        }
        return parameters;
    }

    public static String getAppVersion(ContextWrapper contextWrapper)
    {
        String appVersion = "";
        try
        {
            PackageInfo pInfo = contextWrapper.getPackageManager().getPackageInfo(contextWrapper.getPackageName(), 0);
            appVersion = pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            appVersion = Constants.TH_CLIENT_VERSION_VALUE;
        }
        return appVersion;
    }

    public static String getDeviceName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
        {
            return capitalize(model);
        }
        else
        {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s)
    {
        if (s == null || s.length() == 0)
        {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first))
        {
            return s;
        }
        else
        {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
