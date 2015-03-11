package com.tradehero.th.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import rx.functions.Actions;
import timber.log.Timber;

abstract class GooglePlayMarketUtilBase implements MarketUtil
{
    // Google PlayStore
    public static final String PLAYSTORE_APP_ID = "com.tradehero.th";
    private static final String PLAYSTORE_URL = "https://play.google.com/store/apps/details?id=";
    public static final int REQUEST_CODE_UPDATE_PLAY_SERVICE = 43;

    //<editor-fold desc="Constructors">
    public GooglePlayMarketUtilBase()
    {
        super();
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NonNull Activity activity)
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

    @Override public void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    @Override public String getAppMarketUrl()
    {
        return PLAYSTORE_URL + PLAYSTORE_APP_ID;
    }
}
