package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.BaseAmazonInventoryFetcherHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonInventoryFetcherHolder
    extends BaseAmazonInventoryFetcherHolder<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonInventoryFetcher,
        AmazonException>
    implements THAmazonInventoryFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInventoryFetcherHolder(
            @NotNull Provider<THAmazonInventoryFetcher> thAmazonInventoryFetcherProvider)
    {
        super(thAmazonInventoryFetcherProvider);
    }
    //</editor-fold>
}
