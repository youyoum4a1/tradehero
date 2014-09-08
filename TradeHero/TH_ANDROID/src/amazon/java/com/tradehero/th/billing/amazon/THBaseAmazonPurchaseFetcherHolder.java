package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.activities.CurrentActivityHolder;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonPurchaseFetcherHolder
    extends BaseAmazonPurchaseFetcherHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseFetcher,
            AmazonException>
    implements THAmazonPurchaseFetcherHolder
{
    @NotNull protected final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcherHolder(
            @NotNull Provider<THAmazonPurchaseFetcher> thAmazonPurchaseFetcherProvider,
            @NotNull CurrentActivityHolder currentActivityHolder)
    {
        super(thAmazonPurchaseFetcherProvider);
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>
}
