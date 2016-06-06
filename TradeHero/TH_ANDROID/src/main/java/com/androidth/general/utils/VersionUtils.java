package com.androidth.general.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import com.androidth.general.api.users.CurrentUserId;
import java.util.ArrayList;
import java.util.List;

public class VersionUtils
{
    @NonNull public static Intent getSupportEmailIntent(@NonNull Context context, @NonNull CurrentUserId currentUserId)
    {
        return getSupportEmailIntent(context, currentUserId, false);
    }

    @NonNull public static Intent getSupportEmailIntent(@NonNull Context context, @NonNull CurrentUserId currentUserId, boolean longInfo)
    {
        return getSupportEmailIntent(getSupportEmailTraceParameters(context, currentUserId, longInfo));
    }

    @NonNull public static Intent getSupportEmailIntent(@NonNull List<String> infoStrings)
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

    @NonNull public static List<String> getSupportEmailTraceParameters(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            boolean longInfo)
    {
        List<String> parameters = new ArrayList<>();
        parameters.add("TradeHero: " + getAppVersion(context));
        parameters.add("User Id: " + currentUserId.get());
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

    @NonNull public static List<String> getExceptionStringsAndTraceParameters(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull List<Throwable> exceptions)
    {
        List<String> reported = new ArrayList<>();
        for (Throwable exception : exceptions)
        {
            reported.addAll(getExceptionStringsAndTraceParameters(context, currentUserId, exception));
            reported.add("\n\n\n");
        }
        return reported;
    }

    @NonNull public static List<String> getExceptionStringsAndTraceParameters(
            @NonNull Context context,
            @NonNull CurrentUserId currentUserId,
            @NonNull Throwable exception)
    {
        List<String> reported = getExceptionStrings(context, exception);
        reported.addAll(getSupportEmailTraceParameters(context, currentUserId, true));
        return reported;
    }

    @NonNull public static List<String> getExceptionStrings(@NonNull Context context, @NonNull Throwable exception)
    {
        List<String> reported = new ArrayList<>();

        reported.addAll(ExceptionUtils.getElements(exception));
        reported.add("-----");

        return reported;
    }

    @NonNull public static String getAppVersion(@NonNull Context context)
    {
        return getVersionName(context) + "(" + getVersionCode(context) + ")";
    }

    @NonNull public static String getVersionName(@NonNull Context context)
    {
        String v = "";
        try
        {
            v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            if (!Constants.RELEASE)
            {
                int dashIndex = v.indexOf("-");
                if (dashIndex >= 0)
                {
                    v = v.substring(0, dashIndex);
                }
            }
        } catch (PackageManager.NameNotFoundException ignored)
        {
        }
        return v;
    }

    public static int getVersionCode(@NonNull Context context)
    {
        int v = 0;
        try
        {
            v = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException ignored) { }
        return v;
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

    public static void logScreenMeasurements(@NonNull Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
    }

    private static String capitalize(@Nullable String s)
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

    @NonNull public static String getVersionId(@NonNull Context context)
    {
        return getVersionName(context) + "." + getVersionCode(context);
    }
}
