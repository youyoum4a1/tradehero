package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

class THBaseIABPurchaseFetcherHolder
    extends BaseIABPurchaseFetcherHolder<
        IABSKU,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaseFetcher>
    implements THIABPurchaseFetcherHolder
{
    @NotNull protected final Provider<THIABPurchaseFetcher> thBaseIABPurchaseFetcherProvider;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcherHolder(
            @NotNull Provider<THIABPurchaseFetcher> thBaseIABPurchaseFetcherProvider)
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
