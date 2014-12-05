package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonProductIdentifierFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import javax.inject.Inject;

public class THBaseAmazonProductIdentifierFetcherHolder
    extends BaseAmazonProductIdentifierFetcherHolder<
        AmazonSKUListKey,
        AmazonSKU,
        AmazonSKUList,
        THAmazonProductIdentifierFetcher,
        AmazonException>
    implements THAmazonProductIdentifierFetcherHolder
{
    @NonNull protected final AmazonPurchasingService purchasingService;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonProductIdentifierFetcherHolder(
            @NonNull AmazonPurchasingService purchasingService)
    {
        super();
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonProductIdentifierFetcher createSkuFetcher(int requestCode)
    {
        return new THBaseAmazonProductIdentifierFetcher(
                requestCode,
                purchasingService);
    }
}
