package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonProductIdentifierFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonProductIdentifierFetcherHolder
    extends BaseAmazonProductIdentifierFetcherHolder<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        THAmazonProductIdentifierFetcher,
        AmazonException>
    implements THAmazonProductIdentifierFetcherHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonProductIdentifierFetcherHolder(
            @NotNull Provider<THAmazonProductIdentifierFetcher> thAmazonProductIdentifierFetcherProvider)
    {
        super(thAmazonProductIdentifierFetcherProvider);
    }
    //</editor-fold>
}
