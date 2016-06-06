package com.androidth.general.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import timber.log.Timber;

abstract class GooglePlayMarketUtilBase
{
    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=";
    public static final int REQUEST_CODE_UPDATE_PLAY_SERVICE = 43;

    public static void showAppOnMarket(@NonNull Activity activity)
    {
        try
        {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=" + PLAYSTORE_APP_ID)));
        } catch (ActivityNotFoundException ex)
        {
            try
            {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getAppMarketUrl())));
            } catch (Exception e)
            {
                Timber.e(e, "Cannot send to Google Play store");
                AlertDialogRxUtil.buildDefault(activity)
                        .setTitle(R.string.webview_error_no_browser_for_intent_title)
                        .setMessage(R.string.webview_error_no_browser_for_intent_description)
                        .setPositiveButton(R.string.cancel)
                        .build()
                        .subscribe(
                                new EmptyAction1<OnDialogClickEvent>(),
                                new EmptyAction1<Throwable>());
            }
        }
    }

    public static void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    public static String getAppMarketUrl()
    {
        return PLAYSTORE_URL + PLAYSTORE_APP_ID;
    }
}
