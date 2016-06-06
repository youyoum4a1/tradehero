package com.androidth.general.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.androidth.general.common.utils.THToast;

public class MarketUtil extends GooglePlayMarketUtilBase
{
    public static void testMarketValid(@NonNull Activity activity)
    {
        THToast.show("Not testing if market is valid");
    }
}
