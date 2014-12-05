package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseConsumerHolder;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import javax.inject.Inject;

public class THBaseAmazonPurchaseConsumerHolder
    extends BaseAmazonPurchaseConsumerHolder<
            AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase,
            THAmazonPurchaseConsumer>
    implements THAmazonPurchaseConsumerHolder
{
    @NonNull protected final AmazonPurchasingService purchasingService;
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;
    @NonNull protected final THAmazonPurchaseCacheRx thAmazonPurchaseCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseConsumerHolder(
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull THAmazonPurchaseCacheRx thAmazonPurchaseCache)
    {
        super();
        this.purchasingService = purchasingService;
        this.amazonExceptionFactory = amazonExceptionFactory;
        this.thAmazonPurchaseCache = thAmazonPurchaseCache;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonPurchaseConsumer createPurchaseConsumer(int request)
    {
        return new THBaseAmazonPurchaseConsumer(
                request,
                purchasingService,
                amazonExceptionFactory,
                thAmazonPurchaseCache);
    }
}
