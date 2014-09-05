package com.tradehero.th.activities;

import android.app.Activity;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SamsungMarketUtil implements MarketUtil
{

    @NotNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public SamsungMarketUtil(@NotNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NotNull Activity activity)
    {
        THToast.show("TODO open Samsung market");
    }

    @Override public void sendToReviewAllOnMarket(@NotNull Activity activity)
    {
        showAppOnMarket(activity);
    }
}
