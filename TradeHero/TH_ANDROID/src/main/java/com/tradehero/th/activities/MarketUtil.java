package com.tradehero.th.activities;

import android.app.Activity;
import android.support.annotation.NonNull;

public interface MarketUtil
{
    void testMarketValid(@NonNull Activity activity);
    void showAppOnMarket(@NonNull Activity activity);
    void sendToReviewAllOnMarket(@NonNull Activity activity);
    String getAppMarketUrl();
}
