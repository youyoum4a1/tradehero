package com.tradehero.th.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;

public class SamsungMarketUtil implements MarketUtil
{
    //<editor-fold desc="Constructors">
    @Inject public SamsungMarketUtil()
    {
        super();
    }
    //</editor-fold>

    @Override public void testMarketValid(@NonNull Activity activity)
    {
        // TODO decide what to do
    }

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
