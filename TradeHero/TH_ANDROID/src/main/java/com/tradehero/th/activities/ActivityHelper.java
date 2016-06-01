package com.ayondo.academy.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;

public class ActivityHelper
{
    public static void launchAuthentication(@NonNull Activity activity, @Nullable Uri deepLink)
    {
        presentFromActivity(activity, AuthenticationActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP, deepLink);
    }

    public static void launchDashboard(@NonNull Activity activity, @Nullable Uri deepLink)
    {
        presentFromActivity(activity, DashboardActivity.class, /* Intent.FLAG_ACTIVITY_NO_HISTORY*/ Intent.FLAG_ACTIVITY_CLEAR_TOP, deepLink);
    }

    public static void presentFromActivity(@NonNull Activity fromActivity, @NonNull Class toActivityClass, int flags, @Nullable Uri deepLink)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        localIntent.addFlags(flags);
        if (deepLink != null)
        {
            localIntent.setData(deepLink);
        }
        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
