package com.tradehero.th.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

public class GooglePlayMarketUtil implements MarketUtil
{
    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";

    @NotNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public GooglePlayMarketUtil(@NotNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NotNull Activity activity)
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
                        Uri.parse("https://play.google.com/store/apps/details?id="
                                + PLAYSTORE_APP_ID)));
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

    @Override public void sendToReviewAllOnMarket(@NotNull Activity activity)
    {
        showAppOnMarket(activity);
    }
}
