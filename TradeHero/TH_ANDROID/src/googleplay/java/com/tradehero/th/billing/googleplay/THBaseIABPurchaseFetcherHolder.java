package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaseFetcherHolder
    extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseFetcher>
    implements THIABPurchaseFetcherHolder
{
    @NonNull protected final Provider<THIABPurchaseFetcher> thBaseIABPurchaseFetcherProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcherHolder(
            @NonNull Provider<THIABPurchaseFetcher> thBaseIABPurchaseFetcherProvider)
    {
        super();
        this.thBaseIABPurchaseFetcherProvider = thBaseIABPurchaseFetcherProvider;
    }
    //</editor-fold>

    @Override protected THIABPurchaseFetcher createPurchaseFetcher()
    {
        return thBaseIABPurchaseFetcherProvider.get();
    }
}
