package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
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
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcherHolder(
            @NotNull Provider<THAmazonPurchaseFetcher> thAmazonPurchaseFetcherProvider)
    {
        super(thAmazonPurchaseFetcherProvider);
    }
    //</editor-fold>
}
