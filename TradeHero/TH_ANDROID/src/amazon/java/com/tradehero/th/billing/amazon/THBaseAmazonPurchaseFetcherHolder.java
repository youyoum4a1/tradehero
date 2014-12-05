package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcherHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import javax.inject.Inject;

public class THBaseAmazonPurchaseFetcherHolder
    extends BaseAmazonPurchaseFetcherHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseFetcher,
            AmazonException>
    implements THAmazonPurchaseFetcherHolder
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcherHolder(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.purchasingService = purchasingService;
        this.amazonExceptionFactory = amazonExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonPurchaseFetcher createPurchaseFetcher(int requestCode)
    {
        return new THBaseAmazonPurchaseFetcher(
                requestCode,
                purchasingService,
                amazonExceptionFactory,
                processingPurchaseStringSet);
    }
}
