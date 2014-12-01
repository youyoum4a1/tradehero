package com.tradehero.th.activities;

import android.app.Activity;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class AmazonMarketUtil implements MarketUtil
{
    // Amazon Store
    public static final String APP_KEY = "38c0709a111f4d34bac7f2d2343946ca";

    @NonNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public AmazonMarketUtil(@NonNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NonNull Activity activity)
    {
        THToast.show("TODO open Amazon market");
    }

    @Override public void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    @Override public String getAppMarketUrl()
    {
        return "TODO open Amazon market";
    }
}
