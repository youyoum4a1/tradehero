package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import com.tradehero.common.utils.THLog;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 4:55 PM To change this template use File | Settings | File Templates. */
public class VersionUtils
{
    public static final String TAG = VersionUtils.class.getSimpleName();

    public static Intent getSupportEmailIntent(Context context)
    {
        return getSupportEmailIntent(context, false);
    }

    public static Intent getSupportEmailIntent(Context context, boolean longInfo)
    {
        return getSupportEmailIntent(getSupportEmailTraceParameters(context, longInfo));
    }

   public static Intent getSupportEmailIntent(List<String> infoStrings)
    {
        String deviceDetails = "\n\n-----\n" +
                StringUtils.join("\n", infoStrings) +
                "\n-----\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - Support");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    public static List<String> getSupportEmailTraceParameters(Context context, boolean longInfo)
    {
        List<String> parameters = new ArrayList<>();
        parameters.add("TradeHero: " + getAppVersion(context));
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

    public static List<String> getExceptionStringsAndTraceParameters(Context context, Exception exception)
    {
        List<String> reported = getExceptionStrings(context, exception);
        reported.addAll(VersionUtils.getSupportEmailTraceParameters(context, true));
        return reported;
    }

    public static List<String> getExceptionStrings(Context context, Exception exception)
    {
        List<String> reported = new ArrayList<>();

        if (exception != null)
        {
            reported.addAll(ExceptionUtils.getElements(exception));
            reported.add("-----");
        }

        return reported;
    }

    public static String getAppVersion(Context context)
    {
        String appVersion = "";
        try
        {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
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

    public static void logScreenMeasurements(Activity activity)
    {

        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = activity.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        THLog.d(TAG, "view " + dpHeight + dpHeight + ", dpWidth " + dpWidth);
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
