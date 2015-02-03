package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.th.R;
import com.tradehero.th.utils.Constants;

public class ActivityHelper
{
    public static void launchAuthentication(Activity activity)
    {
        presentFromActivity(activity, AuthenticationActivity.class);
        activity.finish();
    }

    public static void launchGuide(Activity activity)
    {
        presentFromActivity(activity, GuideActivity.class);
        activity.finish();
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

    public static void presentFromActivity(Activity fromActivity, Class toActivityClass)
    {
        Intent localIntent = new Intent(fromActivity.getApplicationContext(), toActivityClass);
        fromActivity.startActivity(localIntent);
        fromActivity.overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    public static void launchBrowserDownloadApp(Context context, String url){
        if(TextUtils.isEmpty(url) || context == null){
            return;
        }
        Uri uri = Uri.parse(url);
        Intent gotoWebIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(gotoWebIntent);
    }

    public static void sendFeedback(Context context){
        if(context==null){
            return;
        }
        try {
            Intent data = new Intent(Intent.ACTION_SENDTO);
            data.setData(Uri.parse("mailto:" + Constants.EMAIL_FEEDBACK));
            context.startActivity(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
