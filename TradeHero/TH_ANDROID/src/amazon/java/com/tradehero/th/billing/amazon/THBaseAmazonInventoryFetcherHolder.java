package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonInventoryFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import javax.inject.Inject;

public class THBaseAmazonInventoryFetcherHolder
        extends BaseAmazonInventoryFetcherHolder<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonInventoryFetcher,
        AmazonException>
        implements THAmazonInventoryFetcherHolder
{

    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInventoryFetcherHolder(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory)
    {
        super();
        this.purchasingService = purchasingService;
        this.amazonExceptionFactory = amazonExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonInventoryFetcher createInventoryFetcher(int requestCode)
    {
        return new THBaseAmazonInventoryFetcher(
                requestCode,
                purchasingService,
                amazonExceptionFactory);
    }
}
