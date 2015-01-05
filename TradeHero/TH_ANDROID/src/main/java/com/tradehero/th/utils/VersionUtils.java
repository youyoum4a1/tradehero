package com.tradehero.th.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class VersionUtils
{
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public VersionUtils(@NonNull CurrentUserId currentUserId)
    {
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    public Intent getSupportEmailIntent(Context context)
    {
        return getSupportEmailIntent(context, false);
    }

    public Intent getSupportEmailIntent(Context context, boolean longInfo)
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

    public List<String> getSupportEmailTraceParameters(Context context, boolean longInfo)
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

    public List<String> getExceptionStringsAndTraceParameters(Context context,
            Exception exception)
    {
        List<String> reported = getExceptionStrings(context, exception);
        reported.addAll(getSupportEmailTraceParameters(context, true));
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
        return getVersionName(context) + "(" + getVersionCode(context) + ")";
    }

    public static String getVersionName(Context context)
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
        }
        catch (PackageManager.NameNotFoundException ignored) { }
        return v;
    }

    public static int getVersionCode(Context context)
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

    public static void logScreenMeasurements(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = activity.getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth = outMetrics.widthPixels / density;
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

    public static String getVersionId(Context context)
    {
        return getVersionName(context) + "." + getVersionCode(context);
    }
}
