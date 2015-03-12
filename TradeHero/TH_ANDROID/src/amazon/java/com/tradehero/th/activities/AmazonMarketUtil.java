package com.tradehero.th.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;

public class AmazonMarketUtil implements MarketUtil
{
    // Amazon Store
    public static final String APP_KEY = "38c0709a111f4d34bac7f2d2343946ca";

    //<editor-fold desc="Constructors">
    @Inject public AmazonMarketUtil()
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
