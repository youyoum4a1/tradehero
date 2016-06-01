package com.ayondo.academy.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;

public class MarketUtil
{
    public static void testMarketValid(@NonNull Activity activity)
    {
        // TODO decide what to do
    }

    public static void showAppOnMarket(@NonNull Activity activity)
    {
        THToast.show("TODO open Samsung market");
    }

    public static void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    public static String getAppMarketUrl()
    {
        return "TODO open Samsung market";
    }
}
