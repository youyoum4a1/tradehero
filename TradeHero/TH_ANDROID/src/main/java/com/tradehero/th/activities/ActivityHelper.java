package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.tradehero.th.R;
import com.tradehero.th.fragments.chinabuild.data.THSharePreferenceManager;

public class ActivityHelper
{
    public static void launchAuthentication(Context activity)
    {
        Intent localIntent = new Intent(activity, AuthenticationActivity.class);
        localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(localIntent);
        ((Activity) activity).finish();
    }

    public static void launchGuide(Context activity)
    {
        Intent localIntent = new Intent(activity, GuideActivity.class);
        activity.startActivity(localIntent);
        ((Activity) activity).finish();
    }

    public static void launchDashboard(Activity activity,Bundle args)
    {
        presentFromActivity(activity, DashboardActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP,args);
    }


    public static void launchMainActivity(Activity activity)
    {
        THSharePreferenceManager.clearDialogShowedRecord();
        presentFromActivity(activity, MainActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP,new Bundle());
    }

    public static void presentFromActivity(Activity fromActivity, Class toActivityClass, int flags,Bundle args)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        localIntent.putExtras(args);
        localIntent.addFlags(flags);
        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }
}
