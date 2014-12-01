package com.tradehero.th.activities;

import android.app.Activity;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class SamsungMarketUtil implements MarketUtil
{

    @NonNull protected final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public SamsungMarketUtil(@NonNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void showAppOnMarket(@NonNull Activity activity)
    {
        THToast.show("TODO open Samsung market");
    }

    @Override public void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    @Override public String getAppMarketUrl()
    {
        return "TODO open Samsung market";
    }
}
