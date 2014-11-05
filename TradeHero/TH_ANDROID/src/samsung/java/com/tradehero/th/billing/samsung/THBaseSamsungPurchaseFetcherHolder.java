package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

public class THBaseSamsungPurchaseFetcherHolder
    extends BaseSamsungPurchaseFetcherHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseFetcher,
        SamsungException>
    implements THSamsungPurchaseFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcherHolder(
            @NonNull Provider<THSamsungPurchaseFetcher> thSamsungPurchaseFetcherProvider)
    {
        super(thSamsungPurchaseFetcherProvider);
    }
    //</editor-fold>
}
