package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:28 PM Copyright (c) TradeHero */
public class ActivityHelper
{
    public static void launchAuthentication(Context activity)
    {
        Intent localIntent = new Intent(Application.context(), AuthenticationActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(localIntent);
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
