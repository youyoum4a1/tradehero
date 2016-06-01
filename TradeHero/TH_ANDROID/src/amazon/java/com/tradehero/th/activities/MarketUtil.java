package com.ayondo.academy.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;

public class MarketUtil
{
    // Amazon Store
    public static final String APP_KEY = "38c0709a111f4d34bac7f2d2343946ca";

    public static void testMarketValid(@NonNull Activity activity)
    {
        // TODO decide what to do
    }

    public static void showAppOnMarket(@NonNull Activity activity)
    {
        THToast.show("TODO open Amazon market");
    }

    public static void sendToReviewAllOnMarket(@NonNull Activity activity)
    {
        showAppOnMarket(activity);
    }

    public static String getAppMarketUrl()
    {
        return "TODO open Amazon market";
    }
}
