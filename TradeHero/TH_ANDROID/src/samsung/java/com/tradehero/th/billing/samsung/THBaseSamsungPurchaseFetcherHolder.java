package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseSamsungPurchaseFetcherHolder
    extends BaseSamsungPurchaseFetcherHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseFetcher,
        SamsungException>
    implements THSamsungPurchaseFetcherHolder
{
    @NotNull protected final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcherHolder(
            @NotNull Provider<THSamsungPurchaseFetcher> thSamsungPurchaseFetcherProvider,
            @NotNull Provider<Activity> activityProvider)
    {
        super(thSamsungPurchaseFetcherProvider);
        this.activityProvider = activityProvider;
    }
    //</editor-fold>
}
