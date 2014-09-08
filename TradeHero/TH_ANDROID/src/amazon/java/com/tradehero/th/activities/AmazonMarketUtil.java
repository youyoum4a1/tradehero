package com.tradehero.th.activities;

import android.app.Activity;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AmazonMarketUtil implements MarketUtil
{
    // Amazon Store
    public static final String APP_KEY = "38c0709a111f4d34bac7f2d2343946ca";

    @NotNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public AmazonMarketUtil(@NotNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NotNull Activity activity)
    {
        THToast.show("TODO open Amazon market");
    }

    @Override public void sendToReviewAllOnMarket(@NotNull Activity activity)
    {
        showAppOnMarket(activity);
    }
}
