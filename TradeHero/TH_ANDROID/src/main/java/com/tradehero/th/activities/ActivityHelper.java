package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.R;

public class ActivityHelper
{
    public static void launchAuthentication(Activity activity)
    {
        presentFromActivity(activity, AuthenticationActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static void launchGuide(Context activity)
    {
        Intent localIntent = new Intent(activity, GuideActivity.class);
        activity.startActivity(localIntent);
        ((Activity) activity).finish();
    }

    public static void launchDashboard(Activity activity)
    {
        presentFromActivity(activity, DashboardActivity.class, /* Intent.FLAG_ACTIVITY_NO_HISTORY*/ Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static void presentFromActivity(Activity fromActivity, Class toActivityClass, int flags)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        localIntent.addFlags(flags);
        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
