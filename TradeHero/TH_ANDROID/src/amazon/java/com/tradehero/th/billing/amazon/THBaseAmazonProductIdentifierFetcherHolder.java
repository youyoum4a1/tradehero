package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonProductIdentifierFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

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
            @NonNull Provider<THAmazonProductIdentifierFetcher> thAmazonProductIdentifierFetcherProvider)
    {
        super(thAmazonProductIdentifierFetcherProvider);
    }
    //</editor-fold>
}
