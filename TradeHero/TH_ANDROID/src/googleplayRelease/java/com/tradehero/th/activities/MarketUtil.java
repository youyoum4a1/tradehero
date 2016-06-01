package com.ayondo.academy.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import timber.log.Timber;

public class MarketUtil extends GooglePlayMarketUtilBase
{
    // Google PlayStore
    public static final int REQUEST_CODE_UPDATE_PLAY_SERVICE = 43;

    public static void testMarketValid(@NonNull Activity activity)
    {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS)
        {
            Timber.e(new Exception("GooglePlayServices are not available"),
                    GooglePlayServicesUtil.getErrorString(result));
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                    result,
                    activity,
                    REQUEST_CODE_UPDATE_PLAY_SERVICE,
                    new DialogInterface.OnCancelListener()
                    {
                        @Override public void onCancel(DialogInterface dialog)
                        {
                            Timber.e(new Exception("User cancelled update of GooglePlayServices"), "");
                        }
                    });
            dialog.show();
        }
    }
}
