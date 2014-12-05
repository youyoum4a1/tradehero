package com.tradehero.th.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import timber.log.Timber;

public class GooglePlayMarketUtil implements MarketUtil
{
    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=";

    @NonNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public GooglePlayMarketUtil(@NonNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NonNull Activity activity)
    {
        try
        {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=" + PLAYSTORE_APP_ID)));
        }
        catch (ActivityNotFoundException ex)
        {
            try
            {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getAppMarketUrl())));
            }
            catch (Exception e)
            {
                Timber.e(e, "Cannot send to Google Play store");
                alertDialogUtil.popWithNegativeButton(
                        activity,
                        R.string.webview_error_no_browser_for_intent_title,
                        R.string.webview_error_no_browser_for_intent_description,
                        R.string.cancel);
            }
        }
    }

    @Override public void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    @Override public String getAppMarketUrl()
    {
        return PLAYSTORE_URL + PLAYSTORE_APP_ID;
    }
}
