package com.androidth.general.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.receivers.CustomAirshipReceiver;

public class ActivityHelper
{
    public static void launchAuthentication(@NonNull Activity activity, @Nullable Uri deepLink)
    {
        presentFromActivity(activity, AuthenticationActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP, deepLink, false);
    }

    public static void launchAuthentication(@NonNull Activity activity, @Nullable Uri deepLink, String uaMessage)
    {
        presentFromActivityWithMessage(activity, AuthenticationActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP, deepLink, false, uaMessage);
    }

    public static void launchDashboard(@NonNull Activity activity, @Nullable Uri deepLink)
    {
        presentFromActivity(activity, DashboardActivity.class,
                Intent.FLAG_ACTIVITY_CLEAR_TOP, deepLink, false);
    }

    public static void launchDashboardWithFinish(@NonNull Activity activity, @Nullable Uri deepLink)
    {
        presentFromActivity(activity, DashboardActivity.class,
                Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP,
                deepLink, true);
    }

    public static void launchDashboardWithFinish(@NonNull Activity activity, @Nullable Uri deepLink, String message)
    {
        presentFromActivityWithMessage(activity, DashboardActivity.class,
                Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP,
                deepLink, true, message);
    }


    public static void presentFromActivity(@NonNull Activity fromActivity,
                                           @NonNull Class toActivityClass,
                                           int flags,
                                           @Nullable Uri deepLink,
                                           boolean finishPrevious)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        localIntent.addFlags(flags);
        if (deepLink != null)
        {
            localIntent.setData(deepLink);
        }
        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        if(finishPrevious){
            fromActivity.finish();
        }
    }

    public static void presentFromActivityWithMessage(@NonNull Activity fromActivity,
                                           @NonNull Class toActivityClass,
                                           int flags,
                                           @Nullable Uri deepLink,
                                           boolean finishPrevious,
                                                      String uaMessage)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        localIntent.addFlags(flags);
        if (deepLink != null)
        {
            localIntent.setData(deepLink);
        }

        localIntent.putExtra(CustomAirshipReceiver.MESSAGE, uaMessage);

        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        if(finishPrevious){
            fromActivity.finish();
        }
    }
}
