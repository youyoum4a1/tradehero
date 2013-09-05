package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.th.R;
import com.tradehero.th.application.App;

/** Created with IntelliJ IDEA. User: tho Date: 8/14/13 Time: 6:28 PM Copyright (c) TradeHero */
public class ActivityHelper
{
    public static void doStart(Context activity)
    {
        Intent localIntent = new Intent(App.context(), AuthenticationActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(localIntent);
    }

    public static void goRoot(Activity activity)
    {
        Intent localIntent = new Intent(activity.getApplicationContext(), DashboardActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(localIntent);
        activity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
