package com.tradehero.th.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;

public class GooglePlayMarketUtil extends GooglePlayMarketUtilBase
{
    //<editor-fold desc="Constructors">
    @Inject public GooglePlayMarketUtil()
    {
        super();
    }
    //</editor-fold>

    @Override public void testMarketValid(@NonNull Activity activity)
    {
        THToast.show("Not testing if market is valid");
    }
}
