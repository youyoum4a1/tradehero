package com.androidth.general.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;

public class IABServiceResult
{
    @NonNull public final IInAppBillingService billingService;
    public final boolean subscriptionSupported;

    //<editor-fold desc="Constructors">
    public IABServiceResult(
            @NonNull IInAppBillingService billingService,
            boolean subscriptionSupported)
    {
        this.billingService = billingService;
        this.subscriptionSupported = subscriptionSupported;
    }
    //</editor-fold>
}
